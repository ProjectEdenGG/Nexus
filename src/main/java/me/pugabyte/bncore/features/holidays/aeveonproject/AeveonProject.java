package me.pugabyte.bncore.features.holidays.aeveonproject;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import lombok.Data;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.RandomUtils;
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

import java.util.List;

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
