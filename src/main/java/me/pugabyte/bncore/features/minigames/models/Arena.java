package me.pugabyte.bncore.features.minigames.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.mechanics.MechanicType;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Data
@Builder
@SerializableAs("Arena")
public class Arena implements ConfigurationSerializable {
	@NonNull
	private int id;
	@NonNull
	private String name;
	@NonNull
	private String displayName;
	@NonNull
	private MechanicType mechanicType;
	@NonNull
	private List<Team> teams;
	@NonNull
	private Lobby lobby;
	private Location respawnLocation;
	private int seconds;
	private int minPlayers;
	private int maxPlayers;
	private int winningScore;
	private int minWinningScore;
	private int maxWinningScore;
	// TODO: private Set<Material> blockList;
	private Location eliminationTeleportPosition;
	@Accessors(fluent = true)
	private boolean canJoinLate;

	public Mechanic getMechanic() {
		return getMechanicType().get();
	}

	private static String getFile(String name) {
		return Minigames.getArenasFolder() + name + ".yml";
	}

	private static FileConfiguration getConfig(String name) {
		return YamlConfiguration.loadConfiguration(new File(getFile(name)));
	}

	public static void read() {
		try (Stream<Path> paths = Files.walk(Paths.get(Minigames.getArenasFolder()))) {
			paths.forEach(filePath -> {
				if (!Files.isRegularFile(filePath)) return;

				String name = filePath.getFileName().toString().replace(".yml", "");
				read(name);
			});
		} catch (IOException ex) {
			BNCore.severe("An error occurred while trying to read arena configuration files: " + ex.getMessage());
		}
	}

	public static void read(String name) {
		ArenaManager.add((Arena) getConfig(name).get("arena"));
	}

	public static void write() {
		ArenaManager.getAll().forEach(arena -> write(arena.getName()));
	}

	public static void write(String name) {
		Optional<Arena> optionalArena = ArenaManager.get(name);
		if (!optionalArena.isPresent()) return;

		FileConfiguration arenaConfig = getConfig(name);
		arenaConfig.set("arena", optionalArena.get());

		try {
			arenaConfig.save(getFile(name));
		} catch (IOException ex) {
			BNCore.severe("An error occurred while trying to write arena configuration files: " + ex.getMessage());
		}
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("id", getId());
		map.put("name", getName());
		map.put("displayName", getDisplayName());
		map.put("mechanicType", getMechanicType().name());
		map.put("seconds", getSeconds());
		map.put("minPlayers", getMinPlayers());
		map.put("maxPlayers", getMaxPlayers());
		map.put("winningScore", getWinningScore());
		map.put("minWinningScore", getMinWinningScore());
		map.put("maxWinningScore", getMaxWinningScore());
		map.put("canJoinLate", canJoinLate());
		map.put("respawnLocation", getRespawnLocation());
		map.put("lobby", getLobby());
		map.put("teams", getTeams());

		return map;
	}

	public static Arena deserialize(Map<String, Object> map) {
		return Arena.builder()
				.id((int) map.get("id"))
				.name((String) map.get("name"))
				.displayName((String) map.get("displayName"))
				.teams((List<Team>) map.get("teams"))
				.mechanicType(MechanicType.valueOf(((String) map.get("mechanicType")).toUpperCase()))
				.lobby((Lobby) map.get("lobby"))
				.respawnLocation((Location) map.get("respawnLocation"))
				.seconds((int) map.getOrDefault("seconds", 600))
				.minPlayers((int) map.getOrDefault("minPlayers", 2))
				.maxPlayers((int) map.getOrDefault("maxPlayers", 10))
				.winningScore((int) map.getOrDefault("winningScore", 10))
				.minWinningScore((int) map.getOrDefault("minWinningScore", 0))
				.maxWinningScore((int) map.getOrDefault("maxWinningScore", 0))
				.canJoinLate((boolean) map.getOrDefault("canJoinLate", false))
				.build();
	}

}
