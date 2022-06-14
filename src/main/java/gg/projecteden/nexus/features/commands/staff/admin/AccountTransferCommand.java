package gg.projecteden.nexus.features.commands.staff.admin;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.griefcraft.sql.PhysDB;
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
import gg.projecteden.nexus.models.alerts.AlertsService;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.banker.Transactions;
import gg.projecteden.nexus.models.banker.TransactionsService;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUser;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUser.DailyStreak;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUserService;
import gg.projecteden.nexus.models.dailyvotereward.DailyVoteReward;
import gg.projecteden.nexus.models.dailyvotereward.DailyVoteReward.DailyVoteStreak;
import gg.projecteden.nexus.models.dailyvotereward.DailyVoteRewardService;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.emoji.EmojiUser;
import gg.projecteden.nexus.models.emoji.EmojiUserService;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.inventoryhistory.InventoryHistory;
import gg.projecteden.nexus.models.inventoryhistory.InventoryHistoryService;
import gg.projecteden.nexus.models.lwc.LWCProtection;
import gg.projecteden.nexus.models.lwc.LWCProtectionService;
import gg.projecteden.nexus.models.mail.Mailer;
import gg.projecteden.nexus.models.mail.Mailer.Mail;
import gg.projecteden.nexus.models.mail.MailerService;
import gg.projecteden.nexus.models.mobheads.MobHeadUser;
import gg.projecteden.nexus.models.mobheads.MobHeadUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import gg.projecteden.nexus.models.perkowner.PerkOwnerService;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.models.punishments.PunishmentsService;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.Product;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.models.store.Contributor;
import gg.projecteden.nexus.models.store.Contributor.Purchase;
import gg.projecteden.nexus.models.store.ContributorService;
import gg.projecteden.nexus.models.trust.Trust;
import gg.projecteden.nexus.models.trust.TrustService;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Permission(Group.ADMIN)
public class AccountTransferCommand extends CustomCommand {

	public AccountTransferCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("all <old> <new>")
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
				send(PREFIX + "&cAn error occurred when transferring " + camelCase(feature) + " data");
				ex.printStackTrace();
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
		EMOJI(new EmojiTransferer()),
		EVENT(new EventUserTransferer()),
		HOMES(new HomeTransferer()),
		HOURS(new HoursTransferer()),
		INVENTORY_HISTORY(new InventoryHistoryTransferer()),
		LWC(new LWCTransferer()),
		MAIL(new MailTransferer()),
		MCMMO(new McMMOTransferer()),
		MINIGAME_PERKS(new MinigamePerkTransferer()),
		MOB_HEADS(new MobHeadUserTransferer()),
		NERD(new NerdTransferer()),
		PUNISHMENTS(new PunishmentsTransferer()),
		SHOP(new ShopTransferer()),
		TRANSACTIONS(new TransactionsTransferer()),
		TRUSTS(new TrustsTransferer()),
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

	@Service(AlertsService.class)
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

	@Service(ContributorService.class)
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

	@Service(CostumeUserService.class)
	static class CostumeUserTransferer extends MongoTransferer<CostumeUser> {
		@Override
		public void transfer(CostumeUser previous, CostumeUser current) {
			if (previous.hasActiveCostumes()) {
				previous.getActiveCostumes().forEach((type, activeCostume) -> {
					if (!current.hasActiveCostume(type))
						current.setActiveCostumeId(type, activeCostume);
				});
			}

			if (previous.hasActiveDisplayCostumes()) {
				previous.getActiveDisplayCostumes().forEach((type, activeCostume) -> {
					if (!current.hasActiveDisplayCostume(type))
						current.setActiveDisplayCostumeId(type, activeCostume);
				});
			}

			current.getOwnedCostumes().addAll(previous.getOwnedCostumes());

			current.addVouchers(previous.getVouchers());

			previous.setVouchers(0);
			previous.getActiveCostumes().clear();
			previous.getActiveDisplayCostumes().clear();
			previous.getOwnedCostumes().clear();
		}
	}

	@Service(DailyRewardUserService.class)
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

	@Service(DailyVoteRewardService.class)
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

	@Service(DiscordUserService.class)
	static class DiscordUserTransferer extends MongoTransferer<DiscordUser> {
		@Override
		protected void transfer(DiscordUser previous, DiscordUser current) {
			current.setRoleId(previous.getRoleId());
			current.setUserId(previous.getUserId());

			previous.setRoleId(null);
			previous.setUserId(null);
		}
	}

	@Service(EmojiUserService.class)
	static class EmojiTransferer extends MongoTransferer<EmojiUser> {
		@Override
		public void transfer(EmojiUser previous, EmojiUser current) {
			current.getOwned().addAll(previous.getOwned());

			previous.getOwned().clear();
		}
	}

	@Service(EventUserService.class)
	static class EventUserTransferer extends MongoTransferer<EventUser> {
		@Override
		public void transfer(EventUser previous, EventUser current) {
			current.setTokens(previous.getTokens());
			previous.getTokensReceivedByDate().forEach((string, map) -> current.getTokensReceivedByDate().put(string, map));
			previous.getTokensReceivedByDate().clear();
		}
	}

	@Service(HomeService.class)
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

	@Service(HoursService.class)
	static class HoursTransferer extends MongoTransferer<Hours> {
		@Override
		public void transfer(Hours previous, Hours current) {
			previous.getTimes().forEach((date, seconds) -> current.getTimes().put(date, seconds));
			previous.getTimes().clear();
		}
	}

	@Service(InventoryHistoryService.class)
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
			final PhysDB database = LWC.getInstance().getPhysicalDatabase();
			final LWCProtectionService service = new LWCProtectionService();
			final List<LWCProtection> oldProtections = service.getPlayerProtections(old.getUniqueId());

			for (LWCProtection oldProtection : oldProtections) {
				Protection protectionById = database.loadProtection(oldProtection.getId());
				if (protectionById != null) {
					protectionById.setOwner(target.getUniqueId().toString());
					protectionById.saveNow();
				}
			}
		}
	}

	@Service(MailerService.class)
	static class MailTransferer extends MongoTransferer<Mailer> {
		@Override
		public void transfer(Mailer old, Mailer target) {
			for (WorldGroup worldGroup : old.getMail().keySet()) {
				List<Mail> mailOld = old.getMail().get(worldGroup);
				List<Mail> mailTarget = target.getMail().get(worldGroup);

				if (mailOld == null)
					mailOld = new ArrayList<>();

				if (mailTarget == null)
					mailTarget = new ArrayList<>();

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

	@Service(MobHeadUserService.class)
	static class MobHeadUserTransferer extends MongoTransferer<MobHeadUser> {
		@Override
		public void transfer(MobHeadUser previous, MobHeadUser current) {
			current.setData(previous.getData());
			previous.getData().clear();
		}
	}

	@Service(NerdService.class)
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

	@Service(PerkOwnerService.class)
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

	@Service(PunishmentsService.class)
	static class PunishmentsTransferer extends MongoTransferer<Punishments> {
		@Override
		public void transfer(Punishments previous, Punishments current) {
			current.getPunishments().addAll(previous.getPunishments());
			current.getIpHistory().addAll(previous.getIpHistory());
		}
	}

	@Service(ShopService.class)
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

	@Service(TransactionsService.class)
	static class TransactionsTransferer extends MongoTransferer<Transactions> {
		@Override
		public void transfer(Transactions previous, Transactions current) {
			current.getTransactions().addAll(previous.getTransactions());
			previous.getTransactions().clear();
		}
	}

	@Service(TrustService.class)
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

}
