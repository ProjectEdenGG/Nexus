package me.pugabyte.bncore.features.hours;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.models.Rank;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.skript.SkriptFunctions;
import me.pugabyte.bncore.utils.Jingles;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import static me.pugabyte.bncore.utils.Utils.colorize;

public class HoursFeature {
	final int INTERVAL = 5;

	public HoursFeature() {
		registerPlaceholder();
		scheduler();
	}

	private void registerPlaceholder() {
		BNCore.registerPlaceholder("hours", event -> {
			Hours hours = new HoursService().get(event.getPlayer());
			return Utils.timespanFormat(hours.getTotal());
		});
	}

	private void scheduler() {
		Tasks.repeatAsync(10, INTERVAL * 20, () -> {
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
								PermissionsEx.getUser(player).removeGroup("Guest");
								PermissionsEx.getUser(player).addGroup("Member");
								SkriptFunctions.koda("Congrats on Member rank, " + player.getName(), "md");
								Jingles.rankup(player);
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
