package me.pugabyte.bncore.features.hours;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.features.chat.Koda;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.models.hours.HoursService.HoursType;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.Rank;
import me.pugabyte.bncore.utils.CitizensUtils;
import me.pugabyte.bncore.utils.SoundUtils.Jingle;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.Utils.runConsoleCommand;

public class HoursFeature {
	final int INTERVAL = 5;

	public HoursFeature() {
		registerPlaceholder();
		scheduler();

		BNCore.getCron().schedule("00 00 * * *", () -> new HoursService().endOfDay());
		BNCore.getCron().schedule("00 00 * * 1", () -> new HoursService().endOfWeek());
		BNCore.getCron().schedule("00 00 1 * *", () -> new HoursService().endOfMonth());
	}

	private void registerPlaceholder() {
		BNCore.registerPlaceholder("hours", event -> {
			Hours hours = new HoursService().get(event.getPlayer());
			return StringUtils.timespanFormat(hours.getTotal());
		});
	}

	private void scheduler() {
		Tasks.repeatAsync(10, Time.SECOND.x(INTERVAL), () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				try {
					if (AFK.get(player).isAfk()) continue;

					HoursService service = new HoursService();
					Hours hours = service.get(player);
					hours.increment(INTERVAL);
					service.save(hours);

					if (Rank.getHighestRank(player) == Rank.GUEST) {
						if (hours.getTotal() > (60 * 60 * 24)) {
							Tasks.sync(() -> {
								runConsoleCommand("lp user " + player.getName() + " parent set " + Rank.MEMBER.name());
								Koda.say("Congrats on Member rank, " + player.getName() + "!");
								Jingle.RANKUP.play(player);
								player.sendMessage("");
								player.sendMessage("");
								player.sendMessage(colorize("&e&lCongratulations! &3You have been promoted to &fMember&3 for " +
										"playing for &e24 hours &3in-game. You are now eligible for &c/trusted&3."));
								player.sendMessage("");
								player.sendMessage(colorize("&6&lThank you for flying Bear Nation!"));
							});
						}
					}
				} catch (Exception ex) {
					BNCore.warn("Error in Hours scheduler: " + ex.getMessage());
				}
			}
		});

		Tasks.repeatAsync(10, Time.HOUR, () -> {
			List<Hours> total = new HoursService().getPage(HoursType.TOTAL, 1);
			List<Hours> monthly = new HoursService().getPage(HoursType.MONTHLY, 1);

			CitizensUtils.updateNameAndSkin(2709, new Nerd(total.get(0).getUuid()).getRankFormat());
			CitizensUtils.updateNameAndSkin(2708, new Nerd(total.get(1).getUuid()).getRankFormat());
			CitizensUtils.updateNameAndSkin(2707, new Nerd(total.get(2).getUuid()).getRankFormat());

			CitizensUtils.updateNameAndSkin(2712, new Nerd(monthly.get(0).getUuid()).getRankFormat());
			CitizensUtils.updateNameAndSkin(2711, new Nerd(monthly.get(1).getUuid()).getRankFormat());
			CitizensUtils.updateNameAndSkin(2710, new Nerd(monthly.get(2).getUuid()).getRankFormat());
		});
	}

}
