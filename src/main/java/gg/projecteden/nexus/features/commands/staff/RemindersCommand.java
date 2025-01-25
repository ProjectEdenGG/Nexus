package gg.projecteden.nexus.features.commands.staff;

import com.google.api.services.sheets.v4.model.ValueRange;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.exceptions.EdenException;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.reminders.ReminderConfig;
import gg.projecteden.nexus.models.reminders.ReminderConfig.Reminder;
import gg.projecteden.nexus.models.reminders.ReminderConfig.Reminder.ReminderCondition;
import gg.projecteden.nexus.utils.GoogleUtils;
import gg.projecteden.nexus.utils.GoogleUtils.SheetsUtils;
import gg.projecteden.nexus.utils.GoogleUtils.SheetsUtils.EdenSpreadsheet;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@NoArgsConstructor
@Aliases("reminder")
@Permission(Group.STAFF)
public class RemindersCommand extends CustomCommand implements Listener {
	private static ReminderConfig config;

	public RemindersCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		Tasks.async(() -> {
			try {
				GoogleUtils.startup();
				load();
			} catch (NullPointerException ignore) {}
		});
	}

	private static void load() {
		config = new ReminderConfig();
		ReminderSheet.readAll();
	}

	@Async
	@Path("reload")
	@Description("Reload reminders")
	void reload() {
		load();
		send(PREFIX + "Loaded &e" + config.getReminders().size() + " periodic reminders &3and &e" + config.getMotds().size() + " on join reminders");
	}

	private void saveToSheet() {
		try {
			ReminderSheet.saveAll();
		} catch (Exception ex) {
			Nexus.severe("[Reminders] An error occurred while trying to save to spreadsheet: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void saveAndEdit(Reminder reminder) {
		saveToSheet();
		edit(reminder);
	}

	@Async
	@Path("save")
	@Description("Saves all reminders to the spreadsheet")
	void save() {
		saveToSheet();
		send(PREFIX + "Saved &e" + config.getReminders().size() + " periodic reminders &3and &e" + config.getMotds().size() + " on join reminders");
	}

	@Async
	@Path("create <id> <text...>")
	@Description("Create a reminder")
	void create(String id, String text) {
		config.add(Reminder.builder().id(id).text(text).build());
		saveToSheet();
		send(PREFIX + "Reminder &e" + id + " &3created");
	}

	@Async
	@Confirm
	@Path("delete <id>")
	@Description("Delete specified reminders")
	void delete(Reminder reminder) {
		config.remove(reminder.getId());
		saveToSheet();
		send(PREFIX + "Reminder &e" + reminder.getId() + " &cdeleted");
	}

	@Async
	@Path("edit <id>")
	@Description("Edit reminders that already exist")
	void edit(Reminder reminder) {
		send(json(PREFIX + "Edit reminder &e" + reminder.getId() + " &f &f ").group()
				.next("&6⟳").hover("&6Refresh").command("/reminders edit " + reminder.getId()).group()
				.next(" ").group()
				.next("&c✖").hover("&cDelete").command("/reminders delete " + reminder.getId()));

		line();
		send(json(" &3Text: &7" + reminder.getText()).hover("&3Click to edit").suggest("/reminders edit text " + reminder.getId() + " " + reminder.getText()));
		line();
		send(json(" &3Command: &7" + reminder.getCommand()).hover("&3Click to edit").suggest("/reminders edit command " + reminder.getId() + " " + (reminder.getCommand() == null ? "" : reminder.getCommand())));
		send(json(" &3Suggest: &7" + reminder.getSuggest()).hover("&3Click to edit").suggest("/reminders edit suggest " + reminder.getId() + " " + (reminder.getSuggest() == null ? "" : reminder.getSuggest())));
		send(json(" &3URL: &7" + reminder.getUrl()).hover("&3Click to edit").suggest("/reminders edit url " + reminder.getId() + " " + (reminder.getUrl() == null ? "" : reminder.getUrl())));
		JsonBuilder hover = json(" &3Hover: ");
		if (reminder.getHover().isEmpty())
			hover.next("&7null").hover("&3Click to add line").suggest("/reminders edit hover add " + reminder.getId() + " ");
		else {
			for (int i = 1; i <= reminder.getHover().size(); i++) {
				String line = reminder.getHover().get(i - 1);
				hover.newline().next(" &f &f ").group()
						.next("&c[-]").command("/reminders edit hover delete " + reminder.getId() + " " + i).hover("&cClick to delete").group()
						.next(" ").group()
						.next(line).suggest("/reminders edit hover set " + reminder.getId() + " " + i + " " + line).hover("&3Click to edit").group();
			}
		}

		send(hover);

		line();

		if (reminder.isEnabled())
			send(json(" " + StringUtils.CHECK + " Enabled").hover("&3Click to &cdisable").command("/reminders disable " + reminder.getId()));
		else
			send(json(" " + StringUtils.X + " Disabled").hover("&3Click to &aenable").command("/reminders enable " + reminder.getId()));

		if (reminder.isMotd())
			send(json(" &3Type: &eMOTD").hover("&3Click to toggle").command("/reminders edit motd " + reminder.getId() + " false"));
		else
			send(json(" &3Type: &3Reminder").hover("&3Click to toggle").command("/reminders edit motd " + reminder.getId() + " true"));

		send(json(" &3Show permissions ").group().next("&a[+]").hover("&aAdd permission").suggest("/reminders edit showPermissions add " + reminder.getId() + " "));
		showPermissionsEdit(reminder, reminder.getShowPermissions(), "showPermissions");

		send(json(" &3Hide permissions ").group().next("&a[+]").hover("&aAdd permission").suggest("/reminders edit hidePermissions add " + reminder.getId() + " "));
		showPermissionsEdit(reminder, reminder.getHidePermissions(), "hidePermissions");

		send(json(" &3Start time: &e" + (reminder.getStartTime() == null ? "None" : TimeUtils.shortDateTimeFormat(reminder.getStartTime())))
				.hover("&3Click to edit").suggest("/reminders edit startTime " + reminder.getId() + " YYYY-MM-DDTHH:MM:SS"));
		send(json(" &3End time: &e" + (reminder.getEndTime() == null ? "None" : TimeUtils.shortDateTimeFormat(reminder.getEndTime())))
				.hover("&3Click to edit").suggest("/reminders edit endTime " + reminder.getId() + " YYYY-MM-DDTHH:MM:SS"));

		send(json(" &3Condition: &e" + (reminder.getCondition() == null ? "None" : camelCase(reminder.getCondition())))
				.hover("&3Click to edit").suggest("/reminders edit condition " + reminder.getId() + " "));
		line();
	}

	private void showPermissionsEdit(Reminder reminder, Set<String> permissions, String command) {
		if (permissions.isEmpty())
			send("   &cNone");
		else
			for (String permission : permissions)
				send(json("   &c[-]").hover("&cRemove permission").command("/reminders edit " + command + " remove " + reminder.getId() + " " + permission).group().next(" &e" + permission));
	}

	@Async
	@Path("enable <id>")
	@HideFromHelp
	@HideFromWiki
	void enable(Reminder reminder) {
		reminder.setEnabled(true);
		saveAndEdit(reminder);
	}

	@Async
	@Path("disable <id>")
	@HideFromHelp
	@HideFromWiki
	void disable(Reminder reminder) {
		reminder.setEnabled(false);
		saveAndEdit(reminder);
	}

	@Async
	@Path("edit id <id> <newId>")
	@HideFromHelp
	@HideFromWiki
	void editId(Reminder reminder, String newId) {
		reminder.setId(newId);
		saveAndEdit(reminder);
	}

	@Async
	@Path("edit text <id> <text...>")
	@HideFromHelp
	@HideFromWiki
	void editText(Reminder reminder, String text) {
		reminder.setText(text);
		saveAndEdit(reminder);
	}

	@Async
	@Path("edit hover add <id> <text...>")
	@HideFromHelp
	@HideFromWiki
	void editHoverAdd(Reminder reminder, String hover) {
		reminder.getHover().add(hover);
		saveAndEdit(reminder);
	}

	@Async
	@Path("edit hover set <id> <line> <text...>")
	@HideFromHelp
	@HideFromWiki
	void editHoverSet(Reminder reminder, int line, String hover) {
		reminder.getHover().set(line - 1, hover);
		saveAndEdit(reminder);
	}

	@Async
	@Path("edit hover delete <line> <id>")
	@HideFromHelp
	@HideFromWiki
	void editHoverRemove(Reminder reminder, int line) {
		reminder.getHover().remove(line - 1);
		saveAndEdit(reminder);
	}

	@Async
	@Path("edit command <id> <text...>")
	@HideFromHelp
	@HideFromWiki
	void editCommand(Reminder reminder, String command) {
		reminder.setCommand(command);
		saveAndEdit(reminder);
	}

	@Async
	@Path("edit suggest <id> <text...>")
	@HideFromHelp
	@HideFromWiki
	void editSuggest(Reminder reminder, String suggest) {
		reminder.setSuggest(suggest);
		saveAndEdit(reminder);
	}

	@Async
	@Path("edit url <id> <text...>")
	@HideFromHelp
	@HideFromWiki
	void editUrl(Reminder reminder, String url) {
		reminder.setUrl(url);
		saveAndEdit(reminder);
	}

	@Async
	@Path("edit motd <id> [enable]")
	@HideFromHelp
	@HideFromWiki
	void editMotd(Reminder reminder, Boolean enable) {
		if (enable == null)
			enable = !reminder.isMotd();
		reminder.setMotd(enable);
		saveAndEdit(reminder);
	}

	@Async
	@Path("edit showPermissions add <id> <permission(s)>")
	@HideFromHelp
	@HideFromWiki
	void editShowPermissionsAdd(Reminder reminder, @Arg(type = String.class) List<String> permissions) {
		reminder.getShowPermissions().addAll(permissions);
		saveAndEdit(reminder);
	}

	@Async
	@Path("edit showPermissions remove <id> <permission(s)>")
	@HideFromHelp
	@HideFromWiki
	void editShowPermissionsRemove(Reminder reminder, @Arg(type = String.class) List<String> permissions) {
		permissions.forEach(reminder.getShowPermissions()::remove);
		saveAndEdit(reminder);
	}

	@Async
	@Path("edit hidePermissions add <id> <permission(s)>")
	@HideFromHelp
	@HideFromWiki
	void editHidePermissionsAdd(Reminder reminder, @Arg(type = String.class) List<String> permissions) {
		reminder.getHidePermissions().addAll(permissions);
		saveAndEdit(reminder);
	}

	@Async
	@Path("edit hidePermissions remove <id> <permission(s)>")
	@HideFromHelp
	@HideFromWiki
	void editHidePermissionsRemove(Reminder reminder, @Arg(type = String.class) List<String> permissions) {
		permissions.forEach(reminder.getHidePermissions()::remove);
		saveAndEdit(reminder);
	}

	@Async
	@Path("edit startTime <id> [time]")
	@HideFromHelp
	@HideFromWiki
	void editStartTime(Reminder reminder, LocalDateTime startTime) {
		reminder.setStartTime(startTime);
		saveAndEdit(reminder);
	}

	@Async
	@Path("edit endTime <id> [time]")
	@HideFromHelp
	@HideFromWiki
	void editEndTime(Reminder reminder, LocalDateTime endTime) {
		reminder.setEndTime(endTime);
		saveAndEdit(reminder);
	}

	@Async
	@Path("edit condition <id> [condition]")
	@HideFromHelp
	@HideFromWiki
	void editCondition(Reminder reminder, ReminderCondition condition) {
		reminder.setCondition(condition);
		saveAndEdit(reminder);
	}

	@Async
	@Path("list [page]")
	@Description("List created reminders")
	void list(@Arg("1") int page) {
		if (config.getAll().isEmpty())
			error("No reminders have been created");

		send(PREFIX + "Reminders");
		BiFunction<Reminder, String, JsonBuilder> formatter = (reminder, index) ->
				json(index + " " + (reminder.isMotd() ? "&6➤" : "&b⚡") + " &e" + reminder.getId() + " &7- " + StringUtils.ellipsis(reminder.getText(), 50))
						.hover("&3Type: &e" + (reminder.isMotd() ? "MOTD" : "Reminder"))
						.hover("&7" + reminder.getText())
						.command("/reminders edit " + reminder.getId());

		new Paginator<Reminder>()
			.values(config.getAll())
			.formatter(formatter)
			.command("/reminders list")
			.page(page)
			.send();
	}

	@Async
	@Path("show <player> <reminder>")
	@Description("Show a player a certain reminder in chat")
	void show(Player player, Reminder reminder) {
		reminder.send(player);
	}

	@Async
	@Path("motd [player]")
	@Description("View the MOTD")
	void motd(@Arg(value = "self", permission = Group.STAFF) Player player) {
		config.showMotd(player);
	}

	@Async
	@Path("test <player> <reminder>")
	@Description("Test if a player will receive a specific reminder")
	void test(Player player, Reminder reminder) {
		send(PREFIX + player.getName() + " " + (reminder.test(player) ? "&awould" : "&cwould not") + " &3receive the &e" + reminder.getId() + " &3reminder");
	}

	@Async
	@Path("testCondition <player> <reminder>")
	@Description("Test if a player has met the conditions for a specific reminder")
	void testCondition(Player player, Reminder reminder) {
		if (reminder.getCondition() == null)
			error("Reminder &e" + reminder.getId() + " &cdoes not have a condition so players will always receive it");

		send(PREFIX + player.getName() + " &ewould" + (reminder.getCondition().test(player) ? "" : " not")
				+ " &3receive the &e" + camelCase(reminder.getCondition()) + " &3reminder");
	}

	@ConverterFor(Reminder.class)
	Reminder convertToReminder(String value) {
		return config.findRequestMatch(value).orElseThrow(() -> new InvalidInputException("Reminder &e" + value +" &cnot found"));
	}

	@TabCompleterFor(Reminder.class)
	List<String> tabCompleteReminder(String filter) {
		return config.getAll().stream()
				.map(Reminder::getId)
				.filter(request -> request.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

	@Async
	@Path("setInterval <seconds>")
	@Description("Edit how often reminders appears in chat")
	void setInterval(int seconds) {
		interval = TickTime.SECOND.x(seconds);
		startTask();
		send(PREFIX + "Interval set to " + seconds + " seconds (will reset when plugin reloads)");
	}

	private static long interval = TickTime.MINUTE.x(5);
	private static int taskId = -1;

	static {
		startTask();
	}

	public static void startTask() {
		Tasks.cancel(taskId);
		taskId = Tasks.repeatAsync(interval, interval, () -> {
			for (Player player : OnlinePlayers.getAll()) {
				Utils.attempt(100, () -> {
					Reminder reminder = config.getRandomReminder();
					if (reminder == null)
						return false;

					if (!reminder.test(player))
						return false;

					reminder.send(player);
					return true;
				});
			}
		});
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (config != null)
			config.showMotd(event.getPlayer());
	}

	@Getter
	@AllArgsConstructor
	public enum ReminderSheet {
		PERIODIC(false),
		ON_JOIN(true),
		;

		private final boolean motd;

		public String getSheetId() {
			return EnumUtils.prettyName(name());
		}

		public static void readAll() {
			for (ReminderSheet sheet : values())
				sheet.read();
		}

		public static void saveAll() {
			for (ReminderSheet sheet : values())
				sheet.save();
		}

		public List<Reminder> getReminders() {
			return config.getAll().stream().filter(reminder -> reminder.isMotd() == motd).toList();
		}

		public void read() {
			final ValueRange valueRange = SheetsUtils.sheetValues(EdenSpreadsheet.REMINDERS, getSheetId(), "A:Z");
			final Iterator<List<Object>> iterator = valueRange.getValues().iterator();
			if (iterator.hasNext())
				iterator.next(); // Skip headers

			while (iterator.hasNext()) {
				final List<Object> row = iterator.next();
				try {
					config.add(Reminder.deserialize(row, motd));
				} catch (Exception ex) {
					Nexus.log("Error adding reminder: " + ex.getMessage());
					Nexus.log(row.toString());
					if (!(ex instanceof EdenException))
						ex.printStackTrace();
				}
			}
		}

		public void save() {
			final List<List<Object>> rows = new ArrayList<>();
			final ValueRange values = new ValueRange().setValues(rows);

			rows.add(Reminder.HEADERS);

			for (Reminder reminder : getReminders())
				rows.add(reminder.serialize());

			SheetsUtils.updateEntireSheet(EdenSpreadsheet.REMINDERS, getSheetId(), values);
		}
	}
}
