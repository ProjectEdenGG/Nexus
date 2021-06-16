package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IEntityChallengeProgress;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class KillChallengeProgress implements IEntityChallengeProgress {
	private final List<EntityType> kills = new ArrayList<>();

}
