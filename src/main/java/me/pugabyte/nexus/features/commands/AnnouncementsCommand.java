package me.pugabyte.nexus.features.commands;

import lombok.NoArgsConstructor;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.ellipsis;
import static me.pugabyte.nexus.utils.StringUtils.shortDateTimeFormat;

@NoArgsConstructor
@Aliases("announcement")
@Permission("group.seniorstaff")
public class AnnouncementsCommand extends CustomCommand implements Listener {
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
		config.add(new Announcement(id, text));
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
				.hover("&3Click to edit").suggest("/announcements edit startTime " + announcement.getId() + " YYYY-MM-DDTHH:MM:SS.ZZZ"));
		send(json(" &3End time: &e" + (announcement.getEndTime() == null ? "None" : shortDateTimeFormat(announcement.getEndTime())))
				.hover("&3Click to edit").suggest("/announcements edit endTime " + announcement.getId() + " YYYY-MM-DDTHH:MM:SS.ZZZ"));

		send(json(" &3Condition: &e" + (announcement.getCondition() == null ? "None" : camelCase(announcement.getCondition())))
				.hover("&3Click to edit").suggest("/announcements edit condition " + announcement.getId() + " "));
		line();
	}

	private void showPermissionsEdit(Announcement announcement, Set<String> hidePermissions, String command) {
		if (hidePermissions.isEmpty())
			send("   &cNone");
		else
			for (String permission : hidePermissions)
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
			AnnouncementConfigService service = new AnnouncementConfigService();
			AnnouncementConfig config = service.get(Nexus.getUUID0());

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
		AnnouncementConfigService service = new AnnouncementConfigService();
		AnnouncementConfig config = service.get(Nexus.getUUID0());

		for (Announcement motd : config.getMotds())
			if (motd.test(event.getPlayer()))
				motd.send(event.getPlayer());
	}

}
