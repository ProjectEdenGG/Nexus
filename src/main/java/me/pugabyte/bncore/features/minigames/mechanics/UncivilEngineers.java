package me.pugabyte.bncore.features.minigames.mechanics;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.UncivilEngineersArena;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bncore.features.minigames.models.matchdata.UncivilEngineersMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UncivilEngineers extends TeamlessMechanic {
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
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public boolean shouldClearInventory() {
		return false;
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
		separatePlayers(event.getMatch());
	}

	public void separatePlayers(Match match) {
		UncivilEngineersArena arena = match.getArena();
		UncivilEngineersMatchData matchData = match.getMatchData();
		Location spawnpoint = arena.getTeams().get(0).getSpawnpoints().get(0);
		for (int i = 0; i < match.getMinigamers().size(); i++) {
			matchData.getPlayerStrips().put(match.getMinigamers().get(i).getPlayer().getUniqueId(), i + 1);
			match.getMinigamers().get(i).teleport(getLocationOffFirst(match.getMinigamers().get(i), spawnpoint));
		}
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		((UncivilEngineersMatchData) event.getMatch().getMatchData()).getEntities().forEach(Entity::remove);
		resetStrips(event.getMatch());
		super.onEnd(event);
	}

	private void resetStrips(Match match) {
		String name = getName().replace(" ", "");
		WGUtils.getRegionsLike(name + "_" + match.getArena().getName() + "_strip_[0-9]+")
				.forEach(region -> {
					String file = (name + "/" + match.getArena().getName() + "_strip").toLowerCase();
					WEUtils.paste(file, region.getMinimumPoint());
				});
	}

	public int getStrip(Minigamer minigamer) {
		UncivilEngineersMatchData matchData = minigamer.getMatch().getMatchData();
		return matchData.getPlayerStrips().get(minigamer.getPlayer().getUniqueId());
	}

	public Location getLocationOffFirst(Minigamer minigamer, Location location) {
		UncivilEngineersArena arena = minigamer.getMatch().getArena();
		int x, y, z;
		x = (int) (location.getX() - arena.getOrigins().get(1).getX());
		y = (int) (location.getY() - arena.getOrigins().get(1).getY());
		z = (int) (location.getZ() - arena.getOrigins().get(1).getZ());
		Vector vector = new Vector(x, y, z);
		Location origin = arena.getOrigins().get(getStrip(minigamer));
		return origin.toVector().add(vector).toLocation(location.getWorld());
	}

	public static Location getLocationOffFirst(UncivilEngineersArena arena, int originID, com.sk89q.worldedit.Vector location) {
		int x, y, z;
		x = (int) (location.getX() - arena.getOrigins().get(1).getX());
		y = (int) (location.getY() - arena.getOrigins().get(1).getY());
		z = (int) (location.getZ() - arena.getOrigins().get(1).getZ());
		Vector vector = new Vector(x, y, z);
		Location origin = arena.getOrigins().get(originID);
		return origin.toVector().add(vector).toLocation(Minigames.getGameworld());
	}

	@EventHandler
	public void onEnterWinRegion(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		if (!minigamer.getMatch().getArena().ownsRegion(event.getRegion().getId(), "win")) return;
		minigamer.scored();
		minigamer.getMatch().end();
	}

	@EventHandler
	public void onEnterCheckpointRegion(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		UncivilEngineersArena arena = minigamer.getMatch().getArena();
		UncivilEngineersMatchData matchData = minigamer.getMatch().getMatchData();

		if (arena.ownsRegion(event.getRegion().getId(), "checkpoint")) {
			int checkpointId = arena.getRegionTypeId(event.getRegion());
			matchData.setCheckpoint(minigamer, checkpointId);
		}
	}

	public void toCheckpoint(Minigamer minigamer) {
		UncivilEngineersArena arena = minigamer.getMatch().getArena();
		UncivilEngineersMatchData matchData = minigamer.getMatch().getMatchData();
		minigamer.clearState();
		if (!matchData.getCheckpoints().containsKey(minigamer.getPlayer().getUniqueId())) {
			Location spawnpoint = arena.getTeams().get(0).getSpawnpoints().get(0);
			minigamer.teleport(getLocationOffFirst(minigamer, spawnpoint));
			return;
		}
		minigamer.teleport(getLocationOffFirst(minigamer, arena.getCheckpoint(matchData.getCheckpointId(minigamer))));
	}

	@EventHandler
	public void onEnterVoid(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		if (!minigamer.getMatch().getArena().ownsRegion(event.getRegion().getId(), "void")) return;
		toCheckpoint(minigamer);
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		event.broadcastDeathMessage();
		toCheckpoint(event.getMinigamer());
	}

	@EventHandler
	public void onEnterMobRegion(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		UncivilEngineersMatchData matchData = minigamer.getMatch().getMatchData();
		EntityType type;
		try {
			type = EntityType.valueOf(getMobFromRegion(minigamer.getMatch(), event.getRegion().getId()));
		} catch (Exception ignore) {
			return;
		}
		if (matchData.getPlayerEntities().containsKey(minigamer.getPlayer().getUniqueId())) {
			if (matchData.getPlayerEntities().get(minigamer.getPlayer().getUniqueId()).contains(type)) return;
		}
		spawnEntities(minigamer, type);
	}

	private void spawnEntities(Minigamer minigamer, EntityType type) {
		Match match = minigamer.getMatch();
		UncivilEngineersArena arena = match.getArena();
		UncivilEngineersMatchData matchData = match.getMatchData();
		for (MobPoint point : arena.getMobPoints()) {
			if (point.getType() == type) {
				Location spawnLoc = getLocationOffFirst(minigamer, point.getLocation());
				spawnLoc.getWorld().spawn(spawnLoc, type.getEntityClass());
				List<EntityType> entities = new ArrayList<>();
				if (matchData.getPlayerEntities().containsKey(minigamer.getPlayer().getUniqueId())) {
					entities = matchData.getPlayerEntities().get(minigamer.getPlayer().getUniqueId());
				}
				entities.add(type);
				matchData.getPlayerEntities().put(minigamer.getPlayer().getUniqueId(), entities);
			}
		}
	}

	public String getMobFromRegion(Match match, String region) {
		region = region.replace(match.getArena().getRegionBaseName() + "_", "");
		return region.toUpperCase();
	}

	public static void setupArena(UncivilEngineersArena arena, Player player) {
		WorldGuardUtils WGUtils = Minigames.getWorldGuardUtils();
		try {
			Minigames.getWorldEditUtils().save("uncivilengineers/" + arena.getName() + "_strip", arena.getRegion("strip_1"));
			RegionManager regionManager = WGUtils.getManager();
			Region region1 = arena.getRegion("strip_1");
			for (int originID : arena.getOrigins().keySet()) {
				Location min = getLocationOffFirst(arena, originID, region1.getMinimumPoint());
				Location max = getLocationOffFirst(arena, originID, region1.getMaximumPoint());
				String regionName = arena.getRegionBaseName() + "_strip_" + originID;
				ProtectedRegion region2 = new ProtectedCuboidRegion(regionName, WGUtils.toVector(min).toBlockVector(), WGUtils.toVector(max).toBlockVector());
				regionManager.addRegion(region2);
			}
			regionManager.save();
		} catch (NullPointerException ex) {
			player.sendMessage(Minigames.PREFIX + "&cYou must setup the region: " + arena.getRegionBaseName() + "_strip_1");
		} catch (StorageException e) {
			e.printStackTrace();
		}
		player.sendMessage(Minigames.PREFIX + "Successfully setup the arena");
	}

	@Data
	@SerializableAs("MobPoint")
	public static class MobPoint implements ConfigurationSerializable {

		public EntityType type;
		public Location location;

		public MobPoint(Location location, EntityType type) {
			this.location = location;
			this.type = type;
		}

		public MobPoint(Map<String, Object> map) {
			this.location = (Location) map.getOrDefault("location", location);
			this.type = EntityType.valueOf((String) map.getOrDefault("type", type));
		}

		@Override
		public Map<String, Object> serialize() {
			return new LinkedHashMap<String, Object>() {{
				put("type", type.name());
				put("location", location);
			}};
		}
	}

}
