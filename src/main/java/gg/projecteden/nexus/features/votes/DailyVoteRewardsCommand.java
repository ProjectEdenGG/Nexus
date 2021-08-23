package gg.projecteden.nexus.features.votes;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.crates.models.CrateType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.dailyvotereward.DailyVoteReward;
import gg.projecteden.nexus.models.dailyvotereward.DailyVoteReward.DailyVoteStreak;
import gg.projecteden.nexus.models.dailyvotereward.DailyVoteRewardService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Aliases("dailyvotereward")
public class DailyVoteRewardsCommand extends CustomCommand {
	private final DailyVoteRewardService service = new DailyVoteRewardService();

	public DailyVoteRewardsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("streak [player]")
	void streak(@Arg("self") DailyVoteReward player) {
		send(PREFIX + (isSelf(player) ? "Your" : player.getNickname() + "'s") + " streak is &e" + player.getCurrentStreak().getStreak());
	}

	@Path("top [page]")
	void streak(@Arg("1") int page) {
		final List<DailyVoteReward> all = service.getAll().stream()
			.filter(rewards -> rewards.getCurrentStreak().getStreak() > 0)
			.sorted(Comparator.<DailyVoteReward>comparingInt(rewards -> rewards.getCurrentStreak().getStreak()).reversed())
			.collect(Collectors.toList());

		final BiFunction<DailyVoteReward, String, JsonBuilder> formatter = (rewards, index) ->
			json("&3" + index + " " + Nerd.of(rewards).getColoredName() + " &7- " + rewards.getCurrentStreak().getStreak());
		paginate(all, formatter, "/dailyvoterewards top", page);
	}

	public static void dailyReset() {
		final DailyVoteRewardService service = new DailyVoteRewardService();
		for (DailyVoteReward rewards : service.cacheAll()) {
			final DailyVoteStreak streak = rewards.getCurrentStreak();

			if (!streak.isEarnedToday()) {
				Nexus.log("[VoteStreak] Ending streak for " + rewards.getNickname() + " | " + streak);
				rewards.endStreak();
			} else {
				Nexus.log("[VoteStreak] Continuing streak for " + rewards.getNickname() + " | " + streak);
				streak.setEarnedToday(false);
			}

			service.save(rewards);
		}
	}

	@Path("rewards")
	void rewards() {
		send(PREFIX + "Rewards:");
		for (VoteStreakReward reward : VoteStreakReward.values())
			send("&e" + camelCase(reward) + " &7- " + reward.getKeys() + " " + reward.getCrateType() + " Crate Keys");
	}

	/*
	@Path("actualStreaks [page]")
	void actualStreaks(@Arg("1") int page) {
		final VoterService voterService = new VoterService();
		Map<UUID, Integer> streaks = new HashMap<>();
		for (DailyVoteReward reward : service.getAll()) {
			final Voter voter = voterService.get(reward);
			LocalDate date = LocalDate.now().minusDays(1);
			int streak = 0;
			while (voter.getVotes(date).size() >= 5 && date.isAfter(LocalDate.of(2021, 7, 31))) {
				++streak;
				date = date.minusDays(1);
			}

			streaks.put(voter.getUuid(), streak);
		}

		final BiFunction<UUID, String, JsonBuilder> formatter = (uuid, index) -> {
			final Integer streak = streaks.get(uuid);

			final DailyVoteStreak fixed = new DailyVoteStreak(uuid);
			fixed.setStart(LocalDate.now().minusDays(streak));
			fixed.setStreak(streak);
			if (voterService.get(uuid).getTodaysVotes().size() >= 5) {
				fixed.setStreak(streak + 1);
				fixed.setEarnedToday(true);
			}

			final DailyVoteReward reward = service.get(uuid);
			reward.setCurrentStreak(fixed);
			service.save(reward);

			return json(Nerd.of(uuid).getColoredName() + " &7- " + streak);
		};

		paginate(Utils.sortByValueReverse(streaks).keySet(), formatter, "/dailyvoterewards actualStreaks", page);
	}
	*/

	@Getter
	@AllArgsConstructor
	public enum VoteStreakReward {
		DAY_3(CrateType.VOTE, 5),
		DAY_5(CrateType.VOTE, 10),
		DAY_10(CrateType.VOTE, 20),
		DAY_15(CrateType.VOTE, 30),
		DAY_30(CrateType.MYSTERY, 1),
		;

		private final CrateType crateType;
		private final int amount;

		public int getDay() {
			return Integer.parseInt(name().replace("DAY_", ""));
		}

		public ItemStack getKeys() {
			return new ItemBuilder(crateType.getKey()).amount(amount).build();
		}
	}

}
