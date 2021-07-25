package gg.projecteden.nexus.features.commands.staff.admin;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.griefcraft.cache.ProtectionCache;
import com.griefcraft.model.Protection;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Sync;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.alerts.Alerts;
import gg.projecteden.nexus.models.alerts.AlertsService;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.banker.Transactions;
import gg.projecteden.nexus.models.banker.TransactionsService;
import gg.projecteden.nexus.models.contributor.Contributor;
import gg.projecteden.nexus.models.contributor.Contributor.Purchase;
import gg.projecteden.nexus.models.contributor.ContributorService;
import gg.projecteden.nexus.models.dailyreward.DailyReward;
import gg.projecteden.nexus.models.dailyreward.DailyRewardService;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.inventoryhistory.InventoryHistory;
import gg.projecteden.nexus.models.inventoryhistory.InventoryHistoryService;
import gg.projecteden.nexus.models.lwc.LWCProtection;
import gg.projecteden.nexus.models.lwc.LWCProtectionService;
import gg.projecteden.nexus.models.mobheads.MobHeadUser;
import gg.projecteden.nexus.models.mobheads.MobHeadUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.models.punishments.PunishmentsService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.trust.Trust;
import gg.projecteden.nexus.models.trust.TrustService;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Method;
import java.util.List;

@Permission("group.admin")
public class AccountTransferCommand extends CustomCommand {

	public AccountTransferCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<old> <new> <features...>")
	void transfer(OfflinePlayer old, OfflinePlayer target, @Arg(type = Transferable.class) List<Transferable> features) {
		features.forEach(feature -> {
			try {
				Runnable transfer = () -> feature.transfer(old, target);
				Method method = feature.getClass().getMethod("transfer", OfflinePlayer.class, OfflinePlayer.class);
				if (method.getAnnotation(Sync.class) != null) {
					Tasks.async(transfer);
				} else
					transfer.run();

				send("Transferred " + camelCase(feature) + " data");
			} catch (Exception ex) {
				rethrow(ex);
			}
		});
	}

