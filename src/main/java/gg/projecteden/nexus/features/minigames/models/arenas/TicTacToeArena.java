package gg.projecteden.nexus.features.minigames.models.arenas;

import gg.projecteden.nexus.features.minigames.models.Arena;
import lombok.Data;
import lombok.ToString;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Data
@ToString(callSuper = true)
@SerializableAs("TicTacToeArena")
public class TicTacToeArena extends Arena {

	public TicTacToeArena(Map<String, Object> map) {
		super(map);
	}

	public @NotNull String getSchematicBaseName() {
		return "minigames/tictactoe/";
	}

	@Override
	public @NotNull String getRegionBaseName() {
		return "tictactoe";
	}

}
