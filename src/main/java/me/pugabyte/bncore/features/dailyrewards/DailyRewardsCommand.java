package me.pugabyte.bncore.features.dailyrewards;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.dailyrewards.DailyRewards;
import me.pugabyte.bncore.models.dailyrewards.DailyRewardsService;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

@Aliases({"jdailyrewards", "jdr"})
@Permission("daily.rewards")
@NoArgsConstructor
public class DailyRewardsCommand extends CustomCommand {
	private DailyRewardsService service = new DailyRewardsService();
	private DailyRewards dailyRewards;

	public DailyRewardsCommand(CommandEvent event) {
		super(event);
		if (sender() instanceof Player)
			dailyRewards = (DailyRewards) service.get(player());
	}

	@Path
	void main() {
		DailyRewardsFeature.menu(player(), dailyRewards);
	}

	@Path("dailyreset")
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

	@Path("streak {offlineplayer}")
	void streak(@Arg("self") OfflinePlayer player) {
		int streak = dailyRewards.getStreak();
		if (!player().getUniqueId().equals(player.getUniqueId())) {
			streak = ((DailyRewards) service.get(playerArg(2))).getStreak();
		}
		reply(PREFIX + player.getName() + "'s streak: &e" + streak);
	}

	@Path("today {offlineplayer}")
	void today(@Arg("self") OfflinePlayer player) {
		boolean earnedToday = dailyRewards.isEarnedToday();
		if (!player().getUniqueId().equals(player.getUniqueId())) {
			earnedToday = ((DailyRewards) service.get(player)).isEarnedToday();
		}
		reply(PREFIX + player.getName() + " has "  + (earnedToday ? "&e" : "&cnot ") + "earned &3today's reward");
	}

	// TODO: Optional arguments in the middle if default value exists
	// TODO: Conditional default values? e.g. /speed [type = isFlying ? fly : walk] <int>
	@Path("unclaim {player} {int}")
	@Permission("unclaim")
	void unclaim(@Arg Player player, @Arg int day) {
		dailyRewards = ((DailyRewards) service.get(player));
		dailyRewards.unclaim(day);
		service.save(dailyRewards);
		reply(PREFIX + "Unclaimed day " + day + " for player " + player.getName());
	}

	@Path("reset")
	void reset() {
		// TODO: Write abstract confirmation menu
	}

	@Path("confirmreset")
	void confirmReset() {

	}

	@Path("top {int}")
	void top(@Arg("1") int page) {
		BNCore.async(() -> {
			List<DailyRewards> results = service.getPage(page);
			if (results.size() == 0) {
				reply(PREFIX + "&cNo results on page " + page);
				return;
			}

			reply("");
			reply(PREFIX + "Top streaks:");
			int i = (page - 1) * 10 + 1;
			for (DailyRewards dailyRewards : results) {
				reply("&3" + i + " &e" + dailyRewards.getPlayer().getName() + " &7- " + dailyRewards.getStreak());
				++i;
			}
		});
	}

}
