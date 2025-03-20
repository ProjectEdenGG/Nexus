package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
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

		event.setCancelled(true);
		event.setUseInteractedBlock(Result.DENY);

		Block redstone = block.getRelative(0, -1, 0).getLocation().getBlock();
		Block sign = block.getRelative(0, -2, 0).getLocation().getBlock();

		if (!(redstone.getType().equals(Material.REDSTONE_ORE))) return;
		if (launchPadPlayers.containsKey(player.getUniqueId())) return;

		if (MaterialTag.ALL_SIGNS.isTagged(sign)) {
			launchPlayer(player, new LaunchConfig(sign));
		} else if (Minigames.isMinigameWorld(player.getWorld()))
			launchPlayer(player, new LaunchConfig(10.0, 45.0, null));
	}

	@Data
	@AllArgsConstructor
	private static class LaunchConfig {
		private double power, angle;
		private Double direction;

		LaunchConfig(Block block) {
			Sign sign = (Sign) block.getState();
			String[] lines = sign.getLines();

			if (!lines[0].equalsIgnoreCase("[LaunchPad]") || !Utils.isDouble(lines[1]) || !Utils.isDouble(lines[2]))
				return;

			power = Double.parseDouble(lines[1]);
			angle = Double.parseDouble(lines[2]);

			if (Utils.isDouble(lines[3]))
				direction = Double.parseDouble(lines[3]);
		}
	}

	public void launchPlayer(Player player, LaunchConfig config) {
		if (launchPadPlayers.get(player.getUniqueId()) != null)
			return;

		double power = config.getPower();
		double angle = config.getAngle();
		Double direction = config.getDirection();

		if (power == 0)
			return;

		if (direction == null)
			direction = (double) player.getLocation().getYaw();
		power *= 2;

		Location launchLocation = LocationUtils.getCenteredLocation(player.getLocation());
		launchLocation.setPitch((float) -angle);
		launchLocation.setYaw(direction.floatValue());

		FallingBlock fallingBlock = spawnFallingBlock(power, launchLocation);

		launchPadPlayers.put(player.getUniqueId(), task(player, fallingBlock));
		launchPadBlockUUIDs.add(fallingBlock.getUniqueId());

		player.getWorld().createExplosion(player.getLocation(), -1);
	}

	@SuppressWarnings({"removal", "deprecation"})
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
			Pair<FallingBlock, Integer> pair = launchPadPlayers.get(player.getUniqueId());
			if (pair == null)
				return;

			FallingBlock block = pair.getFirst();
			int currentTaskId = pair.getSecond();

			Runnable cancel = () -> cancelLaunch(player, fallingBlock, taskId.get());

			if (block == null) {
				cancel.run();
				return;
			}

			if (block.getUniqueId() != fallingBlock.getUniqueId()) {
				cancel.run();
				return;
			}

			if (currentTaskId != taskId.get()) {
				cancel.run();
				return;
			}

			final boolean isOnGround = block.isOnGround();
			final boolean isDead = block.isDead();
			final boolean isInVoid = block.getLocation().getY() < -10.0;
			final boolean isNotMoving = block.getVelocity().length() == 0.0;
			final boolean isDifferentWorld = !block.getWorld().equals(player.getLocation().getWorld());
			final boolean isInLiquid = block.getLocation().clone().add(block.getLocation().getDirection().clone().multiply(0.5)).getBlock().isLiquid();

			final boolean endLaunch = isOnGround || isDead || isInVoid || isNotMoving || isDifferentWorld || isInLiquid;

			if (endLaunch)
				cancel.run();
			else
				player.setVelocity(block.getVelocity());
		}));

		return new Pair<>(fallingBlock, taskId.get());
	}

	public boolean isLaunching(Player player) {
		return launchPadPlayers.containsKey(player.getUniqueId());
	}

	public void cancelLaunch(Player player) {
		if (!isLaunching(player))
			return;

		Pair<FallingBlock, Integer> data = launchPadPlayers.get(player.getUniqueId());
		cancelLaunch(player, data.getFirst(), data.getSecond());
	}

	private void cancelLaunch(Player player, FallingBlock fallingBlock, int taskId) {
		launchPadPlayers.remove(player.getUniqueId());
		Tasks.cancel(taskId);

		if (fallingBlock != null) {
			launchPadBlockUUIDs.remove(fallingBlock.getUniqueId());
			fallingBlock.remove();
		}
	}

	@EventHandler
	public void on(final EntityChangeBlockEvent event) {
		if (event == null) return;

		if (launchPadBlockUUIDs.contains(event.getEntity().getUniqueId()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(final EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		if (isLaunching(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void on(final PlayerToggleFlightEvent event) {
		if (isLaunching(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void on(final PlayerTeleportEvent event) {
		if (isLaunching(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerEnteringRegionEvent event) {
		Player player = event.getPlayer();
		if (event.getRegion().getId().contains("_kill"))
			if (isLaunching(player))
				cancelLaunch(player);
	}

}
