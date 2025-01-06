package gg.projecteden.nexus.models.reminders;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.models.voter.VoteSite;
import gg.projecteden.nexus.models.voter.Voter;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.models.wallsofgrace.WallsOfGrace;
import gg.projecteden.nexus.models.wallsofgrace.WallsOfGraceService;
import gg.projecteden.nexus.utils.*;
import lombok.*;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SerializableAs("ReminderConfig")
public class ReminderConfig {
	private List<Reminder> reminders = new ArrayList<>();

	public Optional<Reminder> findRequestMatch(String id) {
		return reminders.stream()
				.filter(_request -> _request.getId().equalsIgnoreCase(id))
				.findFirst();
	}

	public Reminder getRandomReminder() {
		return RandomUtils.randomElement(getReminders());
	}

	public List<Reminder> getAll() {
		return reminders;
	}

	public List<Reminder> getReminders() {
		return reminders.stream().filter(Reminder::isReminder).collect(Collectors.toList());
	}

	public List<Reminder> getMotds() {
		return reminders.stream().filter(Reminder::isMotd).collect(Collectors.toList());
	}

	public List<Reminder> getReminders(Player player) {
		return getReminders().stream().filter(reminder -> reminder.test(player)).collect(Collectors.toList());
	}

	public List<Reminder> getMotds(Player player) {
		return getMotds().stream().filter(reminder -> reminder.test(player)).collect(Collectors.toList());
	}

	public void add(Reminder reminder) {
		if (findRequestMatch(reminder.getId()).isPresent())
			throw new InvalidInputException("An reminder with id &e" + reminder.getId() + " &calready exists");

		reminders.add(reminder);
	}

	public void remove(String id) {
		if (findRequestMatch(id).isEmpty())
			throw new InvalidInputException("Reminder with id &e" + id + " &cnot found");

		reminders.removeIf(reminder -> reminder.getId().equalsIgnoreCase(id));
	}

