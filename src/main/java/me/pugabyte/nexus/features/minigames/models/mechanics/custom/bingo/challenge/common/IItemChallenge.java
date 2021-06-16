package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common;

import me.pugabyte.nexus.utils.FuzzyItemStack;

import java.util.Set;

public interface IItemChallenge extends IChallenge {

	Set<FuzzyItemStack> getItems();

}
