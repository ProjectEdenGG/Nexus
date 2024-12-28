package gg.projecteden.nexus.models.punishments;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.punishments.Punishment.PunishmentBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.*;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.features.justice.Justice.PREFIX;

@Data
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

	public static Punishments of(String name) {
		return of(PlayerUtils.getPlayer(name));
	}

	public static Punishments of(HasUniqueId player) {
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
				.collect(Collectors.toList());
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
				.collect(Collectors.toList());
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
			Nerd.of(punishment.getPunisher()).sendMessage(PREFIX + "Replacing previous " + typeName + " for &e"
					+ Nickname.of(punishment.getUuid()) + (Nullables.isNullOrEmpty(old.getReason()) ? "" : "&3: &7" + old.getReason()) + " &3(" + old.getTimeSince() + ")");
		}
	}

	public void remove(Punishment punishment) {
		punishments.remove(punishment);
		if (punishment.isActive())
			punishment.announceEnd();
		punishment.getType().onExpire(punishment);

		save();
	}

	public static void broadcast(String message) {
		Broadcast.log().prefix("Justice").message(message).send();
	}

	public List<Punishment> showWarns() {
		List<Punishment> warnings = getNewWarnings();
		if (warnings.isEmpty())
			return warnings;

		sendMessage("&cYou received " + (warnings.size() == 1 ? "a warning" : "multiple warnings") + " from staff:");
		for (Punishment warning : warnings) {
			boolean recent = warning.getTimestamp().isAfter(LocalDateTime.now().minusMinutes(1));
			String reason = " &7- &e" + warning.getReason();

			if (recent) {
				sendMessage(reason);
				continue;
			}

			sendMessage(reason + " &c(" + warning.getTimeSince() + ")");

			String message = "&e" + getName() + " &chas received their warning for &7" + warning.getReason();

			JsonBuilder ingame = json(message)
					.hover("&eClick for more information")
					.command("/history " + getName());

			Broadcast.staff().prefix("Justice").message(ingame).send();
		}
		sendMessage("");
		sendMessage("&cPlease make sure to read the /rules to avoid future punishments");
		return warnings;
	}

	public void tryShowWarns() {
		if (!isOnline())
			return;

		List<Punishment> warnings = showWarns();

		// Try to be more sure they actually saw the warning
		Tasks.wait(TickTime.SECOND.x(5), () -> {
			if (!isOnline())
				return;

			for (Punishment warning : warnings)
				warning.received();

			save();
		});
	}

	public Punishment getPunishment(UUID id) {
		return punishments.stream()
				.filter(punishment -> punishment.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new InvalidInputException("Punishment not found"));
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
		return ipHistory.stream().map(IPHistoryEntry::getIp).collect(Collectors.toList());
	}

	@NotNull
	public Set<UUID> getAlts() {
		final PunishmentsService service = new PunishmentsService();
		Set<UUID> alts = new HashSet<>() {{
			add(uuid);
		}};
		Set<UUID> newMatches = new HashSet<>(alts);

		int size = 1;
		while (true) {
			Set<UUID> toSearch = new HashSet<>(newMatches);
			newMatches.clear();

			List<Punishments> players = toSearch.stream().map(Punishments::of).collect(Collectors.toList());
			newMatches.addAll(service.getAlts(players).stream()
					.map(Punishments::getUuid)
					.toList());

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

		JsonBuilder message = new JsonBuilder(PREFIX + "Alts of &e" + getNickname() + " ");
		if (!getName().equals(getNickname()))
			message.hover("&3Real Name: &e" + getName()).group();

		if (altsMessage != null)
			sender.accept(message.next(altsMessage));
		else if (ifNull != null)
			ifNull.run();
	}

	public JsonBuilder getAltsMessage() {
		Set<UUID> alts = getAlts();
		if (alts.size() == 1)
			return null;

		JsonBuilder json = new JsonBuilder();

		alts.stream().map(Punishments::of).forEach(alt -> {
			if (alt.getUuid().equals(this.getUuid()))
				return;

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

			List<String> hover = new ArrayList<>();
			hover.add(color + StringUtils.camelCase(description));
			if (!alt.getName().equals(alt.getNickname()))
				hover.add("&3Real Name: &e" + alt.getName());

			json.group().content(alt.getNickname()).color(color).hover(hover).group();
		});

		return json;
	}

	void save() {
		new PunishmentsService().save(this);
	}

}
