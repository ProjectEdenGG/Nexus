package me.pugabyte.bncore.features.holidays.aeveonproject;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProjectRegions.sialia_dockingports;
import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProjectRegions.sialia_shipColor;

@Data
public class AeveonProject implements Listener {
	@Getter
	private static final World world = Bukkit.getWorld("Aeveon_Project");
	@Getter
	public static final WorldGuardUtils WGUtils = new WorldGuardUtils(world);
	public static final WorldEditUtils WEUtils = new WorldEditUtils(world);

	public static String PREFIX = "&8&l[&eAeveonProject&8&l] &3";
	public static String ROOT = "Animations/AeveonProject/";

	static List<Player> inGravlift = new ArrayList<>();

	public AeveonProject() {
		BNCore.registerListener(this);

		// PlayerTime Control
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

		// Gravlift control
		Tasks.repeat(0, Time.TICK.x(5), () -> {
			List<Player> inGravLiftCopy = new ArrayList<>(inGravlift);
			for (Player player : inGravLiftCopy) {
				Set<ProtectedRegion> regions = WGUtils.getRegionsAt(player.getLocation());
				// Make sure they are in the region still
				boolean verify = false;
				for (ProtectedRegion region : regions) {
					if (region.getId().contains("gravlift")) {
						verify = true;
						break;
					}
				}
				if (!verify) {
					inGravlift.remove(player);
					continue;
				}
				//

				if (player.isSneaking())
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 6, 4, false, false, false));
				else
					player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 6, 3, false, false, false));
			}
		});
	}
	// Command that teleports armorstand to their pitch & yaw is 0

	@EventHandler
	public void onEnterRegion_GravLift(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("gravlift")) return;

		if (inGravlift.contains(player)) return;
		inGravlift.add(player);
//		Utils.wakka("Added");
	}

	@EventHandler
	public void onExitRegion_GravLift(RegionLeftEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("gravlift")) return;

		if (!inGravlift.contains(player)) return;
		inGravlift.remove(player);
//		Utils.wakka("Removed");
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		if (event.getFrom().equals(world))
			event.getPlayer().resetPlayerTime();
	}

	@EventHandler
	public void onClickNetherBrickStair(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block clicked = player.getTargetBlockExact(2);
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		if (clicked == null) return;
		if (!isInWorld(clicked)) return;
		if (!(new CooldownService().check(player, "AeveonProject_Sit", Time.SECOND.x(2)))) return;

		if (clicked.getType().equals(Material.NETHER_BRICK_STAIRS)) {
			Utils.runCommandAsOp(player, "sit");
		}

	}

	@EventHandler
	public void onEnterRegion_Update(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("update")) return;

		// Sialia
		if (id.contains("sialia_shipcolor")) {
			// Ship Color
			Material concreteType = RandomUtils.randomMaterial(MaterialTag.CONCRETES);
			List<Block> blocks = WEUtils.getBlocks(WGUtils.getRegion(sialia_shipColor));

			for (Block block : blocks) {
				if (block.getType().equals(Material.WHITE_CONCRETE))
					player.sendBlockChange(block.getLocation(), concreteType.createBlockData());
			}

			// Docking Port #1 & #2 - Water
			blocks.clear();
			for (int i = 1; i <= 2; i++) {
				blocks = WEUtils.getBlocks(WGUtils.getRegion(sialia_dockingports.replaceAll("#", String.valueOf(i))));
				for (Block block : blocks) {
					if (block.getType().equals(Material.WATER))
						player.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
				}
			}
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
	}

	@EventHandler
	public void onExitRegion_DockingTube(RegionLeftEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("dockingtube")) return;

		player.removePotionEffect(PotionEffectType.SPEED);
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
