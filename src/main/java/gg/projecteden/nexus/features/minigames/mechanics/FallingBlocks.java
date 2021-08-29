package gg.projecteden.nexus.features.minigames.mechanics;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FallingBlocks extends TeamlessMechanic {

	@Override
	public @NotNull String getName() {
		return "Falling Blocks";
	}

	@Override
	public @NotNull String getDescription() {
		return "Climb to the top without getting squished";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.SAND);
	}

	@Override
	public void onBegin(@NotNull MatchBeginEvent event) {
		final Match match = event.getMatch();
		final WorldGuardUtils worldGuardUtils = match.getWGUtils();
		final ProtectedRegion ceiling = match.getArena().getProtectedRegion("ceiling");
		final int y = (int) worldGuardUtils.toLocation(ceiling.getMinimumPoint()).getY();
		match.getTasks().repeat(0, TickTime.TICK.x(3), () -> {
			for (Minigamer minigamer : match.getAliveMinigamers()) {
				final Location location = minigamer.getLocation();
				location.setY(y);
				final BlockData blockData = Bukkit.createBlockData(Material.SAND);
				final FallingBlock fallingBlock = match.getWorld().spawnFallingBlock(location.toCenterLocation(), blockData);
				fallingBlock.setDropItem(false);
				fallingBlock.setInvulnerable(true);
				fallingBlock.setVelocity(new org.bukkit.util.Vector(0, -0.5, 0));
			}
		});
	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		if (!event.getTo().equals(Material.SAND))
			return;

		final Location location = event.getBlock().getLocation();
		Match match = MatchManager.getActiveMatchFromLocation(this, location);
		if (match == null)
			return;

		for (ProtectedRegion region : match.getWGUtils().getRegionsAt(location)) {
			if (!match.getArena().ownsRegion(region))
				continue;

			for (Minigamer minigamer : match.getAliveMinigamers())
				if (LocationUtils.blockLocationsEqual(minigamer.getLocation(), location))
					kill(minigamer);
			return;
		}
	}

	@EventHandler
	public void onPlayerEnteringRegion(PlayerEnteringRegionEvent event) {
		final Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		if (event.getRegion().getId().contains("ceiling"))
			minigamer.scored();
	}

	private static final List<DamageCause> DAMAGE_CAUSES = List.of(DamageCause.SUFFOCATION, DamageCause.FALLING_BLOCK);

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!DAMAGE_CAUSES.contains(event.getCause()))
			return;

		if (!(event.getEntity() instanceof Player player))
			return;

		final Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this))
			return;

		kill(minigamer);
	}

}
