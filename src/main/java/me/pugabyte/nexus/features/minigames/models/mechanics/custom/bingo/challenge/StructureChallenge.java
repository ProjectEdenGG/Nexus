package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.ProgressClass;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.StructureChallengeProgress;
import org.bukkit.StructureType;

@Data
@AllArgsConstructor
@ProgressClass(StructureChallengeProgress.class)
public class StructureChallenge implements IChallenge {
	private StructureType structureType;

}
