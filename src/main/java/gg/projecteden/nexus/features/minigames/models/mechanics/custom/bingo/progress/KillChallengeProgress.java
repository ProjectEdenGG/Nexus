package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IEntityChallengeProgress;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class KillChallengeProgress implements IEntityChallengeProgress {
	@NonNull
	private Minigamer minigamer;
	private final List<EntityType> progress = new ArrayList<>();

	@NotNull
	public String getAction() {
		return "Kill";
	}

}
