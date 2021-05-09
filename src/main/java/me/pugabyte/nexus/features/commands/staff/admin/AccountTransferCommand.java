package me.pugabyte.nexus.features.commands.staff.admin;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import com.griefcraft.cache.ProtectionCache;
import com.griefcraft.model.Protection;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.Sync;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.alerts.Alerts;
import me.pugabyte.nexus.models.alerts.AlertsService;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import me.pugabyte.nexus.models.dailyreward.DailyReward;
import me.pugabyte.nexus.models.dailyreward.DailyRewardService;
import me.pugabyte.nexus.models.eventuser.EventUser;
import me.pugabyte.nexus.models.eventuser.EventUserService;
import me.pugabyte.nexus.models.home.HomeOwner;
import me.pugabyte.nexus.models.home.HomeService;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.models.lwc.LWCProtection;
import me.pugabyte.nexus.models.lwc.LWCProtectionService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.NerdService;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.models.trust.Trust;
import me.pugabyte.nexus.models.trust.TrustService;
import me.pugabyte.nexus.utils.Tasks;
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
		NERD {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				NerdService service = new NerdService();
				Nerd previous = Nerd.of(old);
				Nerd current = Nerd.of(target);

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
		ALERTS {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				AlertsService service = new AlertsService();
				Alerts previous = service.get(old);
				Alerts current = service.get(target);

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
				BankerService service = new BankerService();
				for (ShopGroup shopGroup : ShopGroup.values()) {
					double balance = service.getBalance(old, shopGroup);
					service.transfer(old, target, balance, shopGroup, TransactionCause.SERVER);
				}
			}
		},
		HOMES {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				HomeService service = new HomeService();
				HomeOwner previous = service.get(old);
				HomeOwner current = service.get(target);

				previous.getHomes().forEach(current::add);
				previous.getHomes().clear();

				current.setAutoLock(previous.isAutoLock());
				current.setUsedDeathHome(previous.isUsedDeathHome());
				Nexus.getPerms().playerAdd(null, target, "homes.limit." + previous.getMaxHomes());

				service.save(previous);
				service.save(current);
			}
		},
		HOURS {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				HoursService service = new HoursService();
				Hours previous = service.get(old.getUniqueId());
				Hours current = service.get(target.getUniqueId());

				previous.getTimes().forEach((date, seconds) -> current.getTimes().put(date, seconds));
				previous.getTimes().clear();

				service.save(previous);
				service.save(current);
			}
		},
		DAILYREWARDS {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				DailyRewardService service = new DailyRewardService();
				DailyReward previous = service.get(old);
				DailyReward current = service.get(target);

				current.setStreak(previous.getStreak());
				current.setClaimed(previous.getClaimed());
				if (!current.isEarnedToday())
					current.setEarnedToday(previous.isEarnedToday());

				previous.setActive(false);

				service.save(previous);
				service.save(current);
			}
		},
		TRUSTS {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				TrustService service = new TrustService();
				Trust previous = service.get(old);
				Trust current = service.get(target);

				previous.getLocks().forEach(lock -> current.getLocks().add(lock));
				previous.getHomes().forEach(home -> current.getHomes().add(home));

				previous.getLocks().clear();
				previous.getHomes().clear();

				service.save(previous);
				service.save(current);
			}
		},
		MCMMO {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				McMMOPlayer previous = UserManager.getPlayer(old.getName());
				McMMOPlayer current = UserManager.getPlayer(target.getName());
				for (PrimarySkillType skill : PrimarySkillType.values()) {
					current.modifySkill(skill, previous.getSkillLevel(skill));
					previous.modifySkill(skill, 0);
				}

				previous.getProfile().scheduleAsyncSave();
				current.getProfile().scheduleAsyncSave();
			}
		},
		HISTORY {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				// Which history?
			}
		},
		EVENTUSER {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				EventUserService service = new EventUserService();
				EventUser previous = service.get(old);
				EventUser current = service.get(target);

				current.setTokens(previous.getTokens());
				previous.getTokensReceivedToday().forEach((string, map) -> current.getTokensReceivedToday().put(string, map));
				previous.getTokensReceivedToday().clear();

				service.save(previous);
				service.save(current);
			}
		},
		LWC {
			@Override
			public void transfer(OfflinePlayer old, OfflinePlayer target) {
				ProtectionCache protectionCache = com.griefcraft.lwc.LWC.getInstance().getProtectionCache();
				LWCProtectionService service = new LWCProtectionService();
				List<LWCProtection> oldProtections = service.getPlayerProtections(old.getUniqueId());

				for (LWCProtection oldProtection : oldProtections) {
					Protection protectionById = protectionCache.getProtectionById(oldProtection.getId());
					if (protectionById != null) {
						protectionById.setOwner(target.getUniqueId().toString());
						protectionById.save();
					}
				}
			}
		};

		public abstract void transfer(OfflinePlayer old, OfflinePlayer target);
	}

}
