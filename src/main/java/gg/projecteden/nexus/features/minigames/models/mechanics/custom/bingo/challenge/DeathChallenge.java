package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.ProgressClass;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.DeathChallengeProgress;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@Data
@AllArgsConstructor
@ProgressClass(DeathChallengeProgress.class)
public class DeathChallenge implements IChallenge {
	private Material displayMaterial;
	private DamageCause damageCause;

}
