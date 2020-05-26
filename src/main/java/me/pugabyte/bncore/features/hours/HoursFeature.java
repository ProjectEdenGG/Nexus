package me.pugabyte.bncore.features.hours;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.features.chat.Koda;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.models.nerd.Rank;
import me.pugabyte.bncore.utils.SoundUtils.Jingle;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.Utils.runConsoleCommand;

public class HoursFeature {
	final int INTERVAL = 5;

	public HoursFeature() {
		scheduler();

		BNCore.getCron().schedule("00 00 * * *", () -> new HoursService().endOfDay());
		BNCore.getCron().schedule("00 00 * * 1", () -> new HoursService().endOfWeek());
		BNCore.getCron().schedule("00 00 1 * *", () -> new HoursService().endOfMonth());
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
	}

}
