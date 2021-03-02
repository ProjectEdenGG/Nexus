package me.pugabyte.nexus.features.commands;

import joptsimple.internal.Strings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.commands.AnnouncementsCommand.AnnouncementConfig.Announcement;
import me.pugabyte.nexus.features.commands.AnnouncementsCommand.AnnouncementConfig.Announcement.AnnouncementCondition;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocalDateTimeConverter;
import me.pugabyte.nexus.models.discord.DiscordService;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.vote.VoteService;
import me.pugabyte.nexus.models.vote.VoteSite;
import me.pugabyte.nexus.models.vote.Voter;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.ellipsis;
import static me.pugabyte.nexus.utils.StringUtils.shortDateTimeFormat;

@NoArgsConstructor
@Aliases("announcement")
@Permission("group.seniorstaff")
public class AnnouncementsCommand extends CustomCommand implements Listener {
	static {
		ConfigurationSerialization.registerClass(AnnouncementConfig.class, "AnnouncementConfig");
		ConfigurationSerialization.registerClass(Announcement.class, "Announcement");
	}

	private static final File file = Nexus.getFile("announcements.yml");
	private static YamlConfiguration yaml;
	private static AnnouncementConfig config;

	static {
		load();
	}

	public AnnouncementsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private void save() {
		try {
			yaml.set("config", config);
			yaml.save(file);
		} catch (Exception ex) {
			Nexus.severe("An error occurred while trying to write announcements configuration file: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void saveAndEdit(Announcement announcement) {
		save();
		edit(announcement);
	}

	@Path("reload")
	void reload() {
		load();
		send(PREFIX + "Reload complete");
	}

	private static void load() {
		yaml = YamlConfiguration.loadConfiguration(file);
		config = (AnnouncementConfig) yaml.get("config", new AnnouncementConfig());
		if (config == null) config = new AnnouncementConfig();
	}

	@Path("create <id> <text...>")
	void create(String id, String text) {
		config.add(Announcement.builder().id(id).text(text).build());
		save();
		send(PREFIX + "Announcement &e" + id + " &3created");
	}

	@Confirm
	@Path("delete <id>")
	void delete(Announcement announcement) {
		config.remove(announcement.getId());
		save();
		send(PREFIX + "Announcement &e" + announcement.getId() + " &cdeleted");
	}

	@Path("edit <id>")
	void edit(Announcement announcement) {
		send(json(PREFIX + "Edit announcement &e" + announcement.getId() + " &f &f ").group()
				.next("&6⟳").hover("&6Refresh").command("/announcements edit " + announcement.getId()).group()
				.next(" ").group()
				.next("&c✖").hover("&cDelete").command("/announcements delete " + announcement.getId()));

		line();
		send(json(" &3Text: &7" + announcement.getText()).hover("&3Click to edit").suggest("/announcements edit text " + announcement.getId() + " " + announcement.getText()));
		line();
		send(json(" &3Hover: &7" + announcement.getHover()).hover("&3Click to edit").suggest("/announcements edit hover " + announcement.getId() + " " + (announcement.getHover() == null ? "" : announcement.getHover())));
		send(json(" &3Command: &7" + announcement.getCommand()).hover("&3Click to edit").suggest("/announcements edit command " + announcement.getId() + " " + (announcement.getCommand() == null ? "" : announcement.getCommand())));
		send(json(" &3Suggest: &7" + announcement.getSuggest()).hover("&3Click to edit").suggest("/announcements edit suggest " + announcement.getId() + " " + (announcement.getSuggest() == null ? "" : announcement.getSuggest())));
		line();

		if (announcement.isEnabled())
			send(json(" " + StringUtils.CHECK + " Enabled").hover("&3Click to &cdisable").command("/announcements disable " + announcement.getId()));
		else
			send(json(" " + StringUtils.X + " Disabled").hover("&3Click to &aenable").command("/announcements enable " + announcement.getId()));

		if (announcement.isMotd())
			send(json(" &3Type: &eMOTD").hover("&3Click to toggle").command("/announcements edit motd " + announcement.getId() + " false"));
		else
			send(json(" &3Type: &eAnnouncement").hover("&3Click to toggle").command("/announcements edit motd " + announcement.getId() + " true"));

		send(json(" &3Show permissions ").group().next("&a[+]").hover("&aAdd permission").suggest("/announcements edit showPermissions add " + announcement.getId() + " "));
		showPermissionsEdit(announcement, announcement.getShowPermissions(), "showPermissions");

		send(json(" &3Hide permissions ").group().next("&a[+]").hover("&aAdd permission").suggest("/announcements edit hidePermissions add " + announcement.getId() + " "));
		showPermissionsEdit(announcement, announcement.getHidePermissions(), "hidePermissions");

		send(json(" &3Start time: &e" + (announcement.getStartTime() == null ? "None" : shortDateTimeFormat(announcement.getStartTime())))
				.hover("&3Click to edit").suggest("/announcements edit startTime " + announcement.getId() + " YYYY-MM-DDTHH:MM:SS"));
		send(json(" &3End time: &e" + (announcement.getEndTime() == null ? "None" : shortDateTimeFormat(announcement.getEndTime())))
				.hover("&3Click to edit").suggest("/announcements edit endTime " + announcement.getId() + " YYYY-MM-DDTHH:MM:SS"));

		send(json(" &3Condition: &e" + (announcement.getCondition() == null ? "None" : camelCase(announcement.getCondition())))
				.hover("&3Click to edit").suggest("/announcements edit condition " + announcement.getId() + " "));
		line();
	}

	private void showPermissionsEdit(Announcement announcement, Set<String> permissions, String command) {
		if (permissions.isEmpty())
			send("   &cNone");
		else
			for (String permission : permissions)
				send(json("   &c[-]").hover("&cRemove permission").command("/announcements edit " + command + " remove " + announcement.getId() + " " + permission).group().next(" &e" + permission));
	}

	@Path("enable <id>")
	void enable(Announcement announcement) {
		announcement.setEnabled(true);
		saveAndEdit(announcement);
	}

	@Path("disable <id>")
	void disable(Announcement announcement) {
		announcement.setEnabled(false);
		saveAndEdit(announcement);
	}

	@Path("edit id <id> <newId>")
	void editId(Announcement announcement, String newId) {
		announcement.setId(newId);
		saveAndEdit(announcement);
	}

	@Path("edit text <id> <text...>")
	void editText(Announcement announcement, String text) {
		announcement.setText(text);
		saveAndEdit(announcement);
	}

	@Path("edit hover <id> <text...>")
	void editHover(Announcement announcement, String hover) {
		announcement.setHover(hover);
		saveAndEdit(announcement);
	}

	@Path("edit command <id> <text...>")
	void editCommand(Announcement announcement, String command) {
		announcement.setCommand(command);
		saveAndEdit(announcement);
	}

	@Path("edit suggest <id> <text...>")
	void editSuggest(Announcement announcement, String suggest) {
		announcement.setSuggest(suggest);
		saveAndEdit(announcement);
	}

	@Path("edit motd <id> [enable]")
	void editMotd(Announcement announcement, Boolean enable) {
		if (enable == null)
			enable = !announcement.isMotd();
		announcement.setMotd(enable);
		saveAndEdit(announcement);
	}

	@Path("edit showPermissions add <id> <permission(s)>")
	void editShowPermissionsAdd(Announcement announcement, @Arg(type = String.class) List<String> permissions) {
		announcement.getShowPermissions().addAll(permissions);
		saveAndEdit(announcement);
	}

	@Path("edit showPermissions remove <id> <permission(s)>")
	void editShowPermissionsRemove(Announcement announcement, @Arg(type = String.class) List<String> permissions) {
		announcement.getShowPermissions().removeAll(permissions);
		saveAndEdit(announcement);
	}

	@Path("edit hidePermissions add <id> <permission(s)>")
	void editHidePermissionsAdd(Announcement announcement, @Arg(type = String.class) List<String> permissions) {
		announcement.getHidePermissions().addAll(permissions);
		saveAndEdit(announcement);
	}

	@Path("edit hidePermissions remove <id> <permission(s)>")
	void editHidePermissionsRemove(Announcement announcement, @Arg(type = String.class) List<String> permissions) {
		announcement.getHidePermissions().removeAll(permissions);
		saveAndEdit(announcement);
	}

	@Path("edit startTime <id> [time]")
	void editStartTime(Announcement announcement, LocalDateTime startTime) {
		announcement.setStartTime(startTime);
		saveAndEdit(announcement);
	}

	@Path("edit endTime <id> [time]")
	void editEndTime(Announcement announcement, LocalDateTime endTime) {
		announcement.setEndTime(endTime);
		saveAndEdit(announcement);
	}

	@Path("edit condition <id> [condition]")
	void editCondition(Announcement announcement, AnnouncementCondition condition) {
		announcement.setCondition(condition);
		saveAndEdit(announcement);
	}

	@Path("list [page]")
	void list(@Arg("1") int page) {
		if (config.getAllAnnouncements().isEmpty())
			error("No announcements have been created");

		send(PREFIX + "Announcements");
		BiFunction<Announcement, Integer, JsonBuilder> formatter = (announcement, index) ->
				json("&3" + (index + 1) + " " + (announcement.isMotd() ? "&6➤" : "&b⚡") + " &e" + announcement.getId() + " &7- " + ellipsis(announcement.getText(), 50))
						.addHover("&3Type: &e" + (announcement.isMotd() ? "MOTD" : "Announcement"))
						.addHover("&7" + announcement.getText())
						.command("/announcements edit " + announcement.getId());
		paginate(config.getAllAnnouncements(), formatter, "/announcements list", page);
	}

	@Path("test <player> <announcement>")
	void testCriteria(Player player, Announcement announcement) {
		if (announcement.getCondition() == null)
			error("Announcement &e" + announcement.getId() + " &cdoes not have a condition so players will always receive it");

		send(PREFIX + player.getName() + " &ewould" + (announcement.getCondition().test(player) ? "" : " not")
				+ " &3receive the &e" + camelCase(announcement.getCondition()) + " &3announcement");
	}

	@ConverterFor(Announcement.class)
	Announcement convertToAnnouncement(String value) {
		return config.findRequestMatch(value).orElseThrow(() -> new InvalidInputException("Announcement &e" + value +" &cnot found"));
	}

	@TabCompleterFor(Announcement.class)
	List<String> tabCompleteAnnouncement(String filter) {
		return config.getAllAnnouncements().stream()
				.map(Announcement::getId)
				.filter(request -> request.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

	private static final int interval = Time.SECOND.x(30);

	static {
		Tasks.repeatAsync(interval, interval, () -> {
			if (true) return;

			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!PlayerUtils.isPuga(player))
					continue;

				Utils.attempt(100, () -> {
					Announcement announcement = config.getRandomAnnouncement();

					if (!announcement.test(player))
						return false;

					announcement.send(player);
					return true;
				});
			}
		});
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		for (Announcement motd : config.getMotds())
			if (motd.test(event.getPlayer()))
				motd.send(event.getPlayer());
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@SerializableAs("AnnouncementConfig")
	public static class AnnouncementConfig implements ConfigurationSerializable {
		private List<Announcement> announcements = new ArrayList<>();

		public AnnouncementConfig(Map<String, Object> map) {
			this.announcements = (List<Announcement>) map.getOrDefault("announcements", announcements);
		}

		@Override
		public Map<String, Object> serialize() {
			return new LinkedHashMap<String, Object>() {{
				put("announcements", announcements);
			}};
		}

		public Optional<Announcement> findRequestMatch(String id) {
			return announcements.stream()
					.filter(_request -> _request.getId().equalsIgnoreCase(id))
					.findFirst();
		}

		public Announcement getRandomAnnouncement() {
			return RandomUtils.randomElement(getAnnouncements());
		}

		public List<Announcement> getAllAnnouncements() {
			return announcements;
		}

		public List<Announcement> getAnnouncements() {
			return announcements.stream().filter(announcement -> !announcement.isMotd()).collect(Collectors.toList());
		}

		public List<Announcement> getMotds() {
			return announcements.stream().filter(Announcement::isMotd).collect(Collectors.toList());
		}

		public void add(Announcement announcement) {
			if (findRequestMatch(announcement.getId()).isPresent())
				throw new InvalidInputException("An announcement with id &e" + announcement.getId() + " &calready exists");

			announcements.add(announcement);
		}

		public void remove(String id) {
			if (!findRequestMatch(id).isPresent())
				throw new InvalidInputException("Announcement with id &e" + id + " &cnot found");

			announcements.removeIf(announcement -> announcement.getId().equalsIgnoreCase(id));
		}

		@Data
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		@SerializableAs("Announcement")
		public static class Announcement implements ConfigurationSerializable {
			private String id;
			private String text;
			private String hover;
			private String command;
			private String suggest;
			@Builder.Default
			private boolean enabled = true;
			private boolean motd;
			@Builder.Default
			private Set<String> showPermissions = new HashSet<>();
			@Builder.Default
			private Set<String> hidePermissions = new HashSet<>();
			private LocalDateTime startTime;
			private LocalDateTime endTime;
			private Announcement.AnnouncementCondition condition;

			@Getter
			private static final String PREFIX = "&8&l[&b⚡&8&l] &7";

			public Announcement(Map<String, Object> map) {
				this.id = (String) map.getOrDefault("id", id);
				this.text = (String) map.getOrDefault("text", text);
				this.hover = (String) map.getOrDefault("hover", hover);
				this.command = (String) map.getOrDefault("command", command);
				this.suggest = (String) map.getOrDefault("suggest", suggest);
				this.enabled = map.get("enabled") != null ? (boolean) map.get("enabled") : enabled;
				this.motd = map.get("motd") != null ? (boolean) map.get("motd") : motd;
				this.showPermissions = map.get("showPermissions") != null ? new HashSet<>((List<String>) map.get("showPermissions")) : new HashSet<>();
				this.hidePermissions = map.get("hidePermissions") != null ? new HashSet<>((List<String>) map.get("hidePermissions")) : new HashSet<>();
				this.startTime = map.get("startTime") != null ? new LocalDateTimeConverter().decode(map.getOrDefault("startTime", startTime)) : null;
				this.endTime = map.get("endTime") != null ? new LocalDateTimeConverter().decode(map.getOrDefault("endTime", endTime)) : null;
				try {
					this.condition = map.get("condition") != null ? AnnouncementCondition.valueOf((String) map.get("condition")) : null;
				} catch (IllegalArgumentException ex) {
					Nexus.log("Announcement Condition invalid for " + id + ": " + map.getOrDefault("condition", condition));
				}
			}

			@Override
			public Map<String, Object> serialize() {
				return new LinkedHashMap<String, Object>() {{
					put("id", id);
					put("text", text);
					put("hover", hover);
					put("command", command);
					put("suggest", suggest);
					put("enabled", enabled);
					put("motd", motd);
					put("showPermissions", new ArrayList<>(showPermissions));
					put("hidePermissions", new ArrayList<>(hidePermissions));
					put("startTime", new LocalDateTimeConverter().encode(startTime));
					put("endTime", new LocalDateTimeConverter().encode(endTime));
					put("condition", condition != null ? condition.name() : null);
				}};
			}

			public void send(Player player) {
				if (motd) {
					PlayerUtils.send(player, text);
				} else {
					PlayerUtils.send(player, "");
					PlayerUtils.send(player, getJson());
					PlayerUtils.send(player, "");
				}
			}

			@NotNull
			private JsonBuilder getJson() {
				JsonBuilder json = new JsonBuilder(PREFIX + text);
				if (!Strings.isNullOrEmpty(hover)) json.hover(hover);
				if (!Strings.isNullOrEmpty(command)) json.command(command);
				if (!Strings.isNullOrEmpty(suggest)) json.suggest(suggest);
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

			public enum AnnouncementCondition {
				VOTE(player -> {
					Voter voter = new VoteService().get(player);
					return voter.getActiveVotes().size() < VoteSite.values().length - 2;
				}),
				DISCORD_LINK(player -> {
					DiscordUser user = new DiscordService().get(player);
					return user.getUserId() == null;
				});

				@Getter
				private final Predicate<Player> condition;

				AnnouncementCondition(Predicate<Player> condition) {
					this.condition = condition;
				}

				public boolean test(Player player) {
					return condition.test(player);
				}
			}
		}

	}

}
