package me.pugabyte.nexus.models.punishments;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.punishments.Punishment.PunishmentBuilder;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@Data
@Builder
@Entity("punishments")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Punishments extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Punishment> punishments = new ArrayList<>();
	private List<String> ipHistory = new ArrayList<>();

	public static transient final String PREFIX = StringUtils.getPrefix("Justice");
	public static transient final String DISCORD_PREFIX = StringUtils.getDiscordPrefix("Justice");

	public static Punishments of(String name) {
		return of(PlayerUtils.getPlayer(name));
	}

	public static Punishments of(UUID uuid) {
		return of(PlayerUtils.getPlayer(uuid));
	}

	public static Punishments of(PlayerOwnedObject player) {
		return of(player.getUuid());
	}

	public static Punishments of(OfflinePlayer player) {
		return new PunishmentsService().get(player);
	}

	// TODO Other player IP Ban check - service query IP history
	public Optional<Punishment> getAnyActiveBan() {
		return getLastActive(PunishmentType.BAN, PunishmentType.IP_BAN);
	}

	public Optional<Punishment> getActiveBan() {
		return getLastActive(PunishmentType.BAN);
	}

	public Optional<Punishment> getActiveIPBan() {
		return getLastActive(PunishmentType.IP_BAN);
	}

	public Optional<Punishment> getActiveMute() {
		return getLastActive(PunishmentType.MUTE);
	}

	public Optional<Punishment> getActiveFreeze() {
		return getLastActive(PunishmentType.FREEZE);
	}

	public Optional<Punishment> getLastWarn() {
		return getLastActive(PunishmentType.WARN);
	}

	public List<Punishment> getActive(PunishmentType... types) {
		return punishments.stream()
				.filter(punishment -> punishment.isActive() && Arrays.asList(types).contains(punishment.getType()))
				.collect(toList());
	}

	public Optional<Punishment> getLastActive(PunishmentType... types) {
		return getActive(types).stream().max(Comparator.comparing(Punishment::getTimestamp));
	}

	public List<Punishment> getNewWarnings() {
		return getActive(PunishmentType.WARN).stream()
				.filter(punishment -> !punishment.hasBeenReceived())
				.collect(toList());
	}

	public void add(PunishmentBuilder builder) {
		Punishment punishment = builder.uuid(uuid).build();

		if (punishment.getType().isOnlyOneActive())
			deactivatePrevious(punishment);

		punishments.add(punishment);
		punishment.getType().action(punishment);
		punishment.announceStart();

		save();
	}

	private void deactivatePrevious(Punishment punishment) {
		for (Punishment old : getActive(punishment.getType())) {
			old.setReplacedBy(punishment.getPunisher());
			old.setActive(false);
			String typeName = old.getType().name().toLowerCase().replace("_", "-");
			Nerd.of(punishment.getPunisher()).send(PREFIX + "Replacing previous " + typeName + " for &e"
					+ Nickname.of(punishment.getUuid()) + (isNullOrEmpty(old.getReason()) ? "" : "&3: &7" + old.getReason()) + " &3(" + old.getTimeSince() + ")");
		}
	}

	public void remove(Punishment punishment) {
		punishments.remove(punishment);
		punishment.announceEnd();
		punishment.getType().onExpire(punishment);

		save();
	}

	static void broadcast(String message) {
		Chat.broadcastIngame(PREFIX + message);
		Chat.broadcastDiscord(DISCORD_PREFIX + message);
	}

	public List<Punishment> showWarns() {
		List<Punishment> warnings = getNewWarnings();
		if (warnings.isEmpty())
			return warnings;

		// TODO Not sure I like this formatting
		send("&cYou received " + (warnings.size() == 1 ? "a warning" : "multiple warnings") + " from staff:");
		for (Punishment warning : warnings) {
			boolean showTimeSince = warning.getTimestamp().isBefore(LocalDateTime.now().minusMinutes(1));
			send(" &7- &e" + warning.getReason() + (showTimeSince ? " &c(" + warning.getTimeSince() + ")" : ""));

			String message = "&e" + getName() + " &chas received their warning for &7" + warning.getReason();

			JsonBuilder ingame = json(PREFIX + message)
					.hover("&eClick for more information")
					.command("/history " + getName());

			Chat.broadcastIngame(ingame, StaticChannel.STAFF);
			Chat.broadcastDiscord(DISCORD_PREFIX + stripColor(message), StaticChannel.STAFF);
		}
		send("");
		send("&cPlease make sure to read the /rules to avoid future punishments");
		return warnings;
	}

	public void tryShowWarns() {
		if (!isOnline())
			return;

		List<Punishment> warnings = showWarns();

		// Try to be more sure they actually saw the warning
		Tasks.wait(Time.SECOND.x(5), () -> {
			if (!isOnline())
				return;

			for (Punishment warning : warnings)
				warning.received();

			save();
		});
	}

	private void save() {
		new PunishmentsService().save(this);
	}

}
