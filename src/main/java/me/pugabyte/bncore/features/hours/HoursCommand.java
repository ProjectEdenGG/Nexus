package me.pugabyte.bncore.features.hours;

import me.pugabyte.bncore.Utils;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import org.bukkit.OfflinePlayer;

import java.util.List;

@Aliases("jhours")
public class HoursCommand extends CustomCommand {
	private HoursService service = new HoursService();
	private Hours hours;

	public HoursCommand(CommandEvent event) {
		super(event);
		hours = (Hours) service.get(player());
	}

	@Path
	void hours() {
		reply("Total: " + Utils.timespanFormat(hours.getTotal()));
		reply("Daily: " + Utils.timespanFormat(hours.getDaily()));
		reply("Weekly: " + Utils.timespanFormat(hours.getWeekly()));
		reply("Monthly: " + Utils.timespanFormat(hours.getMonthly()));
	}

	@Path("{offlineplayer}")
	void player(@Arg OfflinePlayer player) {
		hours = (Hours) service.get(player);
		hours();
	}

	@Path("top {int}")
	void top(@Arg("1") int page) {
		top("total", page);
	}

	@Path("top {string} {int}")
	void top(@Arg("total") String type, @Arg("1") int page) {
		Utils.async(() -> {
			try {
				final HoursService.HoursType hoursType = service.getType(type);

				List<Hours> results = service.getPage(hoursType, page);
				if (results.size() == 0) {
					reply(PREFIX + "&cNo results on page " + page);
					return;
				}

				reply("");
				reply(PREFIX + " Total: " + Utils.timespanFormat(service.total(hoursType)));
				int i = (page - 1) * 10 + 1;
				for (Hours hours : results)
					reply("&3" + i++ + " &e" + hours.getPlayer().getName() + " &7- " + Utils.timespanFormat(hours.get(hoursType)));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}
}
