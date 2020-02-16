package me.pugabyte.bncore.features.minigames.models.arenas;

import com.sk89q.worldedit.regions.Region;
import lombok.Data;
import me.pugabyte.bncore.features.minigames.models.Arena;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@Data
@SerializableAs("PixelPaintersArena")
public class PixelPaintersArena extends Arena {
	private Region DESIGNS_REGION = getRegion(getRegionBaseName() + "_designs");
	private Region NEXT_DESIGN_REGION = getRegion(getRegionBaseName() + "_nextdesign");
	private Region LOBBY_DESIGN_REGION = getRegion(getRegionBaseName() + "_lobbynextdesign");
	private Region LOGO_REGION = getRegion(getRegionBaseName() + "_logo");
	private Region LOBBY_ANIMATION_REGION = getRegion(getRegionBaseName() + "lobbyanimation");

	@Override
	public String getRegionBaseName() {
		return "pixelpainters";
	}

	public PixelPaintersArena(Map<String, Object> map) {
		super(map);
	}
}
