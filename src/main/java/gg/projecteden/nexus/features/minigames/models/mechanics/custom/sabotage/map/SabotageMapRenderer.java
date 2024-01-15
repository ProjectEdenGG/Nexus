package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.map;

import gg.projecteden.nexus.features.minigames.models.arenas.SabotageArena;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

public class SabotageMapRenderer extends MapRenderer {
	protected static final Color BLACK = Color.BLACK;
	protected final Color[][] CACHE = new Color[128][128];
	protected boolean cached = false;
	protected final Object cacheLock = new Object();
	protected final SabotageArena arena;

	public SabotageMapRenderer(SabotageArena arena) {
		this.arena = arena;
	}

	@Override
	public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
		Tasks.async(() -> {
			synchronized (cacheLock) {
				if (!cached) {
					for (int x = 0; x < 128; x++)
						for (int y = 0; y < 128; y++)
							canvas.setPixelColor(x, y, BLACK);

					// TOOO; canvas.drawImage(0, 0, );

					saveCache(canvas);
					cached = true;
				} else
					loadCache(canvas);
			}
		});
	}

	protected void loadCache(MapCanvas canvas) {
		for (int x = 0; x < 128; x++)
			for (int y = 0; y < 128; y++)
				canvas.setPixelColor(x, y, CACHE[x][y]);
	}

	protected void saveCache(MapCanvas canvas) {
		for (int x = 0; x < 128; x++)
			for (int y = 0; y < 128; y++)
				CACHE[x][y] = canvas.getPixelColor(x, y);
	}
}
