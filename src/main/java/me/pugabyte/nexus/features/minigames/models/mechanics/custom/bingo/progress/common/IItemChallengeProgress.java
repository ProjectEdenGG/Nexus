package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common;

import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IItemChallenge;
import me.pugabyte.nexus.utils.FuzzyItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface IItemChallengeProgress extends IChallengeProgress {

	List<ItemStack> getItems();

	default boolean isCompleted(IChallenge challenge) {
		return getRemainingTasks(challenge).isEmpty();
	}

	default Set<FuzzyItemStack> getRemainingTasks(IChallenge challenge) {
		final Set<FuzzyItemStack> required = ((IItemChallenge) challenge).getItems();
		final List<ItemStack> provided = getItems();
		final Set<FuzzyItemStack> todo = new HashSet<>();

		required.stream().map(FuzzyItemStack::clone).forEach(fuzzy -> {
			for (ItemStack item : provided) {
				if (!fuzzy.getMaterials().contains(item.getType()))
					continue;

				fuzzy.setAmount(fuzzy.getAmount() - item.getAmount());

				if (fuzzy.getAmount() <= 0)
					break;
			}

			if (fuzzy.getAmount() > 0)
				todo.add(fuzzy);
		});

		return todo;
	}

}
