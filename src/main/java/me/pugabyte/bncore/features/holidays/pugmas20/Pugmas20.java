package me.pugabyte.bncore.features.holidays.pugmas20;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.pugmas20.menu.AdventMenu;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.time.LocalDateTime;

public class Pugmas20 implements Listener {
	@Getter
	public static final String region = "pugmas20";
	// TODO PUGMAS: Change to final world
	@Getter
	public static final World world = Bukkit.getWorld("buildadmin");
	@Getter
	public static final String PREFIX = StringUtils.getPrefix("Pugmas20");
	public static final WorldGuardUtils WGUtils = new WorldGuardUtils(world);
	public static final WorldEditUtils WEUtils = new WorldEditUtils(world);
	// Dates
	public static final LocalDateTime openingDay = LocalDateTime.of(2020, 12, 1, 0, 0, 0, 0);
	public static final LocalDateTime secondChance = LocalDateTime.of(2020, 12, 25, 0, 0, 0, 0);
	public static final LocalDateTime closingDay = LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0);
	// Advent Menu

	public Pugmas20() {
		BNCore.registerListener(this);

		AdventMenu.loadHeads();
		new AdventChests();
		new Train();
	}

	public static boolean isAtPugmas(Player player) {
		return WGUtils.isInRegion(player.getLocation(), region);
	}
}
