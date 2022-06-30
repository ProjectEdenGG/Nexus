package gg.projecteden.nexus.features.achievements.listeners;

import gg.projecteden.nexus.models.achievement.Achievement;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class BiomeListener implements Listener {

	static {
		Tasks.repeat(100, 100, BiomeListener::check);
	}

	public static void check() {
		for (Player player : OnlinePlayers.getAll()) {
			checkBiome(player);
			checkWorld(player);
		}
	}

	private static final Map<String, Achievement> BIOMES = new HashMap<>() {{
		put("REDWOOD_TAIGA", Achievement.HOTEL_CALIFORNIA);
		put("MUTATED_FOREST", Achievement.WHERE_THE_WILD_ROSES_GROW);
		put("ROOFED_FOREST", Achievement.FIDDLER_ON_THE_ROOF);
		put("BIRCH_FOREST", Achievement.BLACK_HORSE_AND_THE_BIRCH_TREE);
		put("FOREST", Achievement.FORREST_GUMP);
		put("MUTATED_ICE_FLATS", Achievement.HEART_OF_ICE);
		put("ICE_FLATS", Achievement.ICE_ICE_BABY);
		put("DESERT", Achievement.A_HORSE_WITH_NO_NAME);
		put("EXTREME_HILLS", Achievement.THE_MISTY_MOUNTAINS_COLD);
		put("JUNGLE", Achievement.WELCOME_TO_THE_JUNGLE);
		put("MESA", Achievement.THE_PAINTED_DESERT);
		put("MUSHROOM_ISLAND", Achievement.INFECTED_MUSHROOM);
		put("OCEAN", Achievement.SAILING_FOR_ADVENTURE);
		put("PLAINS", Achievement.THE_RAIN_IN_SPAIN_STAYS_MAINLY_ON_THE_PLAIN);
		put("RIVER", Achievement.CRY_ME_A_RIVER);
		put("SAVANNA", Achievement.SUNDAY_IN_SAVANNAH);
		put("SWAMPLAND", Achievement.BORN_ON_THE_BAYOU);
		put("TAIGA", Achievement.TAIGA_TAIGA_BURNING_BRIGHT);
	}};

	public static void checkBiome(Player player) {
		Location loc = player.getLocation();
		if (WorldGroup.of(player) != WorldGroup.SURVIVAL)
			return;
		if (new WorldGuardUtils(player).getRegionNamesAt(loc).contains("spawn"))
			return;

		Biome biome = player.getWorld().getBiome(loc.getBlockX(), loc.getBlockZ());
		String name = biome.toString();

		for (Map.Entry<String, Achievement> map : BIOMES.entrySet()) {
			if (name.contains(map.getKey())) {
				map.getValue().check(player);
				Achievement.THE_WANDERER.check(player, map.getKey());
			}
		}
	}

	public static void checkWorld(Player player) {
		if (WorldGroup.of(player) != WorldGroup.SURVIVAL)
			return;

		World world = player.getWorld();
		switch (world.getEnvironment()) {
			case NETHER -> {
				Achievement.HIGHWAY_TO_HELL.check(player);
				Achievement.THE_WANDERER.check(player, "NETHER");
			}
			case THE_END -> {
				Achievement.THE_END_OF_THE_WORLD_AS_WE_KNOW_IT.check(player);
				Achievement.THE_WANDERER.check(player, "END");
			}
		}
	}
}
