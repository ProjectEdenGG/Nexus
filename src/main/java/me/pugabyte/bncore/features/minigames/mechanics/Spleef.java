package me.pugabyte.bncore.features.minigames.mechanics;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

// TODO:
//  - Anti-camping

public class Spleef extends TeamlessMechanic {

	@Override
	public String getName() {
		return "Spleef";
	}

	@Override
	public String getDescription() {
		return "Spleef other players off the map to win!";
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public void onInitialize(Match match) {
		super.onInitialize(match);
		resetFloors(match);
	}

	@Override
	public void onEnd(Match match) {
		super.onEnd(match);
		resetFloors(match);
	}

	private void resetFloors(Match match) {
		Minigames.getWorldGuardUtils().getRegionsLike("spleef_" + match.getArena().getName() + "_floor_[0-9]+")
				.forEach(floor -> {
					String file = "spleef/" + floor.getId().replaceFirst("spleef_", "");
					Minigames.getWorldEditUtils().paste(file, floor.getMinimumPoint());
				});
	}

	@Override
	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		super.onPlayerInteract(minigamer, event);
		if (event.isCancelled()) return;

		Arena arena = minigamer.getMatch().getArena();
		Location location = event.getClickedBlock().getLocation();

		for (ProtectedRegion region : Minigames.getWorldGuardUtils().getRegionsAt(location)) {
			if (!arena.ownsRegion(region.getId(), "floor")) continue;

			event.setCancelled(true);
			Material material = location.getBlock().getType();
			Sound sound = Sound.BLOCK_STONE_BREAK;
			if (material.equals(Material.SNOW))
				sound = Sound.BLOCK_SNOW_BREAK;
			if (material.equals(Material.WOOL))
				sound = Sound.BLOCK_CLOTH_BREAK;

			minigamer.getPlayer().getWorld().playSound(location, sound, 1.0F, 0.75F);

			location.getBlock().setType(Material.AIR);
			break;
		}
	}

	@EventHandler
	void onRegionEnter(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = PlayerManager.get(player);
		if (!(minigamer.isPlaying(this) && minigamer.isAlive())) return;

		Arena arena = minigamer.getMatch().getArena();

		if (arena.ownsRegion(event.getRegion().getId(), "kill"))
			kill(minigamer);
	}
}
