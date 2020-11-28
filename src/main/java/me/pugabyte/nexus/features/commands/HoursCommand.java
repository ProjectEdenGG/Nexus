package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.afk.AFK;
import me.pugabyte.nexus.features.chat.Koda;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.models.hours.HoursService.PageResult;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.SoundUtils.Jingle;
import me.pugabyte.nexus.utils.StringUtils.TimespanFormatter;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

@Aliases({"playtime", "days", "minutes", "seconds"})
public class HoursCommand extends CustomCommand {
	private final HoursService service = new HoursService();

	public HoursCommand(CommandEvent event) {
		super(event);
	}

	private static final int DAY = Time.DAY.get() / 20;

	@Async
	@Path("[player]")
	void player(@Arg("self") Hours hours) {
		OfflinePlayer player = hours.getOfflinePlayer();
		boolean isSelf = isSelf(player);

		send("");
		send(PREFIX + (isSelf ? "Your" : "&e" + player.getName() + "&3's") + " playtime");
		send("&3Total: &e" + TimespanFormatter.of(hours.getTotal()).noneDisplay(true).format());
		send("&7- &3Today: &e" + TimespanFormatter.of(hours.getDaily()).noneDisplay(true).format());
		send("&7- &3This month: &e" + TimespanFormatter.of(hours.getMonthly()).noneDisplay(true).format());

		if (Rank.getHighestRank(player) == Rank.GUEST) {

			String who = (isSelf ? "You need" : player.getName() + " needs") + " ";
			String left = TimespanFormatter.of(DAY - hours.getTotal()).format();

			line();
			send("&3" + who + "&e" + left + " more in-game play time &3to achieve &fMember&3.");
		}
	}

	@Permission("group.seniorstaff")
	@Path("debug [player]")
	void debug(@Arg("self") OfflinePlayer player) {
		send(service.get(player).toString());
	}

	/*
	/hours top month:march20
	/hours top month:august19 30
	/hours top year:2020
	/hours top monthly
	/hours top daily 5
	/hours top
	 */

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
		send(PREFIX + (page > 1 ? "&3Page " + page : ""));
		int i = (page - 1) * 10 + 1;
		for (PageResult result : results)
			send("&3" + i++ + " &e" + result.getOfflinePlayer().getName() + " &7- " + TimespanFormatter.of(result.getTotal()).format());
	}

	private static final int INTERVAL = 5;

	static {
		Tasks.repeatAsync(10, Time.SECOND.x(INTERVAL), () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				try {
					if (AFK.get(player).isAfk()) continue;

					HoursService service = new HoursService();
					Hours hours = service.get(player);
					hours.increment(INTERVAL);
					service.update(hours);

					if (Rank.getHighestRank(player) == Rank.GUEST) {
						if (hours.getTotal() > DAY) {
							Tasks.sync(() -> {
								PlayerUtils.runCommandAsConsole("lp user " + player.getName() + " parent set " + Rank.MEMBER.name());
								Koda.say("Congrats on Member rank, " + player.getName() + "!");
								Jingle.RANKUP.play(player);
								PlayerUtils.send(player, "");
								PlayerUtils.send(player, "");
								PlayerUtils.send(player, "&e&lCongratulations! &3You have been promoted to &fMember&3 for " +
										"playing for &e24 hours &3in-game. You are now eligible for &c/trusted&3.");
								PlayerUtils.send(player, "");
								PlayerUtils.send(player, "&6&lThank you for flying Bear Nation!");
							});
						}
					}
				} catch (Exception ex) {
					Nexus.warn("Error in Hours scheduler: " + ex.getMessage());
				}
			}
		});
	}
}


