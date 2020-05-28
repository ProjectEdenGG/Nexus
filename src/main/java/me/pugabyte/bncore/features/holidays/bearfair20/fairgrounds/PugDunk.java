package me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.models.bearfair.BearFairUser.BFPointSource;
import me.pugabyte.bncore.utils.CitizensUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;

public class PugDunk implements Listener {

	private static boolean enabled = false;
	private static boolean canWin = false;
	private static Location buttonLoc = new Location(BearFair20.world, -960, 139, -1594);
	private static Location dropBlock = new Location(BearFair20.world, -963, 142, -1588);
	private static Location delArrowsLoc = new Location(BearFair20.world, -961, 135, -1594);
	private BFPointSource SOURCE = BFPointSource.PUGDUNK;
	private static String gameRg = BearFair20.BFRg + "_pugdunk";
	private static String targetRg = gameRg + "_target";

	public PugDunk() {
		BNCore.registerListener(this);
		buttonTask();
	}

	public static void setPugDunkBool(boolean bool) {
		if (!bool) {
			canWin = false;
			buttonLoc.getBlock().setType(Material.AIR);
		}
		enabled = bool;
		start();
	}

	public static void dropNPC() {
		dropBlock.getBlock().setType(Material.REDSTONE_BLOCK);
		delArrowsLoc.getBlock().setType(Material.REDSTONE_BLOCK);
	}

	public static void resetNPC() {
		dropBlock.getBlock().setType(Material.AIR);
		delArrowsLoc.getBlock().setType(Material.AIR);
		Tasks.wait(10, () -> {
			Location loc = new Location(BearFair20.world, -959.5, 141, -1587.5, -90, 0);
			NPC npc = CitizensUtils.getNPC(2720);
			npc.teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
		});
	}

	public static void start() {
		if (!enabled) {
			if (WGUtils.getPlayersInRegion(gameRg).size() > 0) {
				enabled = true;
			} else {
				enabled = false;
				canWin = false;
			}
		}
	}

	private void buttonTask() {
		Tasks.repeat(0, 5, () -> {
			if (enabled) {
				if (WGUtils.getPlayersInRegion(gameRg).size() == 0)
					setPugDunkBool(false);
				else {
					if (Utils.chanceOf(25)) {
						buttonLoc.getBlock().setType(Material.STONE_BUTTON);
						Directional data = (Directional) buttonLoc.getBlock().getBlockData();
						data.setFacing(BlockFace.EAST);
						buttonLoc.getBlock().setBlockData(data);
						canWin = true;
					} else {
						buttonLoc.getBlock().setType(Material.AIR);
						canWin = false;
					}
				}
			}
		});
	}

	private void win(Player player) {
		enabled = false;
		canWin = false;
		buttonLoc.getBlock().setType(Material.AIR);

		BearFair20.world.playSound(buttonLoc, Sound.ENTITY_ARROW_HIT_PLAYER, 0.3F, 0.1F);
		BearFairUser user = new BearFairService().get(player);
		user.giveDailyPoints(1, SOURCE);
		new BearFairService().save(user);
		dropNPC();

		Tasks.wait(Time.SECOND.x(4), () -> {
			resetNPC();
			start();
		});
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		String regionId = event.getRegion().getId();
		if (regionId.equalsIgnoreCase(gameRg)) {
			if (enabled)
				return;
			setPugDunkBool(true);
		}
	}

	@EventHandler
	public void onRegionLeave(RegionLeftEvent event) {
		String regionId = event.getRegion().getId();
		if (regionId.equalsIgnoreCase(gameRg)) {
			int size = WGUtils.getPlayersInRegion(gameRg).size();
			if (size == 0)
				setPugDunkBool(false);
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		if (!(projectile instanceof Arrow)) return;

		Block hitBlock = event.getHitBlock();
		if (hitBlock == null) return;
		if (!hitBlock.getType().equals(Material.WHITE_CONCRETE)) return;

		WorldGuardUtils WGUtils = new WorldGuardUtils(BearFair20.world);
		if (!WGUtils.getRegionNamesAt(hitBlock.getLocation()).contains(targetRg)) return;
		if (!(projectile.getShooter() instanceof Player)) return;

		projectile.remove();
		if (canWin) {
			Player player = (Player) projectile.getShooter();
			win(player);
		}
	}
}
