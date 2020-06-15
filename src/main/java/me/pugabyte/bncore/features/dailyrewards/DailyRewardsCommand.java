package me.pugabyte.bncore.features.dailyrewards;

import me.pugabyte.bncore.features.menus.MenuUtils.ConfirmationMenu;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.CooldownException;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.models.dailyreward.DailyReward;
import me.pugabyte.bncore.models.dailyreward.DailyRewardService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

@Aliases({"dr", "dailyreward"})
@Permission("daily.rewards")
public class DailyRewardsCommand extends CustomCommand {
	private DailyRewardService service = new DailyRewardService();
	private DailyReward dailyReward;

	public DailyRewardsCommand(CommandEvent event) {
		super(event);
		if (sender() instanceof Player)
			dailyReward = service.get(player());
	}

	@Path
	void main() {
		if (WorldGroup.SURVIVAL != WorldGroup.get(player()))
			error("&cYou must be in the survival worlds to claim this reward.");

		DailyRewardsFeature.menu(player(), dailyReward);
	}

	@Path("getAll")
	@Permission("getAll")
	void getAll() {
		for (DailyReward reward : service.getAll())
			send("Object: " + reward);
	}

	@Path("dailyreset")
	@Permission("dailyreset")
	void dailyReset() {
		console();
		DailyRewardsFeature.dailyReset();
	}

	@Path("streak [player]")
	void streak(@Arg("self") OfflinePlayer player) {
		int streak = dailyReward.getStreak();
		if (!player().equals(player)) {
			streak = ((DailyReward) service.get(player)).getStreak();
		}
		send(PREFIX + player.getName() + "'s streak: &e" + streak);
	}

	@Path("today [player]")
	void today(@Arg("self") OfflinePlayer player) {
		boolean earnedToday = dailyReward.isEarnedToday();
		if (!isSelf(player))
			earnedToday = ((DailyReward) service.get(player)).isEarnedToday();

		send(PREFIX + player.getName() + " has " + (earnedToday ? "&e" : "&cnot ") + "earned &3today's reward");
	}

	@Path("unclaim <player> <day>")
	@Permission("modify")
	void unclaim(OfflinePlayer player, int day) {
		dailyReward = service.get(player);
		dailyReward.unclaim(day);
		service.save(dailyReward);
		send(PREFIX + "Unclaimed day " + day + " for player " + player.getName());
	}

	@Path("set <player> <day>")
	@Permission("modify")
	void setDay(OfflinePlayer player, int day) {
		dailyReward = service.get(player);
		dailyReward.setStreak(day);
		service.save(dailyReward);
		send(PREFIX + "Streak set to " + dailyReward.getStreak() + " for player " + player.getName());
	}

	@Path("reset")
	void reset() {
		ConfirmationMenu.builder().onConfirm((e) -> {
			try {
				new CooldownService().check(player(), "dailyRewards-reset", Time.DAY);
				dailyReward.setActive(false);
				service.save(dailyReward);
				e.getPlayer().sendMessage(PREFIX + "Your streak has been cleared; you will be able to begin claiming rewards again tomorrow.");
				e.getPlayer().closeInventory();
			} catch (CooldownException ex) {
				e.getPlayer().sendMessage(colorize(ex.getMessage()));
			}
		})
		.open(player());
	}

	@Path("top [page]")
	void top(@Arg("1") int page) {
		Tasks.async(() -> {
			List<DailyReward> results = service.getPage(page);
			if (results.size() == 0) {
				send(PREFIX + "&cNo results on page " + page);
				return;
			}

			send("");
			send(PREFIX + "Top streaks:");
			int i = (page - 1) * 10 + 1;
			for (DailyReward dailyReward : results) {
				send("&3" + i + " &e" + dailyReward.getPlayer().getName() + " &7- " + dailyReward.getStreak());
				++i;
			}
		});
	}

}
