package me.pugabyte.nexus.features.commands;

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

	@Path("list [page]")
	void list(@Arg("1") int page) {
		if (config.getAnnouncements().isEmpty())
			error("No announcements have been created");

		send(PREFIX + "Announcements");
		BiFunction<Announcement, Integer, JsonBuilder> formatter = (announcement, index) ->
				json("&3" + (index + 1) + " &e" + announcement.getId() + " &7- &3" + ellipsis(announcement.getText(), 20))
						.addHover(announcement.getText())
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
