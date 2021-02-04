package me.pugabyte.nexus.features.commands.staff;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.dailyreward.DailyReward;
import me.pugabyte.nexus.models.dailyreward.DailyRewardService;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
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
		BALANCE {
			@Override
			public void run(OfflinePlayer old, OfflinePlayer target) {

			}
		},
		HOMES {
			@Override
			public void run(OfflinePlayer old, OfflinePlayer target) {

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

			}
		};

		public abstract void run(OfflinePlayer old, OfflinePlayer target);
	}

}
