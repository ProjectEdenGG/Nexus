package me.pugabyte.bncore.features.hours;

import me.pugabyte.bncore.framework.annotations.Disabled;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;

@Disabled // TODO
public class StarTrekMoviesCommand extends CustomCommand {
	private final HoursService service = new HoursService();
	private final DecimalFormat nf = new DecimalFormat("#.00");
	private final double divisor = 60 * 116.53;

	public StarTrekMoviesCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void player(@Arg("self") OfflinePlayer player) {
		boolean isSelf = isSelf(player);

		Hours hours = service.get(player);
		send("");
		send(PREFIX + (isSelf ? "Your" : "&e" + player.getName() + "&3's") + " playtime in &eStar Trek Movies &3(116.53 minutes)");
		send("&3Total: &e" + nf.format(hours.getTotal() / divisor) + " movies");
		send("&7- &3Today: &e" + nf.format(hours.getDaily() / divisor) + " movies");
		send("&7- &3This month: &e" + nf.format(hours.getMonthly() / divisor) + " movies");
	}

	/*
	@Path("top")
	void top() {
		Tasks.async(() -> {
			String type = isIntArg(2) ? "total" : arg(2);
			int page = isIntArg(2) ? intArg(2) : isIntArg(3) ? intArg(3) : 1;

			try {
				final HoursService.HoursType hoursType = service.getType(type);

				List<Hours> results = service.getPage(hoursType, page);
				if (results.size() == 0) {
					send(PREFIX + "&cNo results on page " + page);
					return;
				}

				send("");
				send(PREFIX + "Total: " + nf.format(service.total(hoursType) / divisor) + " movies" + (page > 1 ? "&e  |  &3Page " + page : ""));
				int i = (page - 1) * 10 + 1;
				for (Hours hours : results)
					send("&3" + i++ + " &e" + hours.getPlayer().getName() + " &7- " + nf.format(hours.getTotal() / divisor) + " movies");
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		});
	}
	 */

}
