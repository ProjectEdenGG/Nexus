package me.pugabyte.bncore.features.minigames.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.mechanics.Mechanics;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class Arena {
	@NonNull
	private int id;
	@NonNull
	private String name;
	@NonNull
	private String displayName;
	@NonNull
	private List<Team> teams;
	@NonNull
	private Mechanic mechanic;
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

	public static class Reader {
		private ConfigurationSection config;

		public Reader(String arena) {
			File file = new File("plugins/BNCore/minigames/arenas/" + arena + ".yml");
			if (file.exists()) {
				config = YamlConfiguration.loadConfiguration(file).getConfigurationSection("arena");
				read();
			} else {
				BNCore.warn("Configuration file for arena " + arena + " not found!");
			}
		}

		void read() {
			Arena arena = Arena.builder()
					.id(id())
					.name(name())
					.displayName(displayName())
					.teams(teams())
					.mechanic(mechanic())
					.lobby(lobby())
					.respawnLocation(respawnLocation())
					.seconds(seconds())
					.minPlayers(minPlayers())
					.maxPlayers(maxPlayers())
					.winningScore(winningScore())
					.minWinningScore(minWinningScore())
					.maxWinningScore(maxWinningScore())
					.canJoinLate(canJoinLate())
					.build();
			ArenaManager.add(arena);
			BNCore.log("Created arena " + arena.getName());
		}

		private int id() {
			return config.getInt("id");
		}

		private String name() {
			return config.getString("name");
		}

		private String displayName() {
			return config.getString("displayname");
		}

		private List<Team> teams() {
			List<Team> teams = new ArrayList<>();
			ConfigurationSection teamsConfig = this.config.getConfigurationSection("teams");
			for (String teamKey : teamsConfig.getKeys(false)) {
				ConfigurationSection teamConfig = teamsConfig.getConfigurationSection(teamKey);
				ConfigurationSection loadoutConfig = teamConfig.getConfigurationSection("loadout");

				ConfigurationSection inventoryConfig = loadoutConfig.getConfigurationSection("inventory");
				List<Map<?, ?>> effectMapList = loadoutConfig.getMapList("effects");

				List<Map<?, ?>> spawnpointMapList = teamConfig.getMapList("spawnpoints");

				ItemStack[] inventory = new ItemStack[41];
				for (String loadoutKey : inventoryConfig.getKeys(false)) {
					inventory[Integer.parseInt(loadoutKey)] = inventoryConfig.getItemStack(loadoutKey);
				}

				List<PotionEffect> effects = new ArrayList<>();
				for (Map<?, ?> effect : effectMapList) {
					PotionEffectType type = PotionEffectType.getByName((String) effect.get("type"));
					int duration = (Integer) effect.get("duration") * 20;
					int amplifier = (Integer) effect.get("amplifier") - 1;
					effects.add(new PotionEffect(type, duration, amplifier));
				}

				List<Location> spawnpoints = new ArrayList<>();
				for (Map<?, ?> effect : spawnpointMapList) {
					World world = Bukkit.getWorld((String) effect.get("world"));
					double x = (Double) effect.get("x");
					double y = (Double) effect.get("y");
					double z = (Double) effect.get("z");
					float yaw = ((Double) effect.get("yaw")).floatValue();
					float pitch = ((Double) effect.get("pitch")).floatValue();
					spawnpoints.add(new Location(world, x, y, z, yaw, pitch));
				}

				Team team = Team.builder()
						.name(teamConfig.getString("name"))
						.color(ChatColor.valueOf(teamConfig.getString("color").toUpperCase()))
						.objective(teamConfig.getString("objective"))
						.loadout(new Loadout(inventory, effects))
						.spawnpoints(spawnpoints)
						.build();

				teams.add(team);
			}

			return teams;
		}

		private Mechanic mechanic() {
			return Mechanics.valueOf(config.getString("mechanic").toUpperCase()).get();
		}

		private Lobby lobby() {
			ConfigurationSection config = this.config.getConfigurationSection("lobby");
			Location location = getLocation(config.getConfigurationSection("location"));
			int waitTime = config.getInt("waittime");
			return new Lobby(location, waitTime);
		}

		private Location respawnLocation() {
			return getLocation(config.getConfigurationSection("respawnlocation"));
		}

		private int seconds() {
			return config.getInt("seconds");
		}

		private int minPlayers() {
			return config.getInt("minplayers");
		}

		private int maxPlayers() {
			return config.getInt("maxplayers");
		}

		private int winningScore() {
			return config.getInt("winningscore");
		}

		private int maxWinningScore() {
			return config.getInt("maxwinningscore");
		}

		private int minWinningScore() {
			return config.getInt("minwinningscore");
		}

		private boolean canJoinLate() {
			return config.getBoolean("canjoinlate");
		}

		private Location getLocation(ConfigurationSection config) {
			World world = Bukkit.getWorld(config.getString("world"));
			double x = config.getDouble("x");
			double y = config.getDouble("y");
			double z = config.getDouble("z");
			float yaw = ((Double) config.getDouble("yaw")).floatValue();
			float pitch = ((Double) config.getDouble("pitch")).floatValue();
			return new Location(world, x, y, z, yaw, pitch);
		}

	}
}
