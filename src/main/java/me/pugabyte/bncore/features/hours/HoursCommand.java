package me.pugabyte.bncore.features.hours;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Async;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.models.nerd.Rank;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.OfflinePlayer;

import java.util.List;

@Aliases({"playtime", "days", "minutes", "seconds"})
public class HoursCommand extends CustomCommand {
	private HoursService service = new HoursService();

	public HoursCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void player(@Arg("self") OfflinePlayer player) {
		boolean isSelf = isSelf(player);

		Hours hours = service.get(player);
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

		final HoursService.HoursType hoursType = service.getType(type);

		List<Hours> results = service.getPage(hoursType, page);
		if (results.size() == 0)
			error(PREFIX + "&cNo results on page " + page);

		send("");
		send(PREFIX + "Total: " + StringUtils.timespanFormat(service.total(hoursType)) + (page > 1 ? "&e  |  &3Page " + page : ""));
		int i = (page - 1) * 10 + 1;
		for (Hours hours : results)
			send("&3" + i++ + " &e" + hours.getPlayer().getName() + " &7- " + StringUtils.timespanFormat(hours.get(hoursType)));
	}
}


