package gg.projecteden.nexus.features.events.y2020.bearfair20.fairgrounds;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2020.bearfair20.BearFair20;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.models.bearfair20.BearFair20User;
import gg.projecteden.nexus.models.bearfair20.BearFair20User.BF20PointSource;
import gg.projecteden.nexus.models.bearfair20.BearFair20UserService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
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

public class PugDunk implements Listener {

	private static boolean enabled = false;
	private static boolean canWin = false;
	private static Location buttonLoc = new Location(BearFair20.getWorld(), -960, 139, -1594);
	private static Location dropBlock = new Location(BearFair20.getWorld(), -963, 142, -1588);
	private static Location delArrowsLoc = new Location(BearFair20.getWorld(), -961, 135, -1594);
	private BF20PointSource SOURCE = BF20PointSource.PUGDUNK;
	private static String gameRg = BearFair20.getRegion() + "_pugdunk";
	private static String targetRg = gameRg + "_target";

	public PugDunk() {
		Nexus.registerListener(this);
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
			Location loc = new Location(BearFair20.getWorld(), -959.5, 141, -1587.5, -90, 0);
			NPC npc = CitizensUtils.getNPC(2720);
			npc.teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
		});
	}

	public static void start() {
		if (!enabled) {
			if (BearFair20.worldguard().getPlayersInRegion(gameRg).size() > 0) {
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
				if (BearFair20.worldguard().getPlayersInRegion(gameRg).size() == 0)
					setPugDunkBool(false);
				else {
					if (RandomUtils.chanceOf(25)) {
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

		BearFair20.getWorld().playSound(buttonLoc, Sound.ENTITY_ARROW_HIT_PLAYER, 0.3F, 0.1F);

		if (BearFair20.giveDailyPoints) {
			BearFair20User user = new BearFair20UserService().get(player);
			user.giveDailyPoints(SOURCE);
			new BearFair20UserService().save(user);
		}

		dropNPC();

		Tasks.wait(TickTime.SECOND.x(4), () -> {
			resetNPC();
			start();
		});
	}

	@EventHandler
	public void onRegionEnter(PlayerEnteredRegionEvent event) {
		String regionId = event.getRegion().getId();
		if (regionId.equalsIgnoreCase(gameRg)) {
			if (enabled)
				return;
			setPugDunkBool(true);
		}
	}

	@EventHandler
	public void onRegionLeave(PlayerLeftRegionEvent event) {
		String regionId = event.getRegion().getId();
		if (regionId.equalsIgnoreCase(gameRg)) {
			int size = BearFair20.worldguard().getPlayersInRegion(gameRg).size();
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

		if (!BearFair20.isInRegion(hitBlock, targetRg)) return;
		if (!(projectile.getShooter() instanceof Player shooter)) return;

		projectile.remove();
		if (canWin)
			win(shooter);
	}
}
