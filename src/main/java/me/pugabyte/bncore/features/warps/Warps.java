package me.pugabyte.bncore.features.warps;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Warps {
	@Getter
	private static Location spawn = new Location(Bukkit.getWorld("world"), -1.5, 156.0, -19.5, 90.0F, 0.0F);

	public void spawn(Player player) {
		player.teleport(spawn);
	}

}
