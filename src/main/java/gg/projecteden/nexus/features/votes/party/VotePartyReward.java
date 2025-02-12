package gg.projecteden.nexus.features.votes.party;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.features.crates.CratePinatas;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.models.boost.Booster;
import gg.projecteden.nexus.models.boost.BoosterService;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.offline.OfflineMessage;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.parchment.HasOfflinePlayer;

import java.util.Arrays;

public enum VotePartyReward {
	SIMPLE_BOOST {
		@Override
		public void give(HasOfflinePlayer player) {
			Boostable type = RandomUtils.randomElement(Arrays.stream(Boostable.values()).filter(boostable -> !boostable.isDisabled()).toList());
			Booster booster = BOOSTER_SERVICE.get(player.getOfflinePlayer());
			Booster.Boost boost = booster.add(
				type,
				RandomUtils.randomDouble(1.1, getMaxBoostMultiplier(type)),
				TimeUtils.TickTime.MINUTE.x(RandomUtils.randomInt(30, 90)) / 20,
				true
			);
			BOOSTER_SERVICE.save(booster);
			OfflineMessage.send(player.getOfflinePlayer(), getRewardMessage(getBoostMessage(boost), player));
		}

		public double getMaxBoostMultiplier(Boostable type) {
			return switch (type) {
				case EXPERIENCE, MINIGAME_DAILY_TOKENS, KILLER_MONEY -> 1.35;
				case MCMMO_EXPERIENCE, VOTE_POINTS, MOB_HEADS -> 1.2;
				case MYSTERY_CRATE_KEY -> 1.5;
				default -> 1;
			};
		}
	},
	GOOD_BOOST {
		@Override
		public void give(HasOfflinePlayer player) {
			Boostable type = RandomUtils.randomElement(Arrays.stream(Boostable.values()).filter(boostable -> !boostable.isDisabled()).toList());
			Booster booster = BOOSTER_SERVICE.get(player.getOfflinePlayer());
			Booster.Boost boost = booster.add(
				type,
				RandomUtils.randomDouble(1.1, getMaxBoostMultiplier(type)),
				TimeUtils.TickTime.MINUTE.x(RandomUtils.randomInt(60, 120)) / 20,
				true
			);
			BOOSTER_SERVICE.save(booster);
			OfflineMessage.send(player.getOfflinePlayer(), getRewardMessage(getBoostMessage(boost), player));
		}

		public double getMaxBoostMultiplier(Boostable type) {
			return switch (type) {
				case EXPERIENCE, MINIGAME_DAILY_TOKENS, KILLER_MONEY -> 1.5;
				case MCMMO_EXPERIENCE, VOTE_POINTS, MOB_HEADS -> 1.25;
				case MYSTERY_CRATE_KEY -> 1.75;
				default -> 1;
			};
		}
	},
	GREAT_BOOST {
		@Override
		public void give(HasOfflinePlayer player) {
			Boostable type = RandomUtils.randomElement(Arrays.stream(Boostable.values()).filter(boostable -> !boostable.isDisabled()).toList());
			Booster booster = BOOSTER_SERVICE.get(player.getOfflinePlayer());
			Booster.Boost boost = booster.add(
				type,
				RandomUtils.randomDouble(1.1, getMaxBoostMultiplier(type)),
				TimeUtils.TickTime.MINUTE.x(RandomUtils.randomInt(90, 180)) / 20,
				true
			);
			BOOSTER_SERVICE.save(booster);
			OfflineMessage.send(player.getOfflinePlayer(), getRewardMessage(getBoostMessage(boost), player));
		}

		public double getMaxBoostMultiplier(Boostable type) {
			return switch (type) {
				case EXPERIENCE, MINIGAME_DAILY_TOKENS, KILLER_MONEY -> 1.75;
				case MCMMO_EXPERIENCE, VOTE_POINTS, MOB_HEADS -> 1.35;
				case MYSTERY_CRATE_KEY -> 2;
				default -> 1;
			};
		}
	},
	VOTE_KEYS {
		@Override
		public void give(HasOfflinePlayer player) {
			int amount = RandomUtils.randomInt(10, 15);
			CrateType.VOTE.give(player.getOfflinePlayer(), amount);
			OfflineMessage.send(player.getOfflinePlayer(), getRewardMessage(amount + " Vote Crate Keys", player));
		}
	},
	MORE_VOTE_KEYS {
		@Override
		public void give(HasOfflinePlayer player) {
			int amount = RandomUtils.randomInt(15, 25);
			CrateType.VOTE.give(player.getOfflinePlayer(), amount);
			OfflineMessage.send(player.getOfflinePlayer(), getRewardMessage(amount + " Vote Crate Keys", player));
		}
	},
	ECO {
		@Override
		public void give(HasOfflinePlayer player) {
			int amount = Math.round(RandomUtils.randomInt(5000, 10000) / 500f) * 500;
			BANKER_SERVICE.withdraw(player.getOfflinePlayer(), amount, Shop.ShopGroup.SURVIVAL, Transaction.TransactionCause.VOTE_REWARD);
			OfflineMessage.send(player.getOfflinePlayer(), getRewardMessage("$" + amount, player));
		}
	},
	MORE_ECO {
		@Override
		public void give(HasOfflinePlayer player) {
			int amount = Math.round(RandomUtils.randomInt(12500, 20000) / 500f) * 500;
			BANKER_SERVICE.withdraw(player.getOfflinePlayer(), amount, Shop.ShopGroup.SURVIVAL, Transaction.TransactionCause.VOTE_REWARD);
			OfflineMessage.send(player.getOfflinePlayer(), getRewardMessage("$" + amount, player));
		}
	},
	EVENT_TOKENS {
		private static final EventUserService EVENT_SERVICE = new EventUserService();

		@Override
		public void give(HasOfflinePlayer player) {
			EventUser user = EVENT_SERVICE.get(player.getOfflinePlayer());
			int amount = Math.round(RandomUtils.randomInt(25, 50) / 5f) * 5;
			user.giveTokens(amount);
			EVENT_SERVICE.save(user);
			OfflineMessage.send(player.getOfflinePlayer(), getRewardMessage(amount + " Event Tokens", player));
		}
	},
	MYSTERY_CRATE_KEY {
		@Override
		public void give(HasOfflinePlayer player) {
			CrateType.MYSTERY.give(player.getOfflinePlayer());
			OfflineMessage.send(player.getOfflinePlayer(), getRewardMessage("&3a &dMystery Crate Key", player));
		}
	},
	PINATA {
		@Override
		public void give(HasOfflinePlayer player) {
			int amount = RandomUtils.randomInt(1, 5);
			CratePinatas.give(player.getOfflinePlayer(), CrateType.VOTE, amount);
			OfflineMessage.send(player.getOfflinePlayer(), getRewardMessage(amount + StringUtils.plural(" Vote Piñata", amount), player));
		}
	}
	;


	public abstract void give(HasOfflinePlayer player);

	public static String getBoostMessage(Booster.Boost boost) {
		String message = "&3a " + boost.getDisplayItem().name();
		message += " &e(" + TimeUtils.Timespan.ofSeconds(boost.getDuration()).format(TimeUtils.Timespan.FormatType.LONG) + ")";
		message += " Boost";

		return message;
	}

	public static String getRewardMessage(String reward, HasOfflinePlayer player) {
		boolean online = player.getOfflinePlayer().isOnline();
		String message = VoteParty.getPrefix() + "&3You ";
		message += online ? "have been" : "were";
		message += " given &e";
		message += reward;
		message += " &3for voting";
		message += online ? "!" : " while you were offline!";

		return message;
	}

	private static final BoosterService BOOSTER_SERVICE = new BoosterService();
	private static final BankerService BANKER_SERVICE = new BankerService();

}
