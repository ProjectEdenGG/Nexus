package me.pugabyte.bncore.features.dailyrewards;

import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.dailyreward.DailyReward;
import me.pugabyte.bncore.models.dailyreward.DailyRewardService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

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
		if (WorldGroup.SURVIVAL != WorldGroup.get(player().getWorld()))
			error("&cYou must be in the survival worlds to claim this reward.");

		DailyRewardsFeature.menu(player(), dailyReward);
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
			streak = ((DailyReward) service.get(playerArg(2))).getStreak();
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
		MenuUtils.ConfirmationMenu confirm = MenuUtils.ConfirmationMenu.builder().onConfirm((e) -> {
			dailyReward.reset();
			service.save(dailyReward);
			e.getPlayer().sendMessage(PREFIX + "Your streak has been cleared; you will be able to begin claiming rewards again tomorrow.");
			e.getPlayer().closeInventory();
		}).build();

		MenuUtils.confirmMenu(player(), confirm);
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
