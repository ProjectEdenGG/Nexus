package me.pugabyte.bncore.features.minigames.models.arenas;

import com.sk89q.worldedit.regions.Region;
import lombok.Data;
import me.pugabyte.bncore.features.minigames.models.Arena;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@SerializableAs("PixelPaintersArena")
public class PixelPaintersArena extends Arena {
	private Region designRegion = getRegion("designs");
	private Region nextDesignRegion = getRegion("nextdesign");
	private Region lobbyDesignRegion = getRegion("lobbynextdesign");
	private Region logoRegion = getRegion("logo");
	private Region lobbyAnimationRegion = getRegion("lobbyanimation");

	public PixelPaintersArena(Map<String, Object> map) {
		super(map);
	}

	@Override
	public String getRegionBaseName() {
		return "pixelpainters";
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		return map;
	}

}
