package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.Tasks;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static gg.projecteden.nexus.utils.LocationUtils.getCenteredLocation;
import static gg.projecteden.utils.Utils.isDouble;

public class LaunchPads implements Listener {
	private static final Map<UUID, Pair<FallingBlock, Integer>> launchPadPlayers = new HashMap<>();
	private static final List<UUID> launchPadBlockUUIDs = new ArrayList<>();

	@EventHandler
	public void onPressurePlatePress(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (block == null) return;
		if (!event.getAction().equals(Action.PHYSICAL)) return;
		if (!block.getType().equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)) return;

		Player player = event.getPlayer();
		if (player.isSneaking()) return;

		Block redstone = block.getRelative(0, -1, 0).getLocation().getBlock();
		Block sign = block.getRelative(0, -2, 0).getLocation().getBlock();

		if (!(redstone.getType().equals(Material.REDSTONE_ORE))) return;
		if (launchPadPlayers.containsKey(player.getUniqueId())) return;

		if (MaterialTag.ALL_SIGNS.isTagged(sign)) {
			launchPlayer(player, new LaunchConfig(sign));
		} else if (Minigames.isMinigameWorld(player.getWorld()))
			launchPlayer(player, new LaunchConfig(10.0, 45.0, -1.0));
	}

	@Data
	@AllArgsConstructor
	private static class LaunchConfig {
		private double power, angle, direction = -1;

		LaunchConfig(Block block) {
			Sign sign = (Sign) block.getState();
			String[] lines = sign.getLines();

			if (!lines[0].equalsIgnoreCase("[LaunchPad]") || !isDouble(lines[1]) || !isDouble(lines[2])) return;

			power = Double.parseDouble(lines[1]);
			angle = Double.parseDouble(lines[2]);

			if (isDouble(lines[3]))
				direction = Double.parseDouble(lines[3]);
		}
	}

	public void launchPlayer(Player player, LaunchConfig config) {
		if (launchPadPlayers.get(player.getUniqueId()) != null)
			return;

		double power = config.getPower();
		double angle = config.getAngle();
		double direction = config.getDirection();

		if (power == 0)
			return;

		if (direction == -1.0)
			direction = player.getLocation().getYaw();
		power *= 2;

		Location launchLocation = getCenteredLocation(player.getLocation());
		launchLocation.setPitch((float) -angle);
		launchLocation.setYaw((float) direction);

		FallingBlock fallingBlock = spawnFallingBlock(power, launchLocation);

		launchPadPlayers.put(player.getUniqueId(), task(player, fallingBlock));
		launchPadBlockUUIDs.add(fallingBlock.getUniqueId());

		player.getWorld().createExplosion(player.getLocation(), -1);
	}

	@NotNull
	private FallingBlock spawnFallingBlock(double power, Location launchLocation) {
		MaterialData PISTON = new MaterialData(Material.LEGACY_PISTON_MOVING_PIECE);

		FallingBlock fallingBlock = launchLocation.getWorld().spawnFallingBlock(launchLocation, PISTON.getItemType(), PISTON.getData());
		fallingBlock.setVelocity(launchLocation.getDirection().normalize().multiply(power / 15.0));
		fallingBlock.setDropItem(false);
		return fallingBlock;
	}

	private Pair<FallingBlock, Integer> task(Player player, FallingBlock fallingBlock) {
		AtomicInteger taskId = new AtomicInteger(-1);
		taskId.set(Tasks.repeat(0, 1, () -> {
			boolean endFlight = false;
			Pair<FallingBlock, Integer> pair = launchPadPlayers.get(player.getUniqueId());
			if (pair == null)
				return;

			FallingBlock block = pair.getFirst();

			boolean isInLiquid = true, isDifferentWorld = true, isOnGround = true, isDead = true, isInVoid = true, isNotMoving = true;

			if (block != null) {
				if (fallingBlock.getUniqueId() != block.getUniqueId()) {
					cancelLaunch(player, fallingBlock, taskId.get());
					return;
				}

				isInLiquid = block.getLocation().clone().add(block.getLocation().getDirection().clone().multiply(0.5)).getBlock().isLiquid();
				isDifferentWorld = !block.getWorld().equals(player.getLocation().getWorld());
				isOnGround = block.isOnGround();
				isDead = block.isDead();
				isInVoid = block.getLocation().getY() < -10.0;
				isNotMoving = block.getVelocity().length() == 0.0;

				if (isDifferentWorld || isInLiquid)
					endFlight = true;

				if (!isDead && !endFlight)
					player.setVelocity(block.getVelocity());
			}

			if (block == null || isOnGround || isDead || isInVoid || isNotMoving || isDifferentWorld || isInLiquid || player.isOnGround()) {

				if (Dev.WAKKA.is(player) || Dev.GRIFFIN.is(player)) {
					if (!player.isOnGround()) {
						player.sendMessage("");
						player.sendMessage("Ending launch because:");
						if (block == null) player.sendMessage("  block is null");
						else if (isOnGround) player.sendMessage("  block is on ground");
						else if (isDead) player.sendMessage("  block is dead");
						else if (isInVoid) player.sendMessage("  block is in void");
						else if (isNotMoving) player.sendMessage("  block velocity is 0");
						else if (isDifferentWorld) player.sendMessage("  player world and block world are different");
						else if (isInLiquid) player.sendMessage("  block is in liquid");
						else if (player.isOnline()) player.sendMessage("  player is on ground");
					}
				}

				if (player.isOnGround()) {
					Tasks.wait(3, () -> {
						if (player.isOnGround()) {
							if (Dev.WAKKA.is(player) || Dev.GRIFFIN.is(player)) {
								player.sendMessage("");
								player.sendMessage("Ending launch because:");
								player.sendMessage("  player is on ground 2");
							}
							cancelLaunch(player, fallingBlock, taskId.get());
						}
					});
				} else {
					if (block != null) {
						block.remove();
						launchPadBlockUUIDs.remove(block.getUniqueId());
					}

					cancelLaunch(player, fallingBlock, taskId.get());
				}
			}
		}));

		return new Pair<>(fallingBlock, taskId.get());
	}

	private void cancelLaunch(Player player, FallingBlock fallingBlock, int taskId) {
		launchPadPlayers.remove(player.getUniqueId());
		if (fallingBlock != null) fallingBlock.remove();
		Tasks.cancel(taskId);
	}

	@EventHandler
	public void onEntityChangeBlockEvent(final EntityChangeBlockEvent event) {
		if (event == null) return;

		if (launchPadBlockUUIDs.contains(event.getEntity().getUniqueId()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(final EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Player player)) return;

		if (launchPadPlayers.get(player.getUniqueId()) != null)
			event.setCancelled(true);
	}

	@EventHandler
	public void onFlightToggle(final PlayerToggleFlightEvent event) {
		if (launchPadPlayers.get(event.getPlayer().getUniqueId()) != null)
			event.setCancelled(true);
	}

	@EventHandler
	public void onTeleport(final PlayerTeleportEvent event) {
		if (launchPadPlayers.get(event.getPlayer().getUniqueId()) != null)
			event.setCancelled(true);
	}

}
