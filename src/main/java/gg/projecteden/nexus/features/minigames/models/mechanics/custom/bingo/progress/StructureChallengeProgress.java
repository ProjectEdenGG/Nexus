package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.StructureChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.StructureType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class StructureChallengeProgress implements IChallengeProgress<StructureChallenge> {
	@NonNull
	private Minigamer minigamer;
	private final Set<StructureType> structures = new HashSet<>();

	@Override
	public Set<String> getRemainingTasks(StructureChallenge challenge) {
		final StructureType required = challenge.getStructureType();
		if (structures.contains(required))
			return Collections.emptySet();

		return Set.of("Visit " + StringUtils.an(StringUtils.camelCase(required.getName())));
	}

}
