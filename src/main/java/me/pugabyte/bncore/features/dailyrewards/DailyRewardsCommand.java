package me.pugabyte.bncore.features.dailyrewards;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.commands.models.CustomCommand;
import me.pugabyte.bncore.models.commands.models.annotations.Aliases;
import me.pugabyte.bncore.models.commands.models.annotations.Arg;
import me.pugabyte.bncore.models.commands.models.annotations.Path;
import me.pugabyte.bncore.models.commands.models.annotations.Permission;
import me.pugabyte.bncore.models.commands.models.events.CommandEvent;
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
				dailyReward.setClaimed("");
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
	void streak() {
		int streak = dailyRewards.getStreak();
		String name = sender().getName();
		OfflinePlayer playerArg = playerArg(2);
		if (playerArg == null) player();
		else {
			streak = ((DailyRewards) service.get(playerArg(2))).getStreak();
			name = playerArg.getName();
		}
		reply(PREFIX + name + "'s streak: &e" + streak);
	}

	@Path("today [player]")
	void today() {
		boolean earnedToday = dailyRewards.isEarnedToday();
		String name = sender().getName();
		OfflinePlayer playerArg = playerArg(2);
		if (playerArg == null) player();
		else {
			earnedToday = ((DailyRewards) service.get(playerArg)).isEarnedToday();
			name = playerArg.getName();
		}
		reply(PREFIX + name + " has "  + (earnedToday ? "&e" : "&cnot ") + "earned &3today's reward");
	}

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
				// Cant throw inside lambdas...
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
