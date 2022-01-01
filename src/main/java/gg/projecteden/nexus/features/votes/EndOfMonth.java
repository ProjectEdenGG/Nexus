package gg.projecteden.nexus.features.votes;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.features.crates.models.CrateType;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.voter.TopVoter;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.DiscordId.User;
import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EndOfMonth {

	public static CompletableFuture<Void> run() {
		return run(YearMonth.now().minusMonths(1));
	}

	public static CompletableFuture<Void> run(YearMonth yearMonth) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		Tasks.async(() -> {
			try {
				TopVoterData data = new TopVoterData(yearMonth);

				final String discordMessage = data.getDiscordMessage();
				System.out.println(discordMessage);
				Koda.announce(discordMessage);
				Votes.write();

				if (data.getMysteryChestWinner() != null)
					CrateType.MYSTERY.give(PlayerUtils.getPlayer(data.getMysteryChestWinner().getVoter()));

				Tasks.sync(() -> {
					BankerService bankerService = new BankerService();
					data.getEco30kWinners().forEach(topVoter -> bankerService.deposit(topVoter.getVoter(), 30000, ShopGroup.SURVIVAL, TransactionCause.VOTE_REWARD));
					data.getEco20kWinners().forEach(topVoter -> bankerService.deposit(topVoter.getVoter(), 20000, ShopGroup.SURVIVAL, TransactionCause.VOTE_REWARD));
					data.getEco15kWinners().forEach(topVoter -> bankerService.deposit(topVoter.getVoter(), 15000, ShopGroup.SURVIVAL, TransactionCause.VOTE_REWARD));

					future.complete(null);
				});
			} catch (NexusException ex) {
				Nexus.warn("[Votes] [End Of Month] " + ex.getMessage());
			}
		});

		return future;
	}

	@Data
	public static class TopVoterData {
		@NonNull
		private YearMonth yearMonth;
		@NonNull
		private List<TopVoter> topVoters;
		private int total;
		private List<Integer> scores;
		private List<TopVoter> first;
		private List<TopVoter> second;
		private List<TopVoter> third;
		private List<TopVoter> npcOrHoloWinners;
		private List<TopVoter> eco30kWinners;
		private List<TopVoter> eco20kWinners;
		private List<TopVoter> eco15kWinners;
		private TopVoter mysteryChestWinner;

		public TopVoterData(@NotNull YearMonth yearMonth) {
			this.yearMonth = yearMonth;
			compute(new VoterService().getTopVoters(yearMonth));
		}

		private void compute(@NonNull List<TopVoter> topVoters) {
			this.topVoters = topVoters;

			total = topVoters.stream().map(TopVoter::getCount).mapToInt(Integer::valueOf).sum();
			scores = topVoters.stream().map(TopVoter::getCount).distinct().collect(Collectors.toList());
			if (scores.size() < 3)
				throw new NexusException("Not enough top scores, something must be wrong. (Scores: " + scores + ")");

			first = getVotersAt(scores.get(0));
			second = getVotersAt(scores.get(1));
			third = getVotersAt(scores.get(2));

			npcOrHoloWinners = getVotersWith(125);
			eco30kWinners = getVotersWith(100);
			eco20kWinners = getVotersBetween(75, 99);
			eco15kWinners = getVotersBetween(50, 74);

			mysteryChestWinner = RandomUtils.randomElement(getVotersWith(100).stream()
					.filter(topVoter -> !first.contains(topVoter) && !second.contains(topVoter) && !third.contains(topVoter))
					.collect(Collectors.toList()));
		}

		public List<TopVoter> getVotersWith(int count) {
			return topVoters.stream().filter(topVoter -> topVoter.getCount() >= count).collect(Collectors.toList());
		}

		public List<TopVoter> getVotersBetween(int min, int max) {
			return topVoters.stream().filter(topVoter -> topVoter.getCount() >= min && topVoter.getCount() <= max).collect(Collectors.toList());
		}

		private List<TopVoter> getVotersAt(int count) {
			return topVoters.stream().filter(topVoter -> topVoter.getCount() == count).collect(Collectors.toList());
		}

		public String getAsString(TopVoter topVoter) {
			return getAsString(Collections.singletonList(topVoter));
		}

		public String getAsString(List<TopVoter> topVoters) {
			String names = null;
			if (topVoters.size() > 0)
				names = topVoters.stream()
						.filter(Objects::nonNull)
						.map(topVoter -> Name.of(PlayerUtils.getPlayer(topVoter.getVoter())))
						.collect(Collectors.joining(", "));
			if (names == null || names.length() == 0)
				return "None :(";
			return names;
		}

		public String getDiscordMessage() {
			String msg = "";
			msg += "***Time to congratulate the Top Voters of " + StringUtils.camelCase(yearMonth.getMonth().name()) + "!***";
			msg += System.lineSeparator();
			msg += System.lineSeparator();
			msg += ":first_place:   **First place** ($10/$60,000/3 MC): " + getAsString(first) + " (" + first.get(0).getCount() + ")";
			msg += System.lineSeparator();
			msg += ":second_place:   **Second place** ($5/$45,000/2 MC): " + getAsString(second) + " (" + second.get(0).getCount() + ")";
			msg += System.lineSeparator();
			msg += ":third_place:   **Third place** ($35,000/1 MC): " + getAsString(third) + " (" + third.get(0).getCount() + ")";
			msg += System.lineSeparator();
			msg += System.lineSeparator();
			msg += "(Note: Rewards are (Store Credit/In-Game Money/# of Mystery Chests) - you can choose only one)";
			msg += System.lineSeparator();
			msg += System.lineSeparator();
			msg += ":gift:   **Lucky mystery chest winner:** " + getAsString(mysteryChestWinner) + (mysteryChestWinner == null ? "" : " (" + mysteryChestWinner.getCount() + ")");
			msg += System.lineSeparator();
			msg += System.lineSeparator();
			msg += ":walking: :speech_balloon:   **NPC or Hologram award:** " + getAsString(npcOrHoloWinners);
			msg += System.lineSeparator();
			msg += System.lineSeparator();
			msg += "Message <@" + User.GRIFFIN.getId() + "> to get your reward if you have won something above! (The below economy rewards are automatically applied)";
			msg += System.lineSeparator();
			msg += System.lineSeparator();
			msg += ":gem:   $30,000 bonus: " + getAsString(eco30kWinners);
			msg += System.lineSeparator();
			msg += ":moneybag:   $20,000 bonus: " + getAsString(eco20kWinners);
			msg += System.lineSeparator();
			msg += ":dollar:   $15,000 bonus: " + getAsString(eco15kWinners);
			msg += System.lineSeparator();
			msg += System.lineSeparator();

			if (total > Votes.GOAL)
				msg += "**You've reached the server wide voting goal, congratulations!** Stay tuned for further information from a staff member.";
			else
				msg += "**Unfortunately, the server wide goal was not reached. Get voting this month!**";

			msg += System.lineSeparator();
			msg += System.lineSeparator();
			msg += "**<" + EdenSocialMediaSite.WEBSITE.getUrl() + "/vote>**";
			return msg;
		}
	}
}
