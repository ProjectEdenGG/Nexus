package gg.projecteden.nexus.features.commands.staff.admin;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.griefcraft.cache.ProtectionCache;
import com.griefcraft.model.Protection;
import gg.projecteden.annotations.Sync;
import gg.projecteden.mongodb.annotations.Service;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import gg.projecteden.nexus.models.alerts.Alerts;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.banker.Transactions;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUser;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUser.DailyStreak;
import gg.projecteden.nexus.models.dailyvotereward.DailyVoteReward;
import gg.projecteden.nexus.models.dailyvotereward.DailyVoteReward.DailyVoteStreak;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.emoji.EmojiUser;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.inventoryhistory.InventoryHistory;
import gg.projecteden.nexus.models.lwc.LWCProtection;
import gg.projecteden.nexus.models.lwc.LWCProtectionService;
import gg.projecteden.nexus.models.mail.Mailer;
import gg.projecteden.nexus.models.mail.Mailer.Mail;
import gg.projecteden.nexus.models.mobheads.MobHeadUser;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.Product;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.store.Contributor;
import gg.projecteden.nexus.models.store.Contributor.Purchase;
import gg.projecteden.nexus.models.trust.Trust;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Permission(Group.ADMIN)
public class AccountTransferCommand extends CustomCommand {

	public AccountTransferCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<old> <new> all")
	void transferAll(OfflinePlayer old, OfflinePlayer target) {
		transfer(old, target, Arrays.stream(Transferable.values()).collect(Collectors.toList()));
	}

	@Path("<old> <new> <features...>")
	void transfer(OfflinePlayer old, OfflinePlayer target, @Arg(type = Transferable.class) List<Transferable> features) {
		features.forEach(feature -> {
			try {
				Runnable transfer = () -> feature.getTransferer().transfer(old, target);
				if (Transferable.class.getField(feature.name()).getAnnotation(Sync.class) != null)
					Tasks.async(transfer);
				else
					transfer.run();

				send(PREFIX + "Transferred &e" + camelCase(feature) + " &3data");
			} catch (Exception ex) {
				rethrow(ex);
			}
		});
	}

	@Getter
	@AllArgsConstructor
	public enum Transferable {
		ALERTS(new AlertsTransferer()),
		BALANCE(new BalanceTransferer()),
		CONTRIBUTOR(new ContributorTransferer()),
		COSTUMES(new CostumeUserTransferer()),
		DAILY_REWARDS(new DailyRewardsTransferer()),
		DAILY_VOTE_REWARD(new DailyVoteRewardTransferer()),
		DISCORD(new DiscordUserTransferer()),
		EVENT(new EventUserTransferer()),
		HOMES(new HomeTransferer()),
		HOURS(new HoursTransferer()),
		INVENTORY_HISTORY(new InventoryHistoryTransferer()),
		LWC(new LWCTransferer()),
		MAIL(new MailTransferer()),
		MCMMO(new McMMOTransferer()),
		MOB_HEADS(new MobHeadUserTransferer()),
		NERD(new NerdTransferer()),
		PUNISHMENTS(new PunishmentsTransferer()),
		SHOP(new ShopTransferer()),
		TRANSACTIONS(new TransactionsTransferer()),
		TRUSTS(new TrustsTransferer()),
		EMOJI(new EmojiTransferer()),
		MINIGAME_PERKS(new MinigamePerkTransferer()),
		;

		private final Transferer transferer;
	}

	public interface Transferer {
		void transfer(OfflinePlayer old, OfflinePlayer target);
	}

	abstract static class MongoTransferer<P extends PlayerOwnedObject> implements Transferer {
		protected MongoPlayerService<P> service;

