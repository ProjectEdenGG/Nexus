package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.ProgressClass;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.StatisticIncreaseChallengeProgress;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.Statistic.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@Data
@ProgressClass(StatisticIncreaseChallengeProgress.class)
public class StatisticIncreaseChallenge implements IChallenge {
	private Material displayMaterial;

	private Statistic statistic;
	private Material material;
	private EntityType entityType;
	private int amount;

	public StatisticIncreaseChallenge(Material displayMaterial, Statistic statistic, int amount) {
		this(displayMaterial, statistic, null, null, amount);
	}

	public StatisticIncreaseChallenge(Material displayMaterial, Statistic statistic, Material material, int amount) {
		this(displayMaterial, statistic, material, null, amount);
	}

	public StatisticIncreaseChallenge(Material displayMaterial, Statistic statistic, EntityType entityType, int amount) {
		this(displayMaterial, statistic, null, entityType, amount);
	}

	public StatisticIncreaseChallenge(Material displayMaterial, Statistic statistic, Material material, EntityType entityType, int amount) {
		this.displayMaterial = displayMaterial;
		this.statistic = statistic;
		this.material = material;
		this.entityType = entityType;
		this.amount = amount;
	}

	public int getValue(Player player) {
		if (statistic.getType() == Type.BLOCK || statistic.getType() == Type.ITEM)
			return player.getStatistic(statistic, material);
		else if (statistic.getType() == Type.ENTITY)
			return player.getStatistic(statistic, entityType);
		else
			return player.getStatistic(statistic);
	}

}
