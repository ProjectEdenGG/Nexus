package me.pugabyte.nexus.features.minigames.mechanics;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.arenas.UncivilEngineersArena;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.nexus.features.minigames.models.matchdata.UncivilEngineersMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.WorldEditUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UncivilEngineers extends TeamlessMechanic {
	@Override
	public @NotNull String getName() {
		return "Uncivil Engineers";
	}

	@Override
	public String getDescription() {
		return "Race to the finish";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.WOODEN_PICKAXE);
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public boolean canDropItem(ItemStack item) {
		return true;
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
		match.getWGUtils().getRegionsLike(name + "_" + match.getArena().getName() + "_strip_[0-9]+")
				.forEach(region -> {
					String file = (name + "/" + match.getArena().getName() + "_strip").toLowerCase();
					match.getWEUtils().paster().file(file).at(region.getMinimumPoint()).paste();
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

	public static Location getLocationOffFirst(UncivilEngineersArena arena, int originID, BlockVector3 location) {
		int x, y, z;
		x = (int) (location.getX() - arena.getOrigins().get(1).getX());
		y = (int) (location.getY() - arena.getOrigins().get(1).getY());
		z = (int) (location.getZ() - arena.getOrigins().get(1).getZ());
		Vector vector = new Vector(x, y, z);
		Location origin = arena.getOrigins().get(originID);
		return origin.toVector().add(vector).toLocation(Minigames.getWorld());
	}

	@EventHandler
	public void onEnterWinRegion(PlayerEnteredRegionEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		if (!minigamer.getMatch().getArena().ownsRegion(event.getRegion().getId(), "win")) return;
		minigamer.scored();
		minigamer.getMatch().end();
	}

	@EventHandler
	public void onEnterCheckpointRegion(PlayerEnteredRegionEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		UncivilEngineersArena arena = minigamer.getMatch().getArena();
		UncivilEngineersMatchData matchData = minigamer.getMatch().getMatchData();

		if (arena.ownsRegion(event.getRegion().getId(), "checkpoint")) {
			int checkpointId = Arena.getRegionNumber(event.getRegion());
			matchData.setCheckpoint(minigamer, checkpointId);
		}
	}

	public void toCheckpoint(Minigamer minigamer) {
		UncivilEngineersArena arena = minigamer.getMatch().getArena();
		UncivilEngineersMatchData matchData = minigamer.getMatch().getMatchData();
		clearGameModeState(minigamer);
		if (!matchData.getCheckpoints().containsKey(minigamer.getPlayer().getUniqueId())) {
			Location spawnpoint = arena.getTeams().get(0).getSpawnpoints().get(0);
			minigamer.teleport(getLocationOffFirst(minigamer, spawnpoint));
			return;
		}
		minigamer.teleport(getLocationOffFirst(minigamer, arena.getCheckpoint(matchData.getCheckpointId(minigamer))));
	}

	//Custom clearState due to inventory wipe
	private void clearGameModeState(Minigamer minigamer) {
		Player player = minigamer.getPlayer();
		player.setFireTicks(0);
		player.resetMaxHealth();
		player.setHealth(20);
		player.setExp(0);
		player.setTotalExperience(0);
		player.setLevel(0);

		for (PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
	}

	@EventHandler
	public void onEnterVoid(PlayerEnteredRegionEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		if (!minigamer.getMatch().getArena().ownsRegion(event.getRegion().getId(), "void")) return;
		toCheckpoint(minigamer);
	}

	@EventHandler
	public void onCustomDeath(PlayerDeathEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getEntity());
		if (!minigamer.isPlaying(this)) return;

		event.setKeepInventory(true);
		event.getDrops().clear();
		toCheckpoint(minigamer);

		event.setDeathMessage(null);
		minigamer.getMatch().broadcast(minigamer.getColoredName() + " &3died");
	}

	@EventHandler
	public void onEnterMobRegion(PlayerEnteredRegionEvent event) {
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
				match.spawn(spawnLoc, type.getEntityClass());
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
		WorldGuardUtils WGUtils = arena.getWGUtils();
		WorldEditUtils WEUtils = arena.getWEUtils();
		try {
//			TODO when API saving works again
//			WEUtils.save("uncivilengineers/" + arena.getName() + "_strip", arena.getRegion("strip_1"));
			WEUtils.setSelection(player, arena.getRegion("strip_1"));
			PlayerUtils.runCommand(player, "nexus schem save " + arena.getName() + "_strip");
			RegionManager regionManager = WGUtils.getManager();
			Region region1 = arena.getRegion("strip_1");
			for (int originID : arena.getOrigins().keySet()) {
				Location min = getLocationOffFirst(arena, originID, region1.getMinimumPoint());
				Location max = getLocationOffFirst(arena, originID, region1.getMaximumPoint());
				String regionName = arena.getRegionBaseName() + "_strip_" + originID;
				ProtectedRegion region2 = new ProtectedCuboidRegion(regionName, WGUtils.toBlockVector3(min), WGUtils.toBlockVector3(max));
				regionManager.addRegion(region2);
			}
			regionManager.save();
		} catch (NullPointerException ex) {
			PlayerUtils.send(player, Minigames.PREFIX + "&cYou must setup the region: " + arena.getRegionBaseName() + "_strip_1");
		} catch (StorageException e) {
			e.printStackTrace();
		}
		PlayerUtils.send(player, Minigames.PREFIX + "Successfully setup the arena");
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
