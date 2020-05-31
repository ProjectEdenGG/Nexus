package me.pugabyte.bncore.features.hours2;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Async;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService.HoursType;
import me.pugabyte.bncore.models.hours2.Hours2;
import me.pugabyte.bncore.models.hours2.Hours2Service;
import me.pugabyte.bncore.models.hours2.Hours2Service.PageResult;
import me.pugabyte.bncore.models.nerd.Rank;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.OfflinePlayer;

import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;

// @Aliases({"playtime", "days", "minutes", "seconds"})
@Permission("group.seniorstaff")
public class Hours2Command extends CustomCommand {
	private Hours2Service service = new Hours2Service();

	public Hours2Command(CommandEvent event) {
		super(event);
	}

	@Async
	@Path("[player]")
	void player(@Arg("self") OfflinePlayer player) {
		boolean isSelf = isSelf(player);

		Hours2 hours2 = service.get(player);
		send("");
		send(PREFIX + (isSelf ? "Your" : "&e" + player.getName() + "&3's") + " playtime");
		send("&3Total: &e" + StringUtils.timespanFormat(hours2.getTotal(), "None"));
		send("&7- &3Today: &e" + StringUtils.timespanFormat(hours2.getDaily(), "None"));
//		send("&7- &3This week: &e" + StringUtils.timespanFormat(hours2.getWeekly(), "None"));
		send("&7- &3This month: &e" + StringUtils.timespanFormat(hours2.getMonthly(), "None"));

		if (Rank.getHighestRank(player) == Rank.GUEST) {
			int day = 60 * 60 * 24;
			String who = (isSelf ? "You need" : player.getName() + " needs") + " ";
			String left = StringUtils.timespanFormat(day - hours2.getTotal());

			line();
			send("&3" + who + "&e" + left + " more in-game play time &3to achieve &fMember&3.");
		}
	}

	@Async
	@Path("set <player> <type> <seconds>")
	@Permission("group.admin")
	void set(OfflinePlayer player, HoursType type, int seconds) {
		Hours hours = service.get(player);
		hours.set(type, seconds);
		service.save(hours);
		send(PREFIX + "Set " + player.getName() + "'s " + camelCase(type.name()) + " to " + hours.get(type));
	}

	@Async
	@Path("top [page]")
	void top(@Arg("1") int page) {
//		String type = isIntArg(2) ? "total" : arg(2);
//		int page = isIntArg(2) ? intArg(2) : isIntArg(3) ? intArg(3) : 1;

//		final HoursType hoursType = service.getType(type);

//		List<Hours> results = service.getPage(hoursType, page);

		List<PageResult> results = service.getPage(page);
		if (results.size() == 0)
			error("&cNo results on page " + page);

		send("");
//		send(PREFIX + "Total: " + StringUtils.timespanFormat(service.total(hoursType)) + (page > 1 ? "&e  |  &3Page " + page : ""));
		int i = (page - 1) * 10 + 1;
		for (PageResult result : results)
			send("&3" + i++ + " &e" + result.getOfflinePlayer().getName() + " &7- " + StringUtils.timespanFormat(result.getTotal()));
	}

	@Async
	@Path("migrate")
	void migrate() {
		service.migrate();
	}
}


