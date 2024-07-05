package gg.projecteden.nexus.features.commands.staff.admin;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.griefcraft.sql.PhysDB;
import gg.projecteden.api.common.annotations.Sync;
import gg.projecteden.api.mongodb.annotations.Service;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
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
import gg.projecteden.nexus.models.extraplots.ExtraPlotUser;
import gg.projecteden.nexus.models.extraplots.ExtraPlotUserService;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.inventoryhistory.InventoryHistory;
import gg.projecteden.nexus.models.inventoryhistory.InventoryHistoryService;
import gg.projecteden.nexus.models.legacy.LegacyUser;
import gg.projecteden.nexus.models.legacy.LegacyUserService;
import gg.projecteden.nexus.models.legacy.homes.LegacyHome;
import gg.projecteden.nexus.models.legacy.homes.LegacyHomeOwner;
import gg.projecteden.nexus.models.legacy.homes.LegacyHomeService;
import gg.projecteden.nexus.models.legacy.mail.LegacyMailer;
import gg.projecteden.nexus.models.legacy.mail.LegacyMailerService;
import gg.projecteden.nexus.models.legacy.shops.LegacyShop;
import gg.projecteden.nexus.models.legacy.shops.LegacyShopService;
import gg.projecteden.nexus.models.legacy.vaults.LegacyVaultUser;
import gg.projecteden.nexus.models.legacy.vaults.LegacyVaultUserService;
import gg.projecteden.nexus.models.lwc.LWCProtection;
import gg.projecteden.nexus.models.lwc.LWCProtectionService;
import gg.projecteden.nexus.models.mail.Mailer;
import gg.projecteden.nexus.models.mail.Mailer.Mail;
import gg.projecteden.nexus.models.mail.MailerService;
import gg.projecteden.nexus.models.mobheads.MobHeadUser;
import gg.projecteden.nexus.models.mobheads.MobHeadUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nerd.Rank;
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
import gg.projecteden.nexus.models.trust.Trust.Type;
import gg.projecteden.nexus.models.trust.TrustService;
import gg.projecteden.nexus.models.vaults.VaultUser;
import gg.projecteden.nexus.models.vaults.VaultUserService;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Permission(Group.ADMIN)
public class AccountTransferCommand extends CustomCommand {

	public AccountTransferCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("all <old> <new>")
	@Description("Transfer all data from one account to another")
	void transferAll(OfflinePlayer old, OfflinePlayer target) {
		transfer(old, target, Arrays.stream(Transferable.values()).collect(Collectors.toList()));
	}

