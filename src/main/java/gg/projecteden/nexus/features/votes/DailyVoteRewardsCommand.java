package gg.projecteden.nexus.features.votes;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.crate.CrateType;
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

@Aliases({"dailyvotereward", "dvr"})
@Description("Earn rewards for voting consistently")
public class DailyVoteRewardsCommand extends CustomCommand {
	private final DailyVoteRewardService service = new DailyVoteRewardService();

	public DailyVoteRewardsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("streak [player]")
	@Description("View a player's voting streak")
	void streak(@Optional("self") @Permission(Group.STAFF) DailyVoteReward user) {
		send(PREFIX + (isSelf(user) ? "Your" : user.getNickname() + "'s") + " streak is &e" + user.getCurrentStreak().getStreak());
	}

	@Path("today [player]")
	@Description("Check whether you have advanced your streak today")
	void today(@Optional("self") @Permission(Group.STAFF) DailyVoteReward user) {
		boolean earnedToday = user.getCurrentStreak().isEarnedToday();
		send(PREFIX + (isSelf(user) ? "You have " : user.getNickname() + " has ") + (earnedToday ? "&e" : "&cnot ") + "advanced your streak today");
	}

	@Path("top [page]")
	@Description("View the vote streak leaderboard")
	void streak(@Optional("1") int page) {
		final List<DailyVoteReward> all = service.getAll().stream()
			.filter(rewards -> rewards.getCurrentStreak().getStreak() > 0)
			.sorted(Comparator.<DailyVoteReward>comparingInt(rewards -> rewards.getCurrentStreak().getStreak()).reversed())
			.collect(Collectors.toList());

		final BiFunction<DailyVoteReward, String, JsonBuilder> formatter = (rewards, index) ->
			json(index + " " + Nerd.of(rewards).getColoredName() + " &7- " + rewards.getCurrentStreak().getStreak());
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
	@Description("View the vote streak rewards")
	void rewards() {
		send(PREFIX + "Rewards:");
		for (VoteStreakReward reward : VoteStreakReward.values())
			send("&e" + camelCase(reward) + " &7- " + reward.getAmount() + " " + camelCase(reward.getCrateType()) + " Crate Keys");
	}

	/*
	@Path("fix")
	void fix() {
		final VoterService voterService = new VoterService();

		for (DailyVoteStreak streak : List.of(
			DailyVoteStreak.builder()
				.uuid(UUID.fromString("97254020-8a09-43f3-ad3e-b90bc4b3957a"))
				.streak(9)
				.start(LocalDate.of(2021, 12, 30))
				.build(),

			DailyVoteStreak.builder()
				.uuid(UUID.fromString("32d5d29d-202a-4713-b6c3-0e255e89e571"))
				.streak(1)
				.start(LocalDate.of(2022, 1, 7))
				.build(),

			DailyVoteStreak.builder()
				.uuid(UUID.fromString("b83bae78-83d6-43a0-9316-014a0a702ab2"))
				.streak(20)
				.start(LocalDate.of(2021, 12, 19))
				.build(),

			DailyVoteStreak.builder()
				.uuid(UUID.fromString("27c0dcae-9643-4bdd-bb3d-34216d14761c"))
				.streak(4)
				.start(LocalDate.of(2022, 1, 4))
				.build(),

			DailyVoteStreak.builder()
				.uuid(UUID.fromString("a7fa3c9c-d3cb-494e-bff6-8b6d416b18e3"))
				.streak(1)
				.start(LocalDate.of(2022, 1, 7))
				.build()
		)) {
			final Voter voter = voterService.get(streak.getUuid());
			final DailyVoteReward voteStreak = new DailyVoteRewardService().get(voter);

			final int yesterdaysVotes = voter.getVotes(LocalDate.now().minusDays(1)).size();

			if (yesterdaysVotes >= 2) {
				voteStreak.setCurrentStreak(streak);
				voteStreak.getCurrentStreak().incrementStreak();
				service.save(voteStreak);
			}
		}

		for (Voter voter : voterService.getAll()) {
			final DailyVoteReward streak = new DailyVoteRewardService().get(voter);
			if (voter.getTodaysVotes().size() >= 2) {
				streak.getCurrentStreak().setEarnedToday(true);
				service.save(streak);
			}
		}
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
