package gg.projecteden.nexus.features.votes;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.votes.party.VotePartyReward;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.models.dailyvotereward.DailyVoteReward;
import gg.projecteden.nexus.models.dailyvotereward.DailyVoteReward.DailyVoteStreak;
import gg.projecteden.nexus.models.dailyvotereward.DailyVoteRewardService;
import gg.projecteden.nexus.models.mail.Mailer;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
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
	void streak(@Arg(value = "self", permission = Group.STAFF) DailyVoteReward user) {
		send(PREFIX + (isSelf(user) ? "Your" : user.getNickname() + "'s") + " streak is &e" + user.getCurrentStreak().getStreak());
	}

	@Path("today [player]")
	@Description("Check whether you have advanced your streak today")
	void today(@Arg(value = "self", permission = Group.STAFF) DailyVoteReward user) {
		boolean earnedToday = user.getCurrentStreak().isEarnedToday();
		send(PREFIX + (isSelf(user) ? "You have " : user.getNickname() + " has ") + (earnedToday ? "&e" : "&cnot ") + "advanced your streak today");
	}

	@Path("top [page]")
	@Description("View the vote streak leaderboard")
	void streak(@Arg("1") int page) {
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
		send(" &eDay 3 &7- 5 Extra Vote Points");
		send(" &eDay 5 &7- 5 Vote Crate Keys");
		send(" &eDay 10 &7- 10 Vote Crate Keys");
		send(" &eDay 15 &7- 20 Vote Crate Keys");
		send(" &eDay 30 &7- 1 Random Personal Boost");
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
		DAY_3(player -> {
			new VoterService().edit(player, voter -> voter.givePoints(5));
			Mailer.Mail.fromServer(player, WorldGroup.SURVIVAL, "You received 5 extra vote points for your Vote Streak Reward");
		}),
		DAY_5(player -> giveVoteCrateKeys(player,  5,5)),
		DAY_10(player -> giveVoteCrateKeys(player,  10,10)),
		DAY_15(player -> giveVoteCrateKeys(player,  15,20)),
		DAY_30(player -> {
			VotePartyReward.GREAT_BOOST.give(PlayerUtils.getPlayer(player));
			Mailer.Mail.fromServer(player, WorldGroup.SURVIVAL, "You received a random personal boost for your Vote Streak Reward");
		}),
		;

		private final Consumer<UUID> onAchieve;

		public int getDay() {
			return Integer.parseInt(name().replace("DAY_", ""));
		}

		private static void giveVoteCrateKeys(UUID player, int streak, int amount) {
			Mailer.Mail.fromServer(
				player,
				WorldGroup.SURVIVAL,
				"Vote Streak Reward (Day #" + streak + ")",
				new ItemBuilder(CrateType.VOTE.getKey()).amount(amount).build()
			).send();
		}

	}

}
