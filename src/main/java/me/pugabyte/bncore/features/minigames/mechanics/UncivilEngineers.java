package me.pugabyte.bncore.features.minigames.mechanics;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.mechanics.common.CheckpointMechanic;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.Regenerating;
import me.pugabyte.bncore.features.minigames.models.arenas.UncivilEngineersArena;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.matchdata.UncivilEngineersMatchData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@Regenerating("strip")
public class UncivilEngineers extends CheckpointMechanic {
	@Override
	public String getName() {
		return "Uncivil Engineers";
	}

	@Override
	public String getDescription() {
		return "Race to the finish";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.WOOD_PICKAXE);
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
	}

	@EventHandler
	public void onEnterMobRegion(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		try {
			EntityType mob = EntityType.valueOf(getMobFromRegion(minigamer.getMatch(),
					event.getRegion().getId()).toUpperCase());
		} catch (Exception ignore) {
			return;
		}
		spawnEntities(minigamer.getMatch(), event.getRegion());
	}

	public Vector getLocationOffFirst(Match match, Location location) {
		UncivilEngineersArena arena = match.getArena();
		int x, y, z;
		x = (int) (location.getX() - arena.getOrigins().get(1).getX());
		y = (int) (location.getY() - arena.getOrigins().get(1).getY());
		z = (int) (location.getZ() - arena.getOrigins().get(1).getZ());
		return new Vector(x, y, z);
	}

	private void spawnEntities(Match match, ProtectedRegion region) {
		UncivilEngineersArena arena = match.getArena();
		for (MobPoint point : arena.getMobPoints()) {
			if (point.getType() == EntityType.valueOf(getMobFromRegion(match, region.getId()).toUpperCase())) {
				//Spawning based off first region locations
				int regionID = Integer.parseInt(region.getId().substring(region.getId().lastIndexOf("_") + 1));
				point.getLocation().getWorld().spawn(
						arena.getOrigins().get(regionID).toVector().add(getLocationOffFirst(match,
								point.getLocation())).toLocation(point.getLocation().getWorld()),
						point.getType().getEntityClass());
			}
		}
	}

	public String getMobFromRegion(Match match, String region) {
		region = region.replace(match.getArena().getRegionBaseName() + "_", "");
		return region.substring(0, region.lastIndexOf("_") - 1);
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		((UncivilEngineersMatchData) event.getMatch().getMatchData()).getEntities().forEach((entity, uuid) -> {
			entity.remove();
		});
		fixLines();
		super.onEnd(event);
	}

	private void fixLines() {

	}

	@Data
	public static class MobPoint {

		public EntityType type;
		public Location location;

		public MobPoint(Location location, EntityType type) {
			this.location = location;
			this.type = type;
		}
	}

}
