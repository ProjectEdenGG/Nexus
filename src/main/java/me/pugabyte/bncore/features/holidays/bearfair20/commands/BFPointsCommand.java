package me.pugabyte.bncore.features.holidays.bearfair20.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.bearfair.BFPointsService;
import me.pugabyte.bncore.models.bearfair.BFPointsUser;
import me.pugabyte.bncore.models.bearfair.BFPointsUser.BFPointSource;
import me.pugabyte.bncore.utils.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Aliases("bfp")
public class BFPointsCommand extends CustomCommand {
	BFPointsService service = new BFPointsService();

	public BFPointsCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	public void checkTotal(@Arg("self") BFPointsUser user) {
		if (player().equals(user.getPlayer()))
			send(PREFIX + "&3Total: &e" + user.getTotalPoints());
		else
			send(PREFIX + "&3" + user.getPlayer().getName() + "'s Total: &e" + user.getTotalPoints());
	}

	@Path("daily [player]")
	public void checkDaily(@Arg("self") BFPointsUser user) {
		if (player().equals(user.getPlayer()))
			send(PREFIX + "&3Daily Points:");
		else
			send(PREFIX + "&3" + user.getPlayer().getName() + "'s Daily Points:");

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
	public void givePoints(BFPointsUser user, int points) {
		user.givePoints(points);
		service.save(user);
		String plural = points == 1 ? " point" : " points";
		send(PREFIX + "&e" + points + plural + " &3given to &e" + user.getPlayer().getName());
	}

	@Path("take <player> <points>")
	@Permission("group.admin")
	public void takePoints(BFPointsUser user, int points) {
		user.takePoints(points);
		service.save(user);
		String plural = points == 1 ? " point" : " points";
		send(PREFIX + "&e" + points + plural + " &3taken from &e" + user.getPlayer().getName());
	}

	@Path("set <player> <points>")
	@Permission("group.admin")
	public void setPoints(BFPointsUser user, int points) {
		user.setTotalPoints(points);
		service.save(user);
		String plural = points == 1 ? " point" : " points";
		send(PREFIX + "&3set &e" + user.getPlayer().getName() + "&3 to &e" + points + plural);
	}

	@Path("reset <player>")
	@Permission("group.admin")
	public void reset(BFPointsUser user) {
		user.setTotalPoints(0);
		user.getPointsReceivedToday().clear();
		service.save(user);
	}

	@Path("top [page]")
	public void top(@Arg("1") int page) {
		List<BFPointsUser> results = service.getTop(page);
		if (results.size() == 0)
			error("&cNo results on page " + page);

		send("");
		send(PREFIX + (page > 1 ? "&e  |  &3Page " + page : ""));
		int i = (page - 1) * 10 + 1;
		for (BFPointsUser user : results)
			send("&3" + i++ + " &e" + user.getPlayer().getName() + " &7- " + user.getTotalPoints());
	}

	@ConverterFor(BFPointsUser.class)
	BFPointsUser convertToBFPointsUser(String value) {
		return new BFPointsService().get(convertToOfflinePlayer(value));
	}

	@TabCompleterFor(BFPointsUser.class)
	List<String> tabCompleteBFPointsUser(String value) {
		return tabCompletePlayer(value);
	}

}
