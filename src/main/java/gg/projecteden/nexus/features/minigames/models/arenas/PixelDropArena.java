package gg.projecteden.nexus.features.minigames.models.arenas;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.minigames.models.Arena;
import lombok.Data;
import lombok.ToString;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Data
@ToString(callSuper = true)
@SerializableAs("PixelDropArena")
public class PixelDropArena extends Arena {
	private Region dropRegion = getRegion("dropzone");
	private Region boardRegion = getRegion("board");
	private Region lobbyAnimationRegion = getRegion("lobbyanimation");
	private Set<ProtectedRegion> stackRegions = getRegionsLike("designs");

	private double similarityThreshold = .6f;

	public PixelDropArena(Map<String, Object> map) {
		super(map);
		similarityThreshold = (Double) map.getOrDefault("similarityThreshold", similarityThreshold);
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("similarityThreshold", similarityThreshold);
		return map;
	}

	@Override
	public @NotNull String getRegionBaseName() {
		return "pixeldrop";
	}

}