	public enum Transferable {
		ALERTS {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				final AlertsService service = new AlertsService();
				final Alerts previous = service.get(old);
				final Alerts current = service.get(target);

				previous.getHighlights().forEach(highlight -> current.getHighlights().add(highlight));
				current.setMuted(previous.isMuted());

				previous.getHighlights().clear();

				service.save(previous);
				service.save(current);
			}
		},
		BALANCE {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				final BankerService service = new BankerService();

				for (ShopGroup shopGroup : ShopGroup.values()) {
					double balance = service.getBalance(old, shopGroup);
					service.transfer(old, target, balance, shopGroup, TransactionCause.SERVER);
				}
			}
		},
		CONTRIBUTOR {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				final ContributorService service = new ContributorService();
				final Contributor previous = service.get(old);
				final Contributor current = service.get(target);

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

				service.save(current);
				service.save(previous);
			}
		},
		DAILY_REWARDS {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				final DailyRewardService service = new DailyRewardService();
				final DailyReward previous = service.get(old);
				final DailyReward current = service.get(target);

				current.setStreak(previous.getStreak());
				current.setClaimed(previous.getClaimed());
				if (!current.isEarnedToday())
					current.setEarnedToday(previous.isEarnedToday());

				previous.setActive(false);

				service.save(previous);
				service.save(current);
			}
		},
		DISCORD {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				final DiscordUserService service = new DiscordUserService();
				final DiscordUser previous = service.get(old);
				final DiscordUser current = service.get(target);

				current.setRoleId(previous.getRoleId());
				current.setUserId(previous.getUserId());

				previous.setRoleId(null);
				previous.setUserId(null);

				service.save(previous);
				service.save(current);
			}
		},
		EVENT_USER {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				final EventUserService service = new EventUserService();
				final EventUser previous = service.get(old);
				final EventUser current = service.get(target);

				current.setTokens(previous.getTokens());
				previous.getTokensReceivedToday().forEach((string, map) -> current.getTokensReceivedToday().put(string, map));
				previous.getTokensReceivedToday().clear();

				service.save(previous);
				service.save(current);
			}
		},
		HISTORY {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				final PunishmentsService service = new PunishmentsService();
				final Punishments previous = service.get(old);
				final Punishments current = service.get(target);

				current.getPunishments().addAll(previous.getPunishments());
				current.getIpHistory().addAll(previous.getIpHistory());

				service.save(current);
			}
		},
		HOMES {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				final HomeService service = new HomeService();
				final HomeOwner previous = service.get(old);
				final HomeOwner current = service.get(target);

				previous.getHomes().forEach(current::add);
				previous.getHomes().clear();

				current.setAutoLock(previous.isAutoLock());
				current.setUsedDeathHome(previous.isUsedDeathHome());
				current.addExtraHomes(previous.getExtraHomes());
				previous.setExtraHomes(0);

				service.save(previous);
				service.save(current);
			}
		},
		HOURS {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				final HoursService service = new HoursService();
				final Hours previous = service.get(old.getUniqueId());
				final Hours current = service.get(target.getUniqueId());

				previous.getTimes().forEach((date, seconds) -> current.getTimes().put(date, seconds));
				previous.getTimes().clear();

				service.save(previous);
				service.save(current);
			}
		},
		INVENTORY_HISTORY {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				final InventoryHistoryService service = new InventoryHistoryService();
				final InventoryHistory previous = service.get(old);
				final InventoryHistory current = service.get(target);

				current.getSnapshots().addAll(previous.getSnapshots());
				previous.getSnapshots().clear();

				service.save(previous);
				service.save(current);
			}
		},
		LWC {
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
		},
		MCMMO {
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
		},
		MOB_HEADS {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				final MobHeadUserService service = new MobHeadUserService();
				final MobHeadUser previous = service.get(old);
				final MobHeadUser current = service.get(target);

				current.setData(previous.getData());
				previous.getData().clear();

				service.save(previous);
				service.save(current);
			}
		},
		NERD {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				final NerdService service = new NerdService();
				final Nerd previous = Nerd.of(old);
				final Nerd current = Nerd.of(target);

				current.setPreferredName(previous.getPreferredName());
				current.setBirthday(previous.getBirthday());
				current.setFirstJoin(previous.getFirstJoin());
				current.setLastJoin(previous.getLastJoin());
				current.setLastQuit(previous.getLastQuit());
				current.setPromotionDate(previous.getPromotionDate());
				current.setAbout(previous.getAbout());
				current.setMeetMeVideo(previous.isMeetMeVideo());

				previous.setPreferredName(null);
				previous.setBirthday(null);
				previous.setPromotionDate(null);
				previous.setAbout(null);
				previous.setMeetMeVideo(false);

				service.save(previous);
				service.save(current);
			}
		},
		TRANSACTIONS {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				final TransactionsService service = new TransactionsService();
				final Transactions previous = service.get(old);
				final Transactions current = service.get(target);

				current.getTransactions().addAll(previous.getTransactions());
				previous.getTransactions().clear();

				service.save(current);
				service.save(previous);
			}
		},
		TRUSTS {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				final TrustService service = new TrustService();
				final Trust previous = service.get(old);
				final Trust current = service.get(target);

				previous.getLocks().forEach(lock -> current.getLocks().add(lock));
				previous.getHomes().forEach(home -> current.getHomes().add(home));
				previous.getTeleports().forEach(teleport -> current.getTeleports().add(teleport));

				previous.getLocks().clear();
				previous.getHomes().clear();
				previous.getTeleports().clear();

				service.save(previous);
				service.save(current);
			}
		},
		;

		public abstract void transfer(OfflinePlayer old, OfflinePlayer target);
	}

}
