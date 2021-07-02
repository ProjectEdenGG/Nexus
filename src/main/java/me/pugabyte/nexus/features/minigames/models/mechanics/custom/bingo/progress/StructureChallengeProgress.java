package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.StructureChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import org.bukkit.StructureType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static eden.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.StringUtils.an;

@Data
@NoArgsConstructor
public class StructureChallengeProgress implements IChallengeProgress {
	private final Set<StructureType> structures = new HashSet<>();

	@Override
	public Set<String> getRemainingTasks(IChallenge challenge) {
		final StructureType required = ((StructureChallenge) challenge).getStructureType();
		if (structures.contains(required))
			return Collections.emptySet();

		return Set.of("Visit " + an(camelCase(required.getName())));
	}

}
