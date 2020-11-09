package me.pugabyte.bncore.features.minigames.mechanics.common;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.annotations.AntiCamp;
import me.pugabyte.bncore.features.minigames.models.annotations.Regenerating;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

@AntiCamp
@Regenerating("floor")
public abstract class SpleefMechanic extends TeamlessMechanic {

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
		new AntiCampingTask(event.getMatch()).start();
	}

	public boolean breakBlock(Match match, Location location) {
		for (ProtectedRegion region : match.getWGUtils().getRegionsAt(location.clone().add(0, .1, 0))) {
			if (!match.getArena().ownsRegion(region.getId(), "floor")) continue;

			Material type = location.getBlock().getType();
			if (!type.equals(Material.TNT) && !match.getArena().canUseBlock(type))
				return false;

			boolean spawnTnt = type == Material.TNT;

			playBlockBreakSound(location);
			location.getBlock().setType(Material.AIR);

			if (spawnTnt) spawnTnt(location);

			return true;
		}
		return false;
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if (!event.getEntityType().equals(EntityType.PRIMED_TNT)) return;

		Match match = MatchManager.getActiveMatchFromLocation(this, event.getLocation());
		if (match == null) return;

		event.blockList().forEach(block -> breakBlock(match, block.getLocation()));
		event.blockList().clear();
	}

	public void spawnTnt(Location location) {
		Location spawnLocation = location.add(0.5, 0, 0.5);
		TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(spawnLocation, EntityType.PRIMED_TNT);
		tnt.setYield(3);
		tnt.setFuseTicks(0);
	}

	public abstract void playBlockBreakSound(Location location);

}
