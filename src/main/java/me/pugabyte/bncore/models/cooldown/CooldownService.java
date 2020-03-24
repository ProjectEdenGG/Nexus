package me.pugabyte.bncore.models.cooldown;

import me.pugabyte.bncore.framework.exceptions.postconfigured.CooldownException;
import me.pugabyte.bncore.models.MySQLService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class CooldownService extends MySQLService {

	static {
		Tasks.repeatAsync(10, Time.SECOND.x(5), () -> database.table("cooldown").where("time <= DATE_ADD(NOW(), INTERVAL -(ticks / 20) SECOND)").delete());
	}

	public boolean check(Player player, String type, Time time) {
		return check(player.getUniqueId().toString(), type, time);
	}

	public boolean check(String id, String type, Time time) {
		return check(id, type, time.get());
	}

	public boolean check(Player player, String type, double ticks) {
		return check(player.getUniqueId().toString(), type, ticks);
	}

	public boolean check(String id, String type, double ticks) {
		Cooldown cooldown = database.where("id = ?").and("type = ?").args(id, type).first(Cooldown.class);
		boolean canRun = true;
		if (cooldown.getTime() != null) {
			long ticksElapsed = cooldown.getTime().until(LocalDateTime.now(), ChronoUnit.SECONDS) * 20;
			canRun = ticksElapsed >= ticks;
		} else
			cooldown = new Cooldown(id, type, ticks);

	/*
		if (!canRun && id.length() == 36) {
			OfflinePlayer player = Utils.getPlayer(UUID.fromString(id));
			if (player.isOnline() && player.getPlayer().hasPermission("cooldown.bypass." + type))
				canRun = true;
		}
	*/

		if (canRun) {
			cooldown.update();
			save(cooldown);
		} else
			throw new CooldownException(cooldown);

		return canRun;
	}

}
