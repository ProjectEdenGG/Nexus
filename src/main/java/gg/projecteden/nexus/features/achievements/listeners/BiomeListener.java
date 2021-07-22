package gg.projecteden.nexus.features.achievements.listeners;

import gg.projecteden.nexus.models.achievement.Achievement;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BiomeListener implements Listener {

	static {
		Tasks.repeat(100, 100, BiomeListener::check);
	}

	public static void check() {
		for (Player player : PlayerUtils.getOnlinePlayers()) {
			checkBiome(player);
			checkWorld(player);
		}
	}

	public static void checkBiome(Player player) {
		Location loc = player.getLocation();
		if (!Arrays.asList("world", "survival", "resource").contains(loc.getWorld().getName().split("_")[0])) return;
		if (new WorldGuardUtils(player).getRegionNamesAt(loc).contains("spawn")) return;

		Biome biome = player.getWorld().getBiome(loc.getBlockX(), loc.getBlockZ());
		String name = biome.toString();

		Map<String, Achievement> biomes = new HashMap<>();
		biomes.put("REDWOOD_TAIGA", Achievement.HOTEL_CALIFORNIA);
		biomes.put("MUTATED_FOREST", Achievement.WHERE_THE_WILD_ROSES_GROW);
		biomes.put("ROOFED_FOREST", Achievement.FIDDLER_ON_THE_ROOF);
		biomes.put("BIRCH_FOREST", Achievement.BLACK_HORSE_AND_THE_BIRCH_TREE);
		biomes.put("FOREST", Achievement.FORREST_GUMP);
		biomes.put("MUTATED_ICE_FLATS", Achievement.HEART_OF_ICE);
		biomes.put("ICE_FLATS", Achievement.ICE_ICE_BABY);
		biomes.put("DESERT", Achievement.A_HORSE_WITH_NO_NAME);
		biomes.put("EXTREME_HILLS", Achievement.THE_MISTY_MOUNTAINS_COLD);
		biomes.put("JUNGLE", Achievement.WELCOME_TO_THE_JUNGLE);
		biomes.put("MESA", Achievement.THE_PAINTED_DESERT);
		biomes.put("MUSHROOM_ISLAND", Achievement.INFECTED_MUSHROOM);
		biomes.put("OCEAN", Achievement.SAILING_FOR_ADVENTURE);
		biomes.put("PLAINS", Achievement.THE_RAIN_IN_SPAIN_STAYS_MAINLY_ON_THE_PLAIN);
		biomes.put("RIVER", Achievement.CRY_ME_A_RIVER);
		biomes.put("SAVANNA", Achievement.SUNDAY_IN_SAVANNAH);
		biomes.put("SWAMPLAND", Achievement.BORN_ON_THE_BAYOU);
		biomes.put("TAIGA", Achievement.TAIGA_TAIGA_BURNING_BRIGHT);

		for (Map.Entry<String, Achievement> map : biomes.entrySet()) {
			if (name.contains(map.getKey())) {
				map.getValue().check(player);
				Achievement.THE_WANDERER.check(player, map.getKey());
			}
		}
	}

	public static void checkWorld(Player player) {
		String world = player.getWorld().getName().toLowerCase();
		if (world.equals("world_nether")) {
			Achievement.HIGHWAY_TO_HELL.check(player);
			Achievement.THE_WANDERER.check(player, "NETHER");
		} else if (world.equals("world_the_end")) {
			Achievement.THE_END_OF_THE_WORLD_AS_WE_KNOW_IT.check(player);
			Achievement.THE_WANDERER.check(player, "END");
		}
	}
}
