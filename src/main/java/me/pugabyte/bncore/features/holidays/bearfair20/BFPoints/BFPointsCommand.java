package me.pugabyte.bncore.features.holidays.bearfair20.BFPoints;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.Map;

@Aliases("BFP")
public class BFPointsCommand extends CustomCommand {

	public BFPointsCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	public void checkTotal(@Arg("self") Player player) {
		BFPointsUser user = new BFPointsService().get(player.getUniqueId());
		if (player().equals(player))
			send(PREFIX + "&3Total: &e" + user.getTotalPoints());
		else
			send(PREFIX + "&3" + player.getName() + "'s Total: &e" + user.getTotalPoints());
	}

	@Path("daily [player]")
	public void checkDaily(@Arg("self") Player player) {
		BFPointsUser user = new BFPointsService().get(player.getUniqueId());
		if (player().equals(player))
			send(PREFIX + "&3Daily Points:");
		else
			send(PREFIX + "&3" + player.getName() + "'s Daily Points:");

		for (BFPointSource pointSource : BFPointSource.values()) {
			Map<LocalDate, Integer> dailyMap = user.getPointsReceivedToday().get(pointSource);
			int points = 0;
			if (dailyMap != null)
				points = dailyMap.get(LocalDate.now());

			int dailyMax = BFPointsUser.DAILY_SOURCE_MAX;
			String sourceColor = points == dailyMax ? "&a" : "&3";
			String sourceName = StringUtils.camelCase(pointSource.name());
			send(" " + sourceColor + sourceName + " &7- &e" + points + "&3/&e" + dailyMax);
		}
	}

	@Path("give <player> <points>")
	@Permission("group.admin")
	public void givePoints(Player player, int points) {
		BFPointsUser user = new BFPointsService().get(player.getUniqueId());
		user.givePoints(user, points);
		String plural = points == 1 ? " point" : " points";
		send(PREFIX + "&e" + points + plural + " &3given to &e" + player.getName());
	}

	@Path("take <player> <points>")
	@Permission("group.admin")
	public void takePoints(Player player, int points) {
		BFPointsUser user = new BFPointsService().get(player.getUniqueId());
		user.takePoints(user, points);
		String plural = points == 1 ? " point" : " points";
		send(PREFIX + "&e" + points + plural + " &3taken from &e" + player.getName());
	}

	@Path("set <player> <points>")
	@Permission("group.admin")
	public void setPoints(Player player, int points) {
		BFPointsUser user = new BFPointsService().get(player.getUniqueId());
		user.setTotalPoints(points);
		String plural = points == 1 ? " point" : " points";
		send(PREFIX + "&3set &e" + player.getName() + "&3 to &e" + points + plural);
	}

	@Path("reset <player>")
	@Permission("group.admin")
	public void reset(Player player) {
		BFPointsUser user = new BFPointsService().get(player.getUniqueId());
		user.setTotalPoints(0);
		user.getPointsReceivedToday().clear();
	}

}
