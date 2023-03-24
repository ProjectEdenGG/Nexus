package gg.projecteden.nexus.features.minigames.models.arenas;

import gg.projecteden.nexus.features.minigames.models.Arena;
import lombok.Data;
import lombok.ToString;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Data
@ToString(callSuper = true)
@SerializableAs("Connect4Arena")
public class Connect4Arena extends Arena { // TODO - CANT BE CAST TO ARENA ??

	public Connect4Arena(Map<String, Object> map) {
		super(map);
	}

	@Override
	public @NotNull String getRegionBaseName() {
		return "connect4";
	}
}
