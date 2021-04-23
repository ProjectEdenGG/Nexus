package me.pugabyte.nexus.models.punishments;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.punishments.Punishment.PunishmentBuilder;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static me.pugabyte.nexus.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@Data
@Builder
@Entity(value = "punishments", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Punishments implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Punishment> punishments = new ArrayList<>();
	private List<IPHistoryEntry> ipHistory = new ArrayList<>();

	public static transient final String PREFIX = StringUtils.getPrefix("Justice");
	public static transient final String DISCORD_PREFIX = StringUtils.getDiscordPrefix("Justice");

	public static Punishments of(String name) {
		return of(PlayerUtils.getPlayer(name));
	}

	public static Punishments of(PlayerOwnedObject player) {
		return of(player.getUuid());
	}

	public static Punishments of(OfflinePlayer player) {
		return of(player.getUniqueId());
	}

	public static Punishments of(UUID uuid) {
		return new PunishmentsService().get(uuid);
	}

	public Punishment getById(UUID id) {
		return punishments.stream().filter(punishment -> punishment.getId().equals(id)).findFirst().orElse(null);
	}

	public boolean hasHistory() {
		return !punishments.isEmpty();
	}

	// TODO Other player IP Ban check - service query IP history
	public Optional<Punishment> getAnyActiveBan() {
		return getMostRecentActive(PunishmentType.BAN, PunishmentType.ALT_BAN);
	}

	public Optional<Punishment> getActiveBan() {
		return getMostRecentActive(PunishmentType.BAN);
	}

	public Optional<Punishment> getActiveAltBan() {
		return getMostRecentActive(PunishmentType.ALT_BAN);
	}

	public Optional<Punishment> getActiveMute() {
		return getMostRecentActive(PunishmentType.MUTE);
	}

	public Optional<Punishment> getActiveFreeze() {
		return getMostRecentActive(PunishmentType.FREEZE);
	}

	public Optional<Punishment> getActiveWatchlist() {
		return getMostRecentActive(PunishmentType.WATCHLIST);
	}

	public Optional<Punishment> getLastWarn() {
		return getMostRecentActive(PunishmentType.WARN);
	}

	public List<Punishment> getActive(PunishmentType... types) {
		return punishments.stream()
				.filter(punishment -> punishment.isActive() && Arrays.asList(types).contains(punishment.getType()))
				.collect(toList());
	}

	public Optional<Punishment> getMostRecentActive(PunishmentType... types) {
		return getMostRecent(getActive(types));
	}

	public Optional<Punishment> getMostRecent() {
		return getMostRecent(punishments);
	}

	public Optional<Punishment> getCooldown(UUID punisher) {
		Optional<Punishment> mostRecent = getMostRecent();
		if (!mostRecent.isPresent())
			return Optional.empty();

		boolean recent = mostRecent.get().getTimestamp().isAfter(LocalDateTime.now().minusSeconds(30));
		boolean samePunisher = mostRecent.get().getPunisher().equals(punisher);

		if (!(recent && !samePunisher))
			return Optional.empty();

		return mostRecent;
	}

	public Optional<Punishment> getMostRecent(List<Punishment> punishments) {
		return punishments.stream().max(Comparator.comparing(Punishment::getTimestamp));
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

	public void addImport(PunishmentBuilder builder) {
		Punishment punishment = builder.uuid(uuid).build();

		if (punishment.getType().isOnlyOneActive())
			deactivatePrevious(punishment);

		punishments.add(punishment);

		save();
	}

	private void deactivatePrevious(Punishment punishment) {
		for (Punishment old : getActive(punishment.getType())) {
			old.setReplacedBy(punishment.getId());
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
			boolean recent = warning.getTimestamp().isAfter(LocalDateTime.now().minusMinutes(1));
			String reason = " &7- &e" + warning.getReason();

			if (recent) {
				send(reason);
				continue;
			}

			send(reason + " &c(" + warning.getTimeSince() + ")");

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

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IPHistoryEntry {
		private String ip;
		private LocalDateTime timestamp;
	}

	public Optional<IPHistoryEntry> getIpHistoryEntry(String ip) {
		return ipHistory.stream().filter(entry -> entry.getIp().equals(ip)).findFirst();
	}

	public boolean hasIp(String ip) {
		return getIpHistoryEntry(ip).isPresent();
	}

	public void logIp(String ip) {
		logIp(ip, LocalDateTime.now());
	}

	public void logIp(String ip, LocalDateTime timestamp) {
		if (!hasIp(ip))
			ipHistory.add(new IPHistoryEntry(ip, timestamp));
	}

	public List<String> getIps() {
		return ipHistory.stream().map(IPHistoryEntry::getIp).collect(toList());
	}

	@NotNull
	public Set<UUID> getAlts() {
		final PunishmentsService service = new PunishmentsService();
		Set<UUID> alts = new HashSet<UUID>() {{ add(uuid); }};
		Set<UUID> newMatches = new HashSet<>(alts);

		int size = 1;
		while (true) {
			Set<UUID> toSearch = new HashSet<>(newMatches);
			newMatches.clear();

			List<Punishments> players = toSearch.stream().map(Punishments::of).collect(toList());
			newMatches.addAll(service.getAlts(players).stream()
					.map(Punishments::getUuid)
					.collect(toList()));

			newMatches.removeAll(alts);
			alts.addAll(newMatches);

			if (alts.size() == size)
				break;
			size = alts.size();
		}

		return alts;
	}

	public void sendAltsMessage(Consumer<JsonBuilder> sender) {
		sendAltsMessage(sender, null);
	}

	public void sendAltsMessage(Consumer<JsonBuilder> sender, Runnable ifNull) {
		JsonBuilder altsMessage = getAltsMessage();
		if (altsMessage != null)
			sender.accept(new JsonBuilder(PREFIX + "Alts of &e" + getNickname()).newline().next(altsMessage));
		else if (ifNull != null)
			ifNull.run();
	}

	public JsonBuilder getAltsMessage() {
		Set<UUID> alts = getAlts();
		if (alts.size() == 1)
			return null;

		JsonBuilder json = new JsonBuilder();

		alts.stream().map(Punishments::of).forEach(alt -> {
			ChatColor color = ChatColor.GRAY;
			String description = "Offline";

			if (alt.isOnline()) {
				color = ChatColor.GREEN;
				description = "Online";
			}

			for (PunishmentType type : Arrays.asList(PunishmentType.WATCHLIST, PunishmentType.FREEZE, PunishmentType.MUTE, PunishmentType.ALT_BAN, PunishmentType.BAN))
				if (alt.getMostRecentActive(type).isPresent()) {
					color = type.getChatColor();
					description = type.getPastTense();
				}

			if (json.isInitialized())
				json.next("&f, ");
			else
				json.initialize();

			json.group().next(color + alt.getNickname()).hover(color + camelCase(description)).group();
		});

		return json;
	}

	void save() {
		new PunishmentsService().save(this);
	}

}
