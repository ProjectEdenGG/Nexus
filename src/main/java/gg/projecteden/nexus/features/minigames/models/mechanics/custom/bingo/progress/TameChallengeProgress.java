package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IEntityChallengeProgress;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class TameChallengeProgress implements IEntityChallengeProgress {
	@NonNull
	private Minigamer minigamer;
	private final List<EntityType> progress = new ArrayList<>();

	@Override
	public String getAction() {
		return "Tame";
	}

}
