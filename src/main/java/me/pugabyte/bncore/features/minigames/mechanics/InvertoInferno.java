package me.pugabyte.bncore.features.minigames.mechanics;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InvertoInferno extends TeamlessMechanic {

	@Override
	public String getName() {
		return "Inverto Inferno";
	}

	@Override
	public String getDescription() {
		return "Put out the fire!";
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public void onStart(Match match) {
		super.onStart(match);
		ProtectedRegion region = Minigames.getWorldGuardUtils().getRegion("invertoinferno");
		int percent = 5 / (100 * region.volume());
		for (int i = 0; i < percent; i++) {
			Block block = Minigames.getWorldGuardUtils().getRandomBlock(region);
			if (block.getType().isBurnable()) {
				block.getRelative(0, 1, 0);
				if (block.getType().equals(Material.AIR))
					block.setType(Material.FIRE);
			}
		}
	}

	@Override
	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		super.onPlayerInteract(minigamer, event);
		if (event.isCancelled()) return;

		Block fire = event.getClickedBlock().getRelative(event.getBlockFace());
		if (!fire.getType().equals(Material.FIRE)) {
			event.setCancelled(true);
			return;
		}

		minigamer.scored();
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		Location location = event.getBlock().getLocation();

		for (ProtectedRegion region : Minigames.getWorldGuardUtils().getRegionsAt(location)) {
			if (region.getId().equalsIgnoreCase("invertoinferno")) {
				event.setCancelled(true);
				break;
			}
		}
	}

}