	public void showMotd(Player player) {
		player.sendMessage("§3 §6 §3 §6 §3 §6 §e  §3 §6 §3 §6 §3 §6 §d"); // disable voxelmap radar

		Tasks.waitAsync(TickTime.SECOND, () -> {
			if (!player.isOnline())
				return;

			List<Reminder> motds = getMotds(player);
			if (motds.isEmpty())
				return;

			player.sendMessage("");
			motds.forEach(motd -> motd.send(player));
			player.sendMessage("");
		});
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@SerializableAs("Reminder")
	public static class Reminder {
		private String id;
		private String text;
		private String command;
		private String suggest;
		private String url;
		@Builder.Default
		private List<String> hover = new ArrayList<>();
		@Builder.Default
		private boolean enabled = true;
		@Builder.Default
		private Set<String> showPermissions = new HashSet<>();
		@Builder.Default
		private Set<String> hidePermissions = new HashSet<>();
		private LocalDateTime startTime;
		private LocalDateTime endTime;
		private Reminder.ReminderCondition condition;

		private boolean motd;

		@Getter
		private static final String PREFIX = "&8&l[&b⚡&8&l] &7";

		public static final List<Object> HEADERS = List.of(
			"id",
			"text",
			"command",
			"suggest",
			"url",
			"hover",
			"enabled",
			"showPermissions",
			"hidePermissions",
			"startTime",
			"endTime",
			"condition"
		);

		private static ReminderCondition asReminderCondition(Iterator<Object> iterator, String id) {
			String condition = GoogleUtils.SheetsUtils.asTrimmedString(iterator);
			try {
				return condition != null ? Reminder.ReminderCondition.valueOf(condition) : null;
			} catch (IllegalArgumentException ex) {
				Nexus.log("Reminder Condition invalid for " + id + ": " + condition);
				return null;
			}
		}

		public static Reminder deserialize(List<Object> row, boolean motd) {
			final Iterator<Object> iterator = row.iterator();
			final ReminderBuilder builder = Reminder.builder();

			builder
				.id(GoogleUtils.SheetsUtils.asTrimmedString(iterator))
				.text(GoogleUtils.SheetsUtils.asTrimmedString(iterator))
				.command(GoogleUtils.SheetsUtils.asTrimmedString(iterator))
				.suggest(GoogleUtils.SheetsUtils.asString(iterator))
				.url(GoogleUtils.SheetsUtils.asTrimmedString(iterator))
				.hover(GoogleUtils.SheetsUtils.asStringArrayList(iterator))
				.enabled(GoogleUtils.SheetsUtils.asBoolean(iterator, true))
				.showPermissions(GoogleUtils.SheetsUtils.asStringLinkedHashSet(iterator))
				.hidePermissions(GoogleUtils.SheetsUtils.asStringLinkedHashSet(iterator))
				.startTime(GoogleUtils.SheetsUtils.asLocalDateTime(iterator))
				.endTime(GoogleUtils.SheetsUtils.asLocalDateTime(iterator))
				.condition(asReminderCondition(iterator, builder.id))
				.motd(motd);

			return builder.build();
		}

		public List<Object> serialize() {
			return List.of(
				GoogleUtils.SheetsUtils.valueOf(id),
				GoogleUtils.SheetsUtils.valueOf(text),
				GoogleUtils.SheetsUtils.valueOf(command),
				GoogleUtils.SheetsUtils.valueOf(suggest),
				GoogleUtils.SheetsUtils.valueOf(url),
				GoogleUtils.SheetsUtils.valueOf(hover),
				GoogleUtils.SheetsUtils.valueOf(enabled),
				GoogleUtils.SheetsUtils.valueOf(showPermissions),
				GoogleUtils.SheetsUtils.valueOf(hidePermissions),
				GoogleUtils.SheetsUtils.valueOf(startTime == null ? null : startTime.toLocalDate()),
				GoogleUtils.SheetsUtils.valueOf(startTime == null ? null : startTime.toLocalTime()),
				GoogleUtils.SheetsUtils.valueOf(endTime == null ? null : endTime.toLocalDate()),
				GoogleUtils.SheetsUtils.valueOf(endTime == null ? null : endTime.toLocalTime()),
				GoogleUtils.SheetsUtils.valueOf(condition)
			);
		}

		public void send(Player player) {
			if (motd) {
				PlayerUtils.send(player, getJson());
			} else {
				if (MuteMenuUser.hasMuted(player, MuteMenuItem.REMINDERS))
					return;

				PlayerUtils.send(player, "");
				PlayerUtils.send(player, getJson());
				PlayerUtils.send(player, "");
			}
		}

		@NotNull
		private JsonBuilder getJson() {
			JsonBuilder json = new JsonBuilder(PREFIX + text);
			if (!Nullables.isNullOrEmpty(command)) json.command(command);
			if (!Nullables.isNullOrEmpty(suggest)) json.suggest(suggest);
			if (!Nullables.isNullOrEmpty(url)) json.url(url);
			if (!hover.isEmpty()) json.hover(hover);
			return json;
		}

		public boolean test(Player player) {
			if (!enabled)
				return false;

			if (!showPermissions.isEmpty()) {
				boolean canSee = false;
				for (String showPermission : showPermissions)
					if (player.hasPermission(showPermission)) {
						canSee = true;
						break;
					}

				if (!canSee)
					return false;
			}

			if (!hidePermissions.isEmpty()) {
				boolean canHide = false;
				for (String hidePermission : hidePermissions)
					if (player.hasPermission(hidePermission)) {
						canHide = true;
						break;
					}

				if (canHide)
					return false;
			}

			if (startTime != null && startTime.isAfter(LocalDateTime.now()))
				return false;

			if (endTime != null && endTime.isBefore(LocalDateTime.now()))
				return false;

			if (condition != null && !condition.test(player))
				return false;

			return true;
		}

		public boolean isReminder() {
			return !motd;
		}

		// Return true if you want to show the announcement
		@AllArgsConstructor
		public enum ReminderCondition {
			VOTE(player -> {
				Voter voter = new VoterService().get(player);
				return voter.getActiveVotes().size() < VoteSite.getActiveSites().size() - 2;
			}),
			DISCORD_LINK(player -> {
				DiscordUser user = new DiscordUserService().get(player);
				return user.getUserId() == null;
			}),
			WALLS_OF_GRACE(player -> {
				WallsOfGrace wallsOfGrace = new WallsOfGraceService().get(player);
				return wallsOfGrace.get(1) == null && wallsOfGrace.get(2) == null;
			});

			@Getter
			private final Predicate<Player> condition;

			public boolean test(Player player) {
				return condition.test(player);
			}
		}

	}

}
