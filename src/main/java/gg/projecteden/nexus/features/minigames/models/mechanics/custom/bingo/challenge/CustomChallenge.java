package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.ProgressClass;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.CustomChallengeProgress;
import lombok.Getter;
import org.bukkit.Material;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@ProgressClass(CustomChallengeProgress.class)
public class CustomChallenge implements IChallenge {
	private final Material displayMaterial;
	private final Set<String> tasks;

	public CustomChallenge(Material displayMaterial, String... tasks) {
		this.displayMaterial = displayMaterial;
		this.tasks = new LinkedHashSet<>(Set.of(tasks));
	}

	public static class CustomTask {
		public static final String SPAWN_AN_IRON_GOLEM = "Spawn an Iron Golem";
		public static final String SPAWN_A_SNOW_GOLEM = "Spawn a Snow Golem";
		public static final String CLIMB_TO_BUILD_HEIGHT = "Climb to world build height";
		public static final String DIG_TO_BEDROCK = "Dig to Bedrock";
		public static final String TRADE_WITH_A_VILLAGER = "Trade with a Villager";
		public static final String TRADE_WITH_A_PIGLIN = "Trade with a Piglin";
	}

}
