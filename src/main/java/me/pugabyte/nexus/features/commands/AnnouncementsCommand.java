package me.pugabyte.nexus.features.commands;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
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
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.entity.Player;

import java.util.List;
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

	@Path("info <announcement>")
	void info(Announcement announcement) {
		send(PREFIX + announcement.getId() + " announcement");
		send(" &eText: &3" + announcement.getText());
		if (!announcement.getShowPermissions().isEmpty())
			send(" &eShow permissions: &3" + String.join(", ", announcement.getShowPermissions()));
		if (!announcement.getHidePermissions().isEmpty())
			send(" &eHide permissions: &3" + String.join(", ", announcement.getHidePermissions()));
		if (announcement.getStartTime() != null)
			send(" &eStart time: &3" + shortDateTimeFormat(announcement.getStartTime()));
		if (announcement.getEndTime() != null)
			send(" &eEnd time: &3" + shortDateTimeFormat(announcement.getEndTime()));
		if (announcement.getCondition() != null)
			send(" &eCondition: &3" + camelCase(announcement.getCondition()));
	}

	@Path("create <id> <text>")
	void create(String id, String text) {
		Announcement announcement = new Announcement(id, text);
		config.getAnnouncements().add(announcement);
		save();
		send(PREFIX + "Announcement &e" + announcement.getId() + " &3created");
	}

	@Path("enable <id>")
	void enable(Announcement announcement) {
		announcement.setEnabled(true);
		save();
		send(PREFIX + "Announcement &e" + announcement.getId() + " &aenabled");
	}

	@Path("disable <id>")
	void disable(Announcement announcement) {
		announcement.setEnabled(false);
		save();
		send(PREFIX + "Announcement &e" + announcement.getId() + " &cdisabled");
	}

	@Path("edit <id>")
	void edit(Announcement announcement) {
		send(PREFIX + "Edit announcement &e" + announcement.getId());
		if (announcement.isEnabled())
			send(json(" " + StringUtils.CHECK + "Enabled").hover("&3Click to &cdisable"));
		else
			send(json(" " + StringUtils.X + "Disabled").hover("&3Click to &aenable"));

		send(json(" &3Show permissions ").group().next("&a[+]").suggest("/announcement edit showPermissions add " + announcement.getId() + " "));
		showPermissionsEdit(announcement, announcement.getShowPermissions(), "showPermissions");

		send(json(" &3Hide permissions ").group().next("&a[+]").suggest("/announcement edit hidePermissions add " + announcement.getId() + " "));
		showPermissionsEdit(announcement, announcement.getHidePermissions(), "hidePermissions");

		// TODO Rest of properties
	}

	private void showPermissionsEdit(Announcement announcement, Set<String> hidePermissions, String command) {
		if (hidePermissions.isEmpty())
			send(" &cNone");
		else
			for (String permission : hidePermissions)
				send(json(" &c[-]").suggest("/announcements edit " + command + " remove " + announcement.getId()).group().next(" &3" + permission));
	}

	@Path("edit text <id> <text>")
	void editText(Announcement announcement, String text) {
		announcement.setText(text);
		save();
		send(PREFIX + "Text for announcement &e" + announcement.getId() + " &3set to:");
		send("&7" + announcement.getText());
	}

	@Path("edit showPermissions add <id> <permission(s)>")
	void editShowPermissionsAdd(Announcement announcement, List<String> permissions) {
		announcement.getShowPermissions().addAll(permissions);
		save();
		sendPermissions(announcement, "Show", announcement.getShowPermissions());
	}

	@Path("edit showPermissions remove <id> <permission(s)>")
	void editShowPermissionsRemove(Announcement announcement, List<String> permissions) {
		announcement.getShowPermissions().removeAll(permissions);
		save();
		sendPermissions(announcement, "Show", announcement.getShowPermissions());
	}

	@Path("edit hidePermissions add <id> <permission(s)>")
	void editHidePermissionsAdd(Announcement announcement, Set<String> permissions) {
		announcement.getShowPermissions().addAll(permissions);
		save();
		sendPermissions(announcement, "Hide", announcement.getHidePermissions());
	}

	@Path("edit hidePermissions remove <id> <permission(s)>")
	void editHidePermissionsRemove(Announcement announcement, Set<String> permissions) {
		announcement.getShowPermissions().removeAll(permissions);
		save();
		sendPermissions(announcement, "Hide", announcement.getHidePermissions());
	}

	private void sendPermissions(Announcement announcement, String type, Set<String> permissions) {
		send(PREFIX + type + " permissions for announcement &e" + announcement.getId() + "&3:");
		if (permissions.isEmpty())
			send(" &cNone");
		else
			for (String permission : permissions)
				send(" &3" + permission);
	}

	@Path("edit startTime <id> [time]")
	void editStartTime(Announcement announcement, LocalDateTime startTime) {
		announcement.setStartTime(startTime);
		save();
		send(PREFIX + "Start time for announcement &e" + announcement.getId() + " &3set to &e" + (startTime == null ? "null" : shortDateTimeFormat(startTime)));
	}

	@Path("edit endTime <id> [time]")
	void editEndTime(Announcement announcement, LocalDateTime endTime) {
		announcement.setEndTime(endTime);
		save();
		send(PREFIX + "Start time for announcement &e" + announcement.getId() + " &3set to &e" + (endTime == null ? "null" : shortDateTimeFormat(endTime)));
	}

	@Path("edit condition <id> [condition]")
	void editCondition(Announcement announcement, AnnouncementCondition condition) {
		announcement.setCondition(condition);
		save();
		send(PREFIX + "Condition for announcement &e" + announcement.getId() + " &3set to &e" + (condition == null ? "null" : camelCase(condition)));
	}

	@Path("list [page]")
	void list(@Arg("1") int page) {
		if (config.getAnnouncements().isEmpty())
			error("No announcements have been created");

		send(PREFIX + "Announcements");
		BiFunction<Announcement, Integer, JsonBuilder> formatter = (announcement, index) ->
				json("&3" + (index + 1) + " &e" + announcement.getId() + " &7- " + ellipsis(announcement.getText(), 20))
						.addHover("&7" + announcement.getText())
						.command("/announcements info " + announcement.getId());
		paginate(config.getAnnouncements(), formatter, "/announcements list ", page);
	}

	@Path("test <player> <announcement>")
	void testCriteria(Player player, AnnouncementCondition condition) {
		send(PREFIX + player.getName() + " &ewould" + (condition.test(player) ? "" : " not") + " &3receive the &e" + camelCase(condition) + " &3announcement");
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

}