	@Path("<old> <new> <features...>")
	@Description("Transfer specific features from one account to another")
	void transfer(OfflinePlayer old, OfflinePlayer target, @Arg(type = Transferable.class) List<Transferable> features) {
		features.forEach(feature -> {
			try {
				Runnable transfer = () -> feature.getTransferer().transfer(player(), old, target);
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
		LEGACY(new LegacyUserTransferer()),
		LEGACY_HOMES(new LegacyHomesTransferer()),
		LEGACY_MAIL(new LegacyMailTransferer()),
		LEGACY_SHOP(new LegacyShopTransferer()),
		LEGACY_VAULTS(new LegacyVaultsTransferer()),
		LUCK_PERMS(new LuckPermsTransferer()),
		LWC(new LWCTransferer()),
		MAIL(new MailTransferer()),
		MCMMO(new McMMOTransferer()),
		MINIGAME_PERKS(new MinigamePerkTransferer()),
		MOB_HEADS(new MobHeadUserTransferer()),
		NERD(new NerdTransferer()),
		PLOTS(new PlotTransferer()),
		PUNISHMENTS(new PunishmentsTransferer()),
		SHOP(new ShopTransferer()),
		TRANSACTIONS(new TransactionsTransferer()),
		TRUSTS(new TrustsTransferer()),
		VAULTS(new VaultsTransferer()),
		;

		private final Transferer transferer;
	}

	public interface Transferer {
		void transfer(Player executor, OfflinePlayer old, OfflinePlayer target);
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
		public void transfer(Player executor, OfflinePlayer old, OfflinePlayer target) {
			final P previous = service.get(old);
			final P current = service.get(target);
			transfer(executor, previous, current);
			service.save(previous);
			service.save(current);
		}

		protected abstract void transfer(Player executor, P previous, P current);
	}

	@Service(AlertsService.class)
	static class AlertsTransferer extends MongoTransferer<Alerts> {
		@Override
		protected void transfer(Player executor, Alerts previous, Alerts current) {
			previous.getHighlights().forEach(highlight -> current.getHighlights().add(highlight));

			previous.getHighlights().clear();
		}
	}

	static class BalanceTransferer implements Transferer {
		@Override
		public void transfer(Player executor, OfflinePlayer old, OfflinePlayer target) {
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
		protected void transfer(Player executor, Contributor previous, Contributor current) {
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
		public void transfer(Player executor, CostumeUser previous, CostumeUser current) {
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
		public void transfer(Player executor, DailyRewardUser previous, DailyRewardUser current) {
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
		public void transfer(Player executor, DailyVoteReward previous, DailyVoteReward current) {
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
		protected void transfer(Player executor, DiscordUser previous, DiscordUser current) {
			current.setRoleId(previous.getRoleId());
			current.setUserId(previous.getUserId());

			previous.setRoleId(null);
			previous.setUserId(null);
		}
	}

	@Service(EmojiUserService.class)
	static class EmojiTransferer extends MongoTransferer<EmojiUser> {
		@Override
		public void transfer(Player executor, EmojiUser previous, EmojiUser current) {
			current.getOwned().addAll(previous.getOwned());

			previous.getOwned().clear();
		}
	}

	@Service(EventUserService.class)
	static class EventUserTransferer extends MongoTransferer<EventUser> {
		@Override
		public void transfer(Player executor, EventUser previous, EventUser current) {
			current.setTokens(previous.getTokens());
			previous.getTokensReceivedByDate().forEach((string, map) -> current.getTokensReceivedByDate().put(string, map));
			previous.getTokensReceivedByDate().clear();
		}
	}

	@Service(HomeService.class)
	static class HomeTransferer extends MongoTransferer<HomeOwner> {
		@Override
		public void transfer(Player executor, HomeOwner previous, HomeOwner current) {
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

	@Service(LegacyHomeService.class)
	static class LegacyHomesTransferer extends MongoTransferer<LegacyHomeOwner> {

		@Override
		protected void transfer(Player executor, LegacyHomeOwner previous, LegacyHomeOwner current) {
			for (LegacyHome home : previous.getHomes()) {
				home.setUuid(current.getUuid());
				current.add(home);
			}
			previous.getHomes().clear();
		}
	}

	@Service(HoursService.class)
	static class HoursTransferer extends MongoTransferer<Hours> {
		@Override
		public void transfer(Player executor, Hours previous, Hours current) {
			previous.getTimes().forEach((date, seconds) -> current.getTimes().put(date, seconds));
			previous.getTimes().clear();
		}
	}

	@Service(InventoryHistoryService.class)
	static class InventoryHistoryTransferer extends MongoTransferer<InventoryHistory> {
		@Override
		public void transfer(Player executor, InventoryHistory previous, InventoryHistory current) {
			current.getSnapshots().addAll(previous.getSnapshots());
			previous.getSnapshots().clear();
		}
	}

	static class LuckPermsTransferer implements Transferer {
		@Override
		public void transfer(Player executor, OfflinePlayer old, OfflinePlayer target) {
			final Rank rank = Rank.of(old);
			if (rank == Rank.GUEST)
				return;

			GroupChange.set().player(target).group(rank).runAsync();
			GroupChange.set().player(old).group(Rank.MEMBER).runAsync();

			// TODO Permissions
		}
	}

	static class LWCTransferer implements Transferer {
		@Override
		public void transfer(Player executor, OfflinePlayer old, OfflinePlayer target) {
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
		public void transfer(Player executor, Mailer previous, Mailer current) {
			for (WorldGroup worldGroup : previous.getMail().keySet()) {
				List<Mail> mailOld = previous.getMail().get(worldGroup);
				List<Mail> mailTarget = current.getMail().get(worldGroup);

				if (mailOld == null)
					mailOld = new ArrayList<>();

				if (mailTarget == null)
					mailTarget = new ArrayList<>();

				mailTarget.addAll(mailOld);

				current.getMail().put(worldGroup, mailTarget);
			}

			previous.getMail().clear();
		}
	}

	@Service(LegacyMailerService.class)
	static class LegacyMailTransferer extends MongoTransferer<LegacyMailer> {
		@Override
		protected void transfer(Player executor, LegacyMailer previous, LegacyMailer current) {
			List<Mail> mailOld = previous.getMail();
			if (Nullables.isNullOrEmpty(mailOld))
				return;

			current.getMail().addAll(mailOld);
			previous.getMail().clear();
		}
	}

	static class McMMOTransferer implements Transferer {
		@Override
		public void transfer(Player executor, OfflinePlayer old, OfflinePlayer target) {
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
		public void transfer(Player executor, MobHeadUser previous, MobHeadUser current) {
			current.setData(previous.getData());
			previous.getData().clear();
		}
	}

	@Service(NerdService.class)
	static class NerdTransferer extends MongoTransferer<Nerd> {
		@Override
		protected void transfer(Player executor, Nerd previous, Nerd current) {
			current.setBirthday(previous.getBirthday());
			current.setFirstJoin(previous.getFirstJoin());
			current.setLastJoin(previous.getLastJoin());
			current.setLastQuit(previous.getLastQuit());
			current.setPromotionDate(previous.getPromotionDate());
			current.setAbout(previous.getAbout());
			current.setMeetMeVideo(previous.isMeetMeVideo());
			current.setPronouns(previous.getPronouns());
			current.setPreferredNames(previous.getPreferredNames());
			current.setPrefix(previous.getPrefix());

			previous.setBirthday(null);
			previous.setPromotionDate(null);
			previous.setAbout(null);
			previous.setMeetMeVideo(false);
			previous.getPronouns().clear();
			previous.setPreferredNames(new ArrayList<>());
			previous.setPrefix(null);
		}
	}

	@Service(PerkOwnerService.class)
	static class MinigamePerkTransferer extends MongoTransferer<PerkOwner> {
		@Override
		public void transfer(Player executor, PerkOwner previous, PerkOwner current) {
			current.getPurchasedPerks().putAll(previous.getPurchasedPerks());
			current.giveTokens(previous.getTokens());
			current.setHideParticle(previous.getHideParticle());
			current.setDailyTokens(previous.getDailyTokens());

			previous.getPurchasedPerks().clear();
			previous.setTokens(0);
			previous.setDailyTokens(0);
		}
	}

	@Service(ExtraPlotUserService.class)
	static class PlotTransferer extends MongoTransferer<ExtraPlotUser> {
		@Override
		protected void transfer(Player executor, ExtraPlotUser previous, ExtraPlotUser current) {
			current.setExtraPlots(previous.getExtraPlots());
			previous.setExtraPlots(0);
			PlayerUtils.send(executor, "Transfer plots manually"); // TODO
		}
	}

	@Service(PunishmentsService.class)
	static class PunishmentsTransferer extends MongoTransferer<Punishments> {
		@Override
		public void transfer(Player executor, Punishments previous, Punishments current) {
			current.getPunishments().addAll(previous.getPunishments());
			current.getIpHistory().addAll(previous.getIpHistory());
		}
	}

	@Service(ShopService.class)
	static class ShopTransferer extends MongoTransferer<Shop> {
		@Override
		public void transfer(Player executor, Shop previous, Shop current) {
			current.setDescription(previous.getDescription());
			current.setHolding(previous.getHolding());
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

	@Service(LegacyShopService.class)
	static class LegacyShopTransferer extends MongoTransferer<LegacyShop> {
		@Override
		protected void transfer(Player executor, LegacyShop previous, LegacyShop current) {
			current.setHolding(previous.getHolding());

			for (Product product : previous.getProducts()) {
				product.setUuid(current.getUuid());
				current.getProducts().add(product);
			}

			previous.getProducts().clear();
			previous.getHolding().clear();
		}
	}

	@Service(TransactionsService.class)
	static class TransactionsTransferer extends MongoTransferer<Transactions> {
		@Override
		public void transfer(Player executor, Transactions previous, Transactions current) {
			current.getTransactions().addAll(previous.getTransactions());

			previous.getTransactions().clear();
		}
	}

	@Service(TrustService.class)
	static class TrustsTransferer extends MongoTransferer<Trust> {
		@Override
		public void transfer(Player executor, Trust previous, Trust current) {
			current.addAllTypes(previous);

			previous.clearAll();

			// replace previous in others
			for (Trust uuid : service.getAll()) {
				Trust trust = service.get(uuid);
				if (trust.equals(current) || trust.equals(previous))
					continue;

				for (Type type : Type.values()) {
					for (UUID trusted : trust.get(type)) {
						if (trusted != previous.getUuid())
							continue;

						trust.add(type, current.getUuid());
					}
				}
			}
			service.saveCache();
		}
	}

	@Service(VaultUserService.class)
	static class VaultsTransferer extends MongoTransferer<VaultUser> {

		@Override
		protected void transfer(Player executor, VaultUser previous, VaultUser current) {
			current.setLimit(Math.max(previous.getLimit(), current.getLimit()));

			List<ItemStack> previousVaultItems = new ArrayList<>() {{
				for (Integer page : previous.getVaults().keySet()) {
					addAll(previous.getVaults().get(page));
				}
			}};

			if (!previousVaultItems.isEmpty())
				PlayerUtils.mailItems(current, previousVaultItems, "Items from vault transfer", WorldGroup.SURVIVAL);

			previous.setLimit(0);
			previous.getVaults().clear();
		}
	}

	@Service(LegacyVaultUserService.class)
	static class LegacyVaultsTransferer extends MongoTransferer<LegacyVaultUser> {

		@Override
		protected void transfer(Player executor, LegacyVaultUser previous, LegacyVaultUser current) {
			current.setLimit(Math.max(previous.getLimit(), current.getLimit()));

			List<ItemStack> previousVaultItems = new ArrayList<>() {{
				for (Integer page : previous.getVaults().keySet()) {
					addAll(previous.getVaults().get(page));
				}
			}};

			if (!previousVaultItems.isEmpty())
				PlayerUtils.mailItems(current, previousVaultItems, "Items from vault transfer", WorldGroup.LEGACY);

			previous.setLimit(0);
			previous.getVaults().clear();
		}

	}

	@Service(LegacyUserService.class)
	static class LegacyUserTransferer extends MongoTransferer<LegacyUser> {

		@Override
		protected void transfer(Player executor, LegacyUser previous, LegacyUser current) {
			current.setBalance(current.getBalance().add(previous.getBalance()));
			current.setVotePoints(current.getVotePoints() + previous.getVotePoints());

			for (String skill : previous.getMcmmo().keySet()) {
				int totalLevel = previous.getMcmmo().get(skill) + current.getMcmmo().get(skill);
				current.getMcmmo().put(skill, totalLevel);
			}

			previous.setBalance(null);
			previous.setVotePoints(0);
			previous.getMcmmo().clear();

		}
	}

}
