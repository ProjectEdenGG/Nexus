package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.ProgressClass;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.StatisticIncreaseChallengeProgress;
import org.bukkit.Material;
import org.bukkit.Statistic;

@Data
@AllArgsConstructor
@ProgressClass(StatisticIncreaseChallengeProgress.class)
public class StatisticIncreaseChallenge implements IChallenge {
	private Material displayMaterial;
	private Statistic statistic;
	private int amount;

}
