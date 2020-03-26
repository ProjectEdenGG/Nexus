package me.pugabyte.bncore.features.votes;

import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.exceptions.BNException;
import me.pugabyte.bncore.models.vote.TopVoter;
import me.pugabyte.bncore.models.vote.VoteService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EndOfMonth {

	@lombok.Data
	private static class Data {
		@NonNull
		private List<TopVoter> topVoters;
		private List<Integer> scores;
		private List<TopVoter> first;
		private List<TopVoter> second;
		private List<TopVoter> third;
		private List<TopVoter> npcOrHoloWinners;
		private List<TopVoter> eco30kWinners;
		private List<TopVoter> eco20kWinners;
		private List<TopVoter> eco15kWinners;
		private TopVoter mysteryChestWinner;

		public Data(Month month) {
			this(new VoteService().getTopVoters(month));
		}

		public Data(@NonNull List<TopVoter> topVoters) {
			this.topVoters = topVoters;

			scores = topVoters.stream().map(TopVoter::getCount).map(Long::intValue).distinct().collect(Collectors.toList());
			if (scores.size() < 3)
				throw new BNException("Not enough top scores, something must be wrong. (Scores: " + scores + ")");

			first = getPlace(scores.get(0));
			second = getPlace(scores.get(1));
			third = getPlace(scores.get(2));

			npcOrHoloWinners = getVotersWith(125);
			eco30kWinners = getVotersWith(100);
			eco20kWinners = getVotersWith(75);
			eco15kWinners = getVotersWith(50);

			mysteryChestWinner = Utils.getRandomElement(getVotersWith(100).stream()
					.filter(topVoter -> !first.contains(topVoter) && !second.contains(topVoter) && !third.contains(topVoter))
					.collect(Collectors.toList()));
		}

		public List<TopVoter> getVotersWith(int count) {
			return topVoters.stream().filter(topVoter -> topVoter.getCount() > count).collect(Collectors.toList());
		}

		public String getAsString(List<TopVoter> topVoters) {
			return topVoters.stream()
					.map(TopVoter::getUuid)
					.map(UUID::fromString)
					.map(Bukkit::getOfflinePlayer)
					.map(OfflinePlayer::getName)
					.collect(Collectors.joining(", "));
		}

		private List<TopVoter> getPlace(int i) {
			return topVoters.stream().filter(topVoter -> topVoter.getCount() == i).collect(Collectors.toList());
		}
	}

	static void run() {
		Tasks.async(() -> {
			try {
				Month month = LocalDateTime.now().getMonth(); //.minus(1);
				Data data = new Data(month);
				BNCore.log(data.toString());

				updateNpcs(data);

			} catch (BNException ex) {
				BNCore.warn("[Votes] [End Of Month] " + ex.getMessage());
			}
		});
	}

	private static void updateNpcs(Data data) {
		// TODO
	}
}
