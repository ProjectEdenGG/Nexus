package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LaunchPads implements Listener {
	private static Map<Player, Integer> taskIDs = new HashMap<>();
	private static Map<Player, FallingBlock> jumpPadPlayers = new HashMap<>();
	private static List<UUID> jumpPadBlockUUIDs = new ArrayList<>();

	@EventHandler
	public void onPressurePlatePress(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (block == null) return;
		if (!event.getAction().equals(Action.PHYSICAL)) return;
		if (!block.getType().equals(Material.GOLD_PLATE)) return;
		if (event.getPlayer().isSneaking()) return;

		Block below = block.getRelative(0, -1, 0).getLocation().getBlock();
		if (!(below.getType().equals(Material.GLOWING_REDSTONE_ORE) || below.getType().equals(Material.REDSTONE_ORE)))
			return;

		Block belowBelow = below.getRelative(0, -1, 0).getLocation().getBlock();
		if (Utils.isNullOrAir(belowBelow.getType())) return;

		event.setCancelled(true);

		if (Utils.isSign(belowBelow.getType())) {
			Sign sign = (Sign) belowBelow.getState();
			String[] lines = sign.getLines();

			if (!lines[0].equalsIgnoreCase("[LaunchPad]")) return;
			if (lines[1].equalsIgnoreCase("") || lines[2].equalsIgnoreCase("")) return;

			double power = Double.parseDouble(lines[1]);
			double angle = Double.parseDouble(lines[2]);

			if (!lines[3].equalsIgnoreCase("")) {
				double direction = Double.parseDouble(lines[3]);
				launchPlayer(event.getPlayer(), power, angle, direction);
			}

			launchPlayer(event.getPlayer(), power, angle);
		} else if (WorldGroup.get(block.getLocation().getWorld()).equals(WorldGroup.MINIGAMES))
			launchPlayer(event.getPlayer());
	}

	public void launchPlayer(Player player) {
		launchPlayer(player, 10.0, 45.0, -1.0);
	}

	public void launchPlayer(Player player, double power, double angle) {
		launchPlayer(player, power, angle, -1.0);
	}

	public void launchPlayer(Player player, double power, double angle, double direction) {
		if (jumpPadPlayers.get(player) != null)
			return;

		if (direction == -1.0)
			direction = player.getLocation().getYaw();
		power *= 2;

		Location launchLocation = player.getLocation();
		launchLocation.setPitch((float) -angle);
		launchLocation.setYaw((float) direction);

		// Piston Moving Piece is invisible?
		MaterialData PISTON = new MaterialData(Material.PISTON_MOVING_PIECE);

		FallingBlock fallingBlock = launchLocation.getWorld().spawnFallingBlock(launchLocation, PISTON.getItemType(), PISTON.getData());
		jumpPadPlayers.put(player, fallingBlock);
		jumpPadBlockUUIDs.add(fallingBlock.getUniqueId());
		fallingBlock.setVelocity(launchLocation.getDirection().normalize().multiply(power / 15.0));

		playerVelTask(player);
		player.getWorld().createExplosion(player.getLocation(), -1);
	}

	private void playerVelTask(Player player) {
		taskIDs.put(player, Tasks.repeat(0, 1, () -> {
			boolean endFlight = false;
			FallingBlock fBlock = jumpPadPlayers.get(player);
			fBlock.setDropItem(false);

			if (fBlock != null) {
				if ((fBlock.getWorld() != null && !player.getLocation().getWorld().equals(fBlock.getWorld()))
						|| fBlock.getLocation().clone().add(fBlock.getLocation().getDirection().clone().multiply(0.5)).getBlock().isLiquid())
					endFlight = true;

				if (!fBlock.isDead() && !endFlight)
					player.setVelocity(fBlock.getVelocity());
			}

			if (fBlock == null || fBlock.isOnGround() || fBlock.isDead() || fBlock.getLocation().getY() < -10.0
					|| fBlock.getVelocity().length() == 0.0 || endFlight || jumpPadPlayers.get(player) == null || player.isOnGround()) {

				if (fBlock != null) {
					fBlock.remove();
					jumpPadBlockUUIDs.remove(fBlock.getUniqueId());
				}

				jumpPadPlayers.remove(player);
				cancelPlayerVelTask(player);
			}
		}));
	}

	private void cancelPlayerVelTask(Player player) {
		Tasks.cancel(taskIDs.get(player));
	}

	@EventHandler
	public void onEntityChangeBlockEvent(final EntityChangeBlockEvent event) {
		if (event == null) return;

		if (jumpPadBlockUUIDs.contains(event.getEntity().getUniqueId()))
			event.setCancelled(true);

	}

	@EventHandler
	public void onDamage(final EntityDamageEvent event) {
		if (event.getEntity() == null) return;
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		if (jumpPadPlayers.get(player) != null)
			event.setCancelled(true);
	}

	@EventHandler
	public void onFlightToggle(final PlayerToggleFlightEvent event) {
		if (jumpPadPlayers.get(event.getPlayer()) != null)
			event.setCancelled(true);
	}

	@EventHandler
	public void onTeleport(final PlayerTeleportEvent event) {
		if (jumpPadPlayers.get(event.getPlayer()) != null) {
			event.setCancelled(true);
		}
	}

}