package me.pugabyte.bncore.features.holidays.aeveonproject;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Adventure map for Bear Nation to replace Stranded.
@Data
public class AeveonProject implements Listener {
	@Getter
	private static final World world = Bukkit.getWorld("Aeveon_Project");
	@Getter
	public static final WorldGuardUtils WGUtils = new WorldGuardUtils(world);
	public static final WorldEditUtils WEUtils = new WorldEditUtils(world);

	public static String PREFIX = "&8&l[&eAeveonProject&8&l] &3";

	/*
	Areas:
		sialia
		sialia_crashing
		sialia_wreckage
	 */

	public AeveonProject() {
		BNCore.registerListener(this);

		Tasks.repeat(0, Time.TICK.x(10), () -> {
			Collection<? extends Player> players = Bukkit.getOnlinePlayers();
			for (Player player : players) {
				if (!isInWorld(player)) continue;

				if (isInSpace(player)) {
					if (player.getPlayerTime() != 570000)
						player.setPlayerTime(18000, false);
				} else {
					if (player.getPlayerTime() == 570000)
						player.resetPlayerTime();
				}
			}
		});
	}

	@EventHandler
	public void onEnterRegion_ShipColor(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("shipcolor")) return;

		Material concreteType = RandomUtils.randomMaterial(MaterialTag.CONCRETES);
		List<Block> blocks = WEUtils.getBlocks(WGUtils.getRegion(id));

		for (Block block : blocks) {
			if (block.getType().equals(Material.WHITE_CONCRETE))
				player.sendBlockChange(block.getLocation(), concreteType.createBlockData());
		}
	}

	@EventHandler
	public void onEnterRegion_DockingPort(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("dockingport")) return;

		player.setSprinting(true);
	}

	@EventHandler
	public void onEnterRegion_DockingTube(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("dockingtube")) return;

		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 15, false, false));
//		player.setGravity(false);
	}

	@EventHandler
	public void onExitRegion_DockingTube(RegionLeftEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("dockingtube")) return;

		player.removePotionEffect(PotionEffectType.SPEED);
//		player.setGravity(true);
	}

	public static boolean isInSpace(Player player) {
		Set<ProtectedRegion> regions = WGUtils.getRegionsAt(player.getLocation());
		Set<ProtectedRegion> spaceRegions = regions.stream().filter(region -> region.getId().contains("space")).collect(Collectors.toSet());
		return spaceRegions.size() > 0;
	}

	public static boolean isInWorld(Block block) {
		return isInWorld(block.getLocation());
	}

	public static boolean isInWorld(Player player) {
		return isInWorld(player.getLocation());
	}

	public static boolean isInWorld(Location location) {
		return location.getWorld().equals(AeveonProject.getWorld());

	}
}
