package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.Challenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.DeathChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class DeathChallengeProgress implements IChallengeProgress {
	@NonNull
	private Minigamer minigamer;
	private final Set<DamageCause> damageCauses = new HashSet<>();

	@Override
	public Set<String> getRemainingTasks(Challenge challenge) {
		final DamageCause required = ((DeathChallenge) challenge.getChallenge()).getDamageCause();
		if (damageCauses.contains(required))
			return Collections.emptySet();

		return Collections.singleton("Die by " + StringUtils.camelCase(required).toLowerCase());
	}

}
