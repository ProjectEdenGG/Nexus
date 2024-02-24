package gg.projecteden.nexus.features.votes.party;

import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.parchment.HasOfflinePlayer;

public enum RewardTier {
	COMMON {
		@Override
		public void giveReward(HasOfflinePlayer player) {
			RandomUtils.randomElement(
				VotePartyReward.SIMPLE_BOOST,
				VotePartyReward.VOTE_KEYS,
				VotePartyReward.ECO
			).give(player);
		}
	},
	UNCOMMON {
		@Override
		public void giveReward(HasOfflinePlayer player) {
			RandomUtils.randomElement(
				VotePartyReward.SIMPLE_BOOST,
				VotePartyReward.VOTE_KEYS,
				VotePartyReward.EVENT_TOKENS,
				VotePartyReward.ECO
			).give(player);

		}
	},
	RARE {
		@Override
		public void giveReward(HasOfflinePlayer player) {
			RandomUtils.randomElement(
				VotePartyReward.GOOD_BOOST,
				VotePartyReward.MORE_VOTE_KEYS,
				VotePartyReward.ECO,
				VotePartyReward.EVENT_TOKENS,
				VotePartyReward.PINATA
			).give(player);
		}
	},
	EPIC {
		@Override
		public void giveReward(HasOfflinePlayer player) {
			RandomUtils.randomElement(
				VotePartyReward.GREAT_BOOST,
				VotePartyReward.MORE_ECO,
				VotePartyReward.MORE_VOTE_KEYS,
				VotePartyReward.EVENT_TOKENS,
				VotePartyReward.PINATA
			).give(player);
		}
	},
	LEGENDARY {
		@Override
		public void giveReward(HasOfflinePlayer player) {
			RandomUtils.randomElement(
				VotePartyReward.GREAT_BOOST,
				VotePartyReward.MORE_VOTE_KEYS,
				VotePartyReward.MORE_ECO,
				VotePartyReward.MYSTERY_CRATE_KEY,
				VotePartyReward.EVENT_TOKENS,
				VotePartyReward.PINATA
			).give(player);
		}
	};

	public static RewardTier of(int count) {
		if (count == 0)
			return null;

		int maxDailyVotes = 3;
		int maxWeeklyVotes = maxDailyVotes * 7;

		for (int i = 0; i < RewardTier.values().length; i++) {
			RewardTier tier = RewardTier.values()[i];
			if (count <= maxWeeklyVotes * (i + 1) / RewardTier.values().length)
				return tier;
		}
		return null;
	}

	public abstract void giveReward(HasOfflinePlayer player);

}