		public MongoTransferer() {
			try {
				this.service = (MongoPlayerService<P>) getClass().getAnnotation(Service.class).value().newInstance();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void transfer(OfflinePlayer old, OfflinePlayer target) {
			final P previous = service.get(old);
			final P current = service.get(target);
			transfer(previous, current);
			service.save(previous);
			service.save(current);
		}

		protected abstract void transfer(P previous, P current);
	}

	static class AlertsTransferer extends MongoTransferer<Alerts> {
		@Override
		protected void transfer(Alerts previous, Alerts current) {
			previous.getHighlights().forEach(highlight -> current.getHighlights().add(highlight));

			previous.getHighlights().clear();
		}
	}

	static class BalanceTransferer implements Transferer {
		@Override
		public void transfer(OfflinePlayer old, OfflinePlayer target) {
			final BankerService service = new BankerService();

			for (ShopGroup shopGroup : ShopGroup.values()) {
				double balance = service.getBalance(old, shopGroup);
				service.transfer(old, target, balance, shopGroup, TransactionCause.SERVER);
			}
		}
	}

	static class ContributorTransferer extends MongoTransferer<Contributor> {
		@Override
		protected void transfer(Contributor previous, Contributor current) {
			current.getPurchases().addAll(previous.getPurchases());
			for (Purchase purchase : current.getPurchases()) {
				if (purchase.getUuid().equals(previous.getUuid())) {
					purchase.setUuid(current.getUuid());
					purchase.setName(current.getName());
				}
				if (purchase.getPurchaserUuid().equals(previous.getUuid())) {
					purchase.setPurchaserUuid(current.getUuid());
					purchase.setPurchaserName(current.getName());
				}
			}

			current.setCredit(current.getCredit() + previous.getCredit());
			previous.getPurchases().clear();
			previous.setCredit(0);
		}
	}

	static class CostumeUserTransferer extends MongoTransferer<CostumeUser> {
		@Override
		public void transfer(CostumeUser previous, CostumeUser current) {
			current.addVouchers(previous.getVouchers());
			if (current.getActiveCostume() == null)
				current.setActiveCostumeId(previous.getActiveCostume());
			current.getOwnedCostumes().addAll(previous.getOwnedCostumes());

			previous.setVouchers(0);
			previous.setActiveCostume(null);
			previous.getOwnedCostumes().clear();
		}
	}

	static class DailyRewardsTransferer extends MongoTransferer<DailyRewardUser> {
		@Override
		public void transfer(DailyRewardUser previous, DailyRewardUser current) {
			current.setCurrentStreak(previous.getCurrentStreak());
			current.getCurrentStreak().setUuid(current.getUuid());
			current.getPastStreaks().addAll(previous.getPastStreaks());
			previous.setCurrentStreak(new DailyStreak(current.getUuid()));
			previous.getPastStreaks().clear();
		}
	}

	static class DailyVoteRewardTransferer extends MongoTransferer<DailyVoteReward> {
		@Override
		public void transfer(DailyVoteReward previous, DailyVoteReward current) {
			current.setCurrentStreak(previous.getCurrentStreak());
			current.getCurrentStreak().setUuid(current.getUuid());
			current.getPastStreaks().addAll(previous.getPastStreaks());
			previous.setCurrentStreak(new DailyVoteStreak(previous.getUuid()));
			previous.getPastStreaks().clear();
		}
	}

	static class DiscordUserTransferer extends MongoTransferer<DiscordUser> {
		@Override
		protected void transfer(DiscordUser previous, DiscordUser current) {
			current.setRoleId(previous.getRoleId());
			current.setUserId(previous.getUserId());

			previous.setRoleId(null);
			previous.setUserId(null);
		}
	}

	static class EventUserTransferer extends MongoTransferer<EventUser> {
		@Override
		public void transfer(EventUser previous, EventUser current) {
			current.setTokens(previous.getTokens());
			previous.getTokensReceivedByDate().forEach((string, map) -> current.getTokensReceivedByDate().put(string, map));
			previous.getTokensReceivedByDate().clear();
		}
	}

	static class HomeTransferer extends MongoTransferer<HomeOwner> {
		@Override
		public void transfer(HomeOwner previous, HomeOwner current) {
			for (Home home : previous.getHomes()) {
				home.setUuid(current.getUuid());
				current.add(home);
			}
			previous.getHomes().clear();

			current.setAutoLock(previous.isAutoLock());
			current.setUsedDeathHome(previous.isUsedDeathHome());
			current.addExtraHomes(previous.getExtraHomes());
			previous.setExtraHomes(0);
		}
	}

	static class HoursTransferer extends MongoTransferer<Hours> {
		@Override
		public void transfer(Hours previous, Hours current) {
			previous.getTimes().forEach((date, seconds) -> current.getTimes().put(date, seconds));
			previous.getTimes().clear();
		}
	}

	static class InventoryHistoryTransferer extends MongoTransferer<InventoryHistory> {
		@Override
		public void transfer(InventoryHistory previous, InventoryHistory current) {
			current.getSnapshots().addAll(previous.getSnapshots());
			previous.getSnapshots().clear();
		}
	}

	static class LWCTransferer implements Transferer {
		@Override
		public void transfer(OfflinePlayer old, OfflinePlayer target) {
			final ProtectionCache protectionCache = com.griefcraft.lwc.LWC.getInstance().getProtectionCache();
			final LWCProtectionService service = new LWCProtectionService();
			final List<LWCProtection> oldProtections = service.getPlayerProtections(old.getUniqueId());

			for (LWCProtection oldProtection : oldProtections) {
				Protection protectionById = protectionCache.getProtectionById(oldProtection.getId());
				if (protectionById != null) {
					protectionById.setOwner(target.getUniqueId().toString());
					protectionById.save();
				}
			}
		}
	}

	static class MailTransferer extends MongoTransferer<Mailer> {
		@Override
		public void transfer(Mailer old, Mailer target) {
			for (WorldGroup worldGroup : old.getMail().keySet()) {
				List<Mail> mailOld = old.getMail().get(worldGroup);
				List<Mail> mailTarget = target.getMail().get(worldGroup);

				mailTarget.addAll(mailOld);
				target.getMail().put(worldGroup, mailTarget);
			}

			old.getMail().clear();
		}
	}

	static class McMMOTransferer implements Transferer {
		@Override
		public void transfer(OfflinePlayer old, OfflinePlayer target) {
			final PlayerProfile previous = mcMMO.getDatabaseManager().loadPlayerProfile(old.getUniqueId());
			final PlayerProfile current = mcMMO.getDatabaseManager().loadPlayerProfile(target.getUniqueId());

			for (PrimarySkillType skill : PrimarySkillType.values()) {
				current.modifySkill(skill, previous.getSkillLevel(skill));
				previous.modifySkill(skill, 0);
			}

			previous.scheduleAsyncSave();
			current.scheduleAsyncSave();
		}
	}

	static class MobHeadUserTransferer extends MongoTransferer<MobHeadUser> {
		@Override
		public void transfer(MobHeadUser previous, MobHeadUser current) {
			current.setData(previous.getData());
			previous.getData().clear();
		}
	}

	static class NerdTransferer extends MongoTransferer<Nerd> {
		@Override
		protected void transfer(Nerd previous, Nerd current) {
			current.setPreferredName(previous.getPreferredName());
			current.setBirthday(previous.getBirthday());
			current.setFirstJoin(previous.getFirstJoin());
			current.setLastJoin(previous.getLastJoin());
			current.setLastQuit(previous.getLastQuit());
			current.setPromotionDate(previous.getPromotionDate());
			current.setAbout(previous.getAbout());
			current.setMeetMeVideo(previous.isMeetMeVideo());
			current.setPronouns(previous.getPronouns());

			previous.setPreferredName(null);
			previous.setBirthday(null);
			previous.setPromotionDate(null);
			previous.setAbout(null);
			previous.setMeetMeVideo(false);
			previous.getPronouns().clear();
		}
	}

	static class PunishmentsTransferer extends MongoTransferer<Punishments> {
		@Override
		public void transfer(Punishments previous, Punishments current) {
			current.getPunishments().addAll(previous.getPunishments());
			current.getIpHistory().addAll(previous.getIpHistory());
		}
	}

	static class ShopTransferer extends MongoTransferer<Shop> {
		@Override
		public void transfer(Shop previous, Shop current) {
			current.setDescription(previous.getDescription());
			current.addHolding(previous.getHolding());
			current.getDisabledResourceMarketItems().addAll(previous.getDisabledResourceMarketItems());

			for (Product product : previous.getProducts()) {
				product.setUuid(current.getUuid());
				current.getProducts().add(product);
			}

			previous.getDescription().clear();
			previous.getProducts().clear();
			previous.getHolding().clear();
			previous.getDisabledResourceMarketItems().clear();
		}
	}

	static class TransactionsTransferer extends MongoTransferer<Transactions> {
		@Override
		public void transfer(Transactions previous, Transactions current) {
			current.getTransactions().addAll(previous.getTransactions());
			previous.getTransactions().clear();
		}
	}

	static class TrustsTransferer extends MongoTransferer<Trust> {
		@Override
		public void transfer(Trust previous, Trust current) {
			previous.getLocks().forEach(lock -> current.getLocks().add(lock));
			previous.getHomes().forEach(home -> current.getHomes().add(home));
			previous.getTeleports().forEach(teleport -> current.getTeleports().add(teleport));

			previous.getLocks().clear();
			previous.getHomes().clear();
			previous.getTeleports().clear();
		}
	}

	static class EmojiTransferer extends MongoTransferer<EmojiUser> {
		@Override
		public void transfer(EmojiUser previous, EmojiUser current) {
			current.getOwned().addAll(previous.getOwned());

			previous.getOwned().clear();
		}
	}

	static class MinigamePerkTransferer extends MongoTransferer<PerkOwner> {
		@Override
		public void transfer(PerkOwner previous, PerkOwner current) {
			current.getPurchasedPerks().putAll(previous.getPurchasedPerks());
			current.setTokens(current.getTokens() + previous.getTokens());
			current.setHideParticle(previous.getHideParticle());
			current.setDailyTokens(previous.getDailyTokens());

			previous.getPurchasedPerks().clear();
			previous.setTokens(0);
			previous.setDailyTokens(0);
		}
	}

}
