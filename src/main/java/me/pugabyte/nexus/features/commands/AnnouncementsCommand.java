package me.pugabyte.nexus.features.commands;

import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
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
import me.pugabyte.nexus.models.announcement.AnnouncementConfig;
import me.pugabyte.nexus.models.announcement.AnnouncementConfig.Announcement;
import me.pugabyte.nexus.models.announcement.AnnouncementConfig.Announcement.AnnouncementCondition;
import me.pugabyte.nexus.models.announcement.AnnouncementConfigService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.ellipsis;
import static me.pugabyte.nexus.utils.StringUtils.shortDateTimeFormat;

@Permission("group.seniorstaff")
@Aliases("announcement")
public class AnnouncementsCommand extends CustomCommand {
	private final AnnouncementConfigService service = new AnnouncementConfigService();
	private final AnnouncementConfig config = service.get(Nexus.getUUID0());

	public AnnouncementsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private void save() {
		service.save(config);
	}

	private void saveAndEdit(Announcement announcement) {
		service.save(config);
		edit(announcement);
	}

	@Path("create <id> <text...>")
	void create(String id, String text) {
		Announcement announcement = new Announcement(id, text);
		config.getAnnouncements().add(announcement);
		save();
		send(PREFIX + "Announcement &e" + announcement.getId() + " &3created");
	}

	@Confirm
	@Path("delete <id>")
	void delete(Announcement announcement) {
		config.getAnnouncements().remove(announcement);
		save();
		send(PREFIX + "Announcement &e" + announcement.getId() + " &cdeleted");
	}

	@Path("edit <id>")
	void edit(Announcement announcement) {
		send(json(PREFIX + "Edit announcement &e" + announcement.getId() + " &f &f ").group()
				.next("&6⟳").command("/announcements edit " + announcement.getId()).group()
				.next(" ").group()
				.next("&c✖").command("/announcements delete " + announcement.getId()));

		line();
		send(json(" &3Text: &7" + announcement.getText()).suggest("/announcements edit text " + announcement.getId() + " " + announcement.getText()));
		line();

		if (announcement.isEnabled())
			send(json(" " + StringUtils.CHECK + " Enabled").hover("&3Click to &cdisable").command("/announcements disable " + announcement.getId()));
		else
			send(json(" " + StringUtils.X + " Disabled").hover("&3Click to &aenable").command("/announcements enable " + announcement.getId()));

		send(json(" &3Show permissions ").group().next("&a[+]").suggest("/announcements edit showPermissions add " + announcement.getId() + " "));
		showPermissionsEdit(announcement, announcement.getShowPermissions(), "showPermissions");

		send(json(" &3Hide permissions ").group().next("&a[+]").suggest("/announcements edit hidePermissions add " + announcement.getId() + " "));
		showPermissionsEdit(announcement, announcement.getHidePermissions(), "hidePermissions");

		send(json(" &3Start time: &e" + (announcement.getStartTime() == null ? "None" : shortDateTimeFormat(announcement.getStartTime())))
				.suggest("/announcements edit startTime " + announcement.getId() + " YYYY-MM-DDTHH:MM:SS.ZZZ"));
		send(json(" &3End time: &e" + (announcement.getEndTime() == null ? "None" : shortDateTimeFormat(announcement.getEndTime())))
				.suggest("/announcements edit endTime " + announcement.getId() + " YYYY-MM-DDTHH:MM:SS.ZZZ"));

		send(json(" &3Condition: &e" + (announcement.getCondition() == null ? "None" : camelCase(announcement.getCondition())))
				.suggest("/announcements edit condition " + announcement.getId() + " "));
		line();
	}

	private void showPermissionsEdit(Announcement announcement, Set<String> hidePermissions, String command) {
		if (hidePermissions.isEmpty())
			send("   &cNone");
		else
			for (String permission : hidePermissions)
				send(json("   &c[-]").command("/announcements edit " + command + " remove " + announcement.getId() + " " + permission).group().next(" &e" + permission));
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
		if (config.getAnnouncements().isEmpty())
			error("No announcements have been created");

		send(PREFIX + "Announcements");
		BiFunction<Announcement, Integer, JsonBuilder> formatter = (announcement, index) ->
				json("&3" + (index + 1) + " &e" + announcement.getId() + " &7- " + ellipsis(announcement.getText(), 50))
						.addHover("&7" + announcement.getText())
						.command("/announcements edit " + announcement.getId());
		paginate(config.getAnnouncements(), formatter, "/announcements list", page);
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
		return config.getAnnouncements().stream()
				.map(Announcement::getId)
				.filter(request -> request.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

	private static final int interval = Time.SECOND.x(30);

	static {
		Tasks.repeatAsync(interval, interval, () -> {
			if (true) return;
			AnnouncementConfigService service = new AnnouncementConfigService();
			AnnouncementConfig config = service.get(Nexus.getUUID0());

			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!PlayerUtils.isPuga(player))
					continue;

				Utils.attempt(100, () -> {
					Announcement announcement = config.getRandomAnnouncement();

					if (!announcement.isEnabled())
						return false;

					if (!announcement.getShowPermissions().isEmpty()) {
						boolean canSee = false;
						for (String showPermission : announcement.getShowPermissions())
							if (player.hasPermission(showPermission)) {
								canSee = true;
								break;
							}

						if (!canSee)
							return false;
					}

					if (!announcement.getHidePermissions().isEmpty()) {
						boolean canHide = false;
						for (String hidePermission : announcement.getHidePermissions())
							if (player.hasPermission(hidePermission)) {
								canHide = true;
								break;
							}

						if (canHide)
							return false;
					}

					if (announcement.getStartTime() != null && announcement.getStartTime().isAfter(LocalDateTime.now()))
						return false;

					if (announcement.getEndTime() != null && announcement.getEndTime().isBefore(LocalDateTime.now()))
						return false;

					if (announcement.getCondition() != null && !announcement.getCondition().test(player))
						return false;

					announcement.send(player);
					return true;
				});
			}
		});
	}

}
