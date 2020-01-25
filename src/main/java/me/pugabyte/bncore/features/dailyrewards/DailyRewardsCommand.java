package me.pugabyte.bncore.features.dailyrewards;

import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.dailyrewards.DailyRewards;
import me.pugabyte.bncore.models.dailyrewards.DailyRewardsService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

@Aliases({"dr", "dailyreward"})
@Permission("daily.rewards")
public class DailyRewardsCommand extends CustomCommand {
	private DailyRewardsService service = new DailyRewardsService();
	private DailyRewards dailyRewards;

	public DailyRewardsCommand(CommandEvent event) {
		super(event);
		if (sender() instanceof Player)
			dailyRewards = service.get(player());
	}

	@Path
	void main() {
		if (WorldGroup.SURVIVAL != WorldGroup.get(player().getWorld()))
			error("&cYou must be in the survival worlds to claim this reward.");

		DailyRewardsFeature.menu(player(), dailyRewards);
	}

	@Path("dailyreset")
	@Permission("dailyreset")
	void dailyReset() {
		console();
		List<DailyRewards> dailyRewards = service.getAll();
		for (DailyRewards dailyReward : dailyRewards) {
			if (!dailyReward.isEarnedToday()) {
				dailyReward.setStreak(0);
				dailyReward.setClaimed(null);
			}

			dailyReward.setEarnedToday(false);
			if (dailyReward.getPlayer().isOnline()) {
				dailyReward.setEarnedToday(true);
				dailyReward.increaseStreak();
			}

			service.save(dailyReward);
		}
	}

	@Path("streak [player]")
	void streak(@Arg("self") OfflinePlayer player) {
		int streak = dailyRewards.getStreak();
		if (!player().getUniqueId().equals(player.getUniqueId())) {
			streak = ((DailyRewards) service.get(playerArg(2))).getStreak();
		}
		send(PREFIX + player.getName() + "'s streak: &e" + streak);
	}

	@Path("today [player]")
	void today(@Arg("self") OfflinePlayer player) {
		boolean earnedToday = dailyRewards.isEarnedToday();
		if (!isSelf(player))
			earnedToday = ((DailyRewards) service.get(player)).isEarnedToday();

		send(PREFIX + player.getName() + " has " + (earnedToday ? "&e" : "&cnot ") + "earned &3today's reward");
	}

	// TODO: Optional arguments in the middle if default value exists
	// TODO: Conditional default values? e.g. /speed [type = isFlying ? fly : walk] <int>
	@Path("unclaim <player> <day>")
	@Permission("modify")
	void unclaim(OfflinePlayer player, int day) {
		dailyRewards = service.get(player);
		dailyRewards.unclaim(day);
		service.save(dailyRewards);
		send(PREFIX + "Unclaimed day " + day + " for player " + player.getName());
	}

	@Path("set <player> <day>")
	@Permission("modify")
	void setDay(OfflinePlayer player, int day) {
		dailyRewards = service.get(player);
		dailyRewards.setStreak(day);
		service.save(dailyRewards);
		send(PREFIX + "Streak set to " + dailyRewards.getStreak() + " for player " + player.getName());
	}

	@Path("reset")
	void reset() {
		MenuUtils.ConfirmationMenu confirm = MenuUtils.ConfirmationMenu.builder().onConfirm((e) -> {
			dailyRewards.reset();
			service.save(dailyRewards);
			e.getPlayer().sendMessage(PREFIX + "Your streak has been cleared; you will be able to begin claiming rewards again tomorrow.");
			e.getPlayer().closeInventory();
		}).build();

		MenuUtils.confirmMenu(player(), confirm);
	}

	@Path("top [page]")
	void top(@Arg("1") int page) {
		Tasks.async(() -> {
			List<DailyRewards> results = service.getPage(page);
			if (results.size() == 0) {
				send(PREFIX + "&cNo results on page " + page);
				return;
			}

			send("");
			send(PREFIX + "Top streaks:");
			int i = (page - 1) * 10 + 1;
			for (DailyRewards dailyRewards : results) {
				send("&3" + i + " &e" + dailyRewards.getPlayer().getName() + " &7- " + dailyRewards.getStreak());
				++i;
			}
		});
	}

}
