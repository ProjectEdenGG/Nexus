package me.pugabyte.bncore.features.hours;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Async;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.hoursold.HoursOld;
import me.pugabyte.bncore.models.hoursold.HoursOldService;
import me.pugabyte.bncore.models.hoursold.HoursOldService.HoursType;
import me.pugabyte.bncore.models.nerd.Rank;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;

@Permission("group.seniorstaff")
public class HoursOldCommand extends CustomCommand {
	private final HoursOldService service = new HoursOldService();

	public HoursOldCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void player(@Arg("self") OfflinePlayer player) {
		boolean isSelf = isSelf(player);

		HoursOld hours = service.get(player);
		send("");
		send(PREFIX + (isSelf ? "Your" : "&e" + player.getName() + "&3's") + " playtime");
		send("&3Total: &e" + StringUtils.timespanFormat(hours.getTotal(), "None"));
		send("&7- &3Today: &e" + StringUtils.timespanFormat(hours.getDaily(), "None"));
		send("&7- &3This week: &e" + StringUtils.timespanFormat(hours.getWeekly(), "None"));
		send("&7- &3This month: &e" + StringUtils.timespanFormat(hours.getMonthly(), "None"));

		if (Rank.getHighestRank(player) == Rank.GUEST) {
			int day = 60 * 60 * 24;
			String who = (isSelf ? "You need" : player.getName() + " needs") + " ";
			String left = StringUtils.timespanFormat(day - hours.getTotal());

			line();
			send("&3" + who + "&e" + left + " more in-game play time &3to achieve &fMember&3.");
		}
	}

	@Path("cleanup")
	@Permission("group.admin")
	void cleanup() {
		send("Cleaned up " + service.cleanup() + " records");
	}

	@Path("set <player> <type> <seconds>")
	@Permission("group.admin")
	void set(OfflinePlayer player, HoursType type, int seconds) {
		HoursOld hours = service.get(player);
		hours.set(type, seconds);
		service.save(hours);
		send(PREFIX + "Set " + player.getName() + "'s " + camelCase(type.name()) + " to " + hours.get(type));
	}

	@Path("endOfDay")
	void endOfDay() {
		console();
		service.endOfDay();
	}

	@Path("endOfWeek")
	void endOfWeek() {
		console();
		service.endOfWeek();
	}

	@Path("endOfMonth")
	void endOfMonth() {
		console();
		service.endOfMonth();
	}

	@Async
	@Path("top")
	void top() {
		String type = isIntArg(2) ? "total" : arg(2);
		int page = isIntArg(2) ? intArg(2) : isIntArg(3) ? intArg(3) : 1;

		final HoursOldService.HoursType hoursType = service.getType(type);

		List<HoursOld> results = service.getPage(hoursType, page);
		if (results.size() == 0)
			error("&cNo results on page " + page);

		send("");
		send(PREFIX + "Total: " + StringUtils.timespanFormat(service.total(hoursType)) + (page > 1 ? "&e  |  &3Page " + page : ""));
		int i = (page - 1) * 10 + 1;
		for (HoursOld hours : results)
			send("&3" + i++ + " &e" + hours.getPlayer().getName() + " &7- " + StringUtils.timespanFormat(hours.get(hoursType)));
	}

	private static final int INTERVAL = 5;

	static {
		BNCore.getCron().schedule("00 00 * * *", () -> new HoursOldService().endOfDay());
		BNCore.getCron().schedule("00 00 * * 1", () -> new HoursOldService().endOfWeek());
		BNCore.getCron().schedule("00 00 1 * *", () -> new HoursOldService().endOfMonth());

		Tasks.repeatAsync(10, Time.SECOND.x(INTERVAL), () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				try {
					if (AFK.get(player).isAfk()) continue;

					HoursOldService service = new HoursOldService();
					HoursOld hours = service.get(player);
					hours.increment(INTERVAL);
					service.save(hours);
				} catch (Exception ex) {
					BNCore.warn("Error in Hours scheduler: " + ex.getMessage());
				}
			}
		});
	}
}


