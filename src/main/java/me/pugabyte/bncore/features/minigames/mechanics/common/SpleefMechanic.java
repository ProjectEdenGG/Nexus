package me.pugabyte.bncore.features.minigames.mechanics.common;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class SpleefMechanic extends TeamlessMechanic {

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
		Minigames.getWorldGuardUtils().getRegionsLike(getName() + "_" + match.getArena().getName() + "_floor_[0-9]+")
				.forEach(floor -> {
					String file = (getName() + "/" + floor.getId().replaceFirst(getName().toLowerCase() + "_", "")).toLowerCase();
					Minigames.getWorldEditUtils().paste(file, floor.getMinimumPoint());
				});
	}

	@EventHandler
	public void onRegionEntered(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = PlayerManager.get(player);
		if (!(minigamer.isPlaying(this))) return;

		Arena arena = minigamer.getMatch().getArena();

		if (arena.ownsRegion(event.getRegion().getId(), "kill"))
			kill(minigamer);
	}

	public boolean breakBlock(Arena arena, Location location) {
		for (ProtectedRegion region : Minigames.getWorldGuardUtils().getRegionsAt(location.clone().add(0,.1,0))) {
			if (!arena.ownsRegion(region.getId(), "floor")) continue;

			Material type = location.getBlock().getType();
			if (!type.equals(Material.TNT) && !arena.canUseBlock(type))
				return false;

			playBlockBreakSound(location);
			location.getBlock().setType(Material.AIR);
			return true;
		}
		return false;
	}

	@EventHandler
	public void onBlockExplode(EntityExplodeEvent event) {
		if (!event.getEntityType().equals(EntityType.PRIMED_TNT)) return;

		boolean found = false;
		Arena arena = null;
		Match match = null;

		for (ProtectedRegion region : Minigames.getWorldGuardUtils().getRegionsAt(event.getLocation())) {
			arena = ArenaManager.getFromRegion(region.getId());
			if (arena == null) continue;

			match = MatchManager.get(arena);
			if (!match.isMechanic(this)) continue;
			if (!match.isStarted()) continue;

			found = true;
		}

		if (!found || arena == null || match == null) return;

		List<Block> toKeep = new ArrayList<>(event.blockList());

		for (Block block : event.blockList()) {
			Location location = block.getLocation();
			for (ProtectedRegion region : Minigames.getWorldGuardUtils().getRegionsAt(event.getLocation())) {
				if (!arena.ownsRegion(region.getId(), "floor")) continue;

				Material type = location.getBlock().getType();
				if (!type.equals(Material.TNT) && !arena.canUseBlock(type)) continue;

				toKeep.remove(block);
			}
		}

		event.blockList().removeAll(toKeep);

		List<Block> toDelete = new ArrayList<>(event.blockList());

		event.blockList().clear();

		toDelete.forEach(block -> block.setType(Material.AIR));
	}

	public abstract void playBlockBreakSound(Location location);

}
