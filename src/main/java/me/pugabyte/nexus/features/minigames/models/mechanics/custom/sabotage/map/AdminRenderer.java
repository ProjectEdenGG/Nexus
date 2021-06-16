package me.pugabyte.nexus.features.minigames.models.mechanics.custom.sabotage.map;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.arenas.SabotageArena;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AdminRenderer extends SabotageMapRenderer {
	private final Set<ProtectedRegion> regions;
	private Map<ProtectedRegion, Integer> headCount;

	public AdminRenderer(SabotageArena arena) {
		super(arena);
		regions = arena.getRegionsLike("room_\\w+");
	}

	@Override
	public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
		Minigamer minigamer = PlayerManager.get(player);
		Match match = minigamer.getMatch();
		Map<ProtectedRegion, Integer> headCount = new HashMap<>();
		match.getAlivePlayers().forEach(player1 -> regions.stream()
				.filter(protectedRegion -> protectedRegion.contains(WorldGuardUtils.toBlockVector3(player1.getLocation())))
				.findAny().ifPresent(room -> headCount.compute(room, ($, integer) -> integer == null ? 1 : integer + 1)));
		match.getTasks().async(() -> {
			synchronized (cacheLock) {
				if (cached && headCount.equals(this.headCount))
					loadCache(canvas);
				else {
					// TODO render heads
					saveCache(canvas);
				}
			}
		});
	}
}
