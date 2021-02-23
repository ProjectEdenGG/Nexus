package me.pugabyte.nexus.features.commands.staff;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.alerts.Alerts;
import me.pugabyte.nexus.models.alerts.AlertsService;
import me.pugabyte.nexus.models.dailyreward.DailyReward;
import me.pugabyte.nexus.models.dailyreward.DailyRewardService;
import me.pugabyte.nexus.models.eventuser.EventUser;
import me.pugabyte.nexus.models.eventuser.EventUserService;
import me.pugabyte.nexus.models.home.HomeOwner;
import me.pugabyte.nexus.models.home.HomeService;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.NerdService;
import me.pugabyte.nexus.models.trust.Trust;
import me.pugabyte.nexus.models.trust.TrustService;
import org.bukkit.OfflinePlayer;

import java.util.List;

@Permission("group.admin")
public class AccountTransferCommand extends CustomCommand {

	public AccountTransferCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<old> <new> <features...>")
	void transfer(OfflinePlayer old, OfflinePlayer target, @Arg(type = Transferable.class) List<Transferable> features) {
		features.forEach(feature -> feature.run(old, target));
	}

	public enum Transferable {
		NERD {
			@Override
			public void run(OfflinePlayer old, OfflinePlayer target) {
				NerdService service = new NerdService();
				Nerd previous = service.get(old);
				Nerd current = service.get(target);

				current.setPreferredName(previous.getPreferredName());
				current.setBirthday(previous.getBirthday());
				current.setFirstJoin(previous.getFirstJoin());
				current.setLastJoin(previous.getLastJoin());
				current.setLastQuit(previous.getLastQuit());
				current.setPromotionDate(previous.getPromotionDate());
				current.setAbout(previous.getAbout());
				current.setMeetMeVideo(previous.isMeetMeVideo());

				previous.setPreferredName(null);
				current.setBirthday(null);
				current.setPromotionDate(null);
				current.setAbout(null);
				current.setMeetMeVideo(false);

				service.save(previous);
				service.save(current);
			}
		},
		ALERTS {
			@Override
			public void run(OfflinePlayer old, OfflinePlayer target) {
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
			public void run(OfflinePlayer old, OfflinePlayer target) {
				double balance = Nexus.getEcon().getBalance(old);
				Nexus.getEcon().withdrawPlayer(old, balance);
				Nexus.getEcon().depositPlayer(target, balance);
			}
		},
		HOMES {
			@Override
			public void run(OfflinePlayer old, OfflinePlayer target) {
				HomeService service = new HomeService();
				HomeOwner previous = service.get(old);
				HomeOwner current = service.get(target);

				previous.getHomes().forEach(home -> current.getHomes().add(home));
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
			public void run(OfflinePlayer old, OfflinePlayer target) {
				HoursService service = new HoursService();
				Hours previous = service.get(old);
				Hours current = service.get(target);

				previous.getTimes().forEach((date, seconds) -> current.getTimes().put(date, previous.getTimes().get(date) + seconds));
				previous.getTimes().clear();

				service.save(previous);
				service.save(current);
			}
		},
		DAILYREWARDS {
			@Override
			public void run(OfflinePlayer old, OfflinePlayer target) {
				DailyRewardService service = new DailyRewardService();
				DailyReward previous = service.get(old);
				DailyReward current = service.get(target);

				current.setStreak(previous.getStreak());
				current.setClaimed(previous.getClaimed());

				previous.setActive(false);

				service.save(previous);
				service.save(current);
			}
		},
		TRUSTS {
			@Override
			public void run(OfflinePlayer old, OfflinePlayer target) {
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
			public void run(OfflinePlayer old, OfflinePlayer target) {
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
			public void run(OfflinePlayer old, OfflinePlayer target) {
				// Which history?
			}
		},
		EVENTUSER {
			@Override
			public void run(OfflinePlayer old, OfflinePlayer target) {
				EventUserService service = new EventUserService();
				EventUser previous = service.get(old);
				EventUser current = service.get(target);

				current.setTokens(previous.getTokens());
				previous.getTokensReceivedToday().forEach((string, map)
						-> current.getTokensReceivedToday().put(string, map));

				previous.getTokensReceivedToday().clear();

				service.save(previous);
				service.save(current);
			}
		};

		public abstract void run(OfflinePlayer old, OfflinePlayer target);
	}

}
