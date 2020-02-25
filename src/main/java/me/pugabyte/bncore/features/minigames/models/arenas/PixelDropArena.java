package me.pugabyte.bncore.features.minigames.models.arenas;

import com.sk89q.worldedit.regions.Region;
import lombok.Data;
import me.pugabyte.bncore.features.minigames.models.Arena;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@Data
@SerializableAs("PixelDropArena")
public class PixelDropArena extends Arena {
	private Region designRegion = getRegion("designs");
	private Region dropRegion = getRegion("dropzone");
	private Region boardRegion = getRegion("board");
	private Region lobbyAnimationRegion = getRegion("lobbyanimation");

	public PixelDropArena(Map<String, Object> map) {
		super(map);
	}

	@Override
	public String getRegionBaseName() {
		return "pixeldrop";
	}
}
