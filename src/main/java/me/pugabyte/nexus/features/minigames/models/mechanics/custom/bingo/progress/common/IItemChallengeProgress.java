package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common;

import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IItemChallenge;
import me.pugabyte.nexus.utils.FuzzyItemStack;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface IItemChallengeProgress extends IChallengeProgress {

	List<ItemStack> getItems();

	String getTask();

	@Override
	default Set<String> getRemainingTasks(IChallenge challenge) {
		return getRemainingItems(challenge).stream().map(fuzzyItemStack -> {
			final String materials = fuzzyItemStack.getMaterials().stream()
				.map(StringUtils::camelCase)
				.collect(Collectors.joining(" or "));

			return getTask() + " " + fuzzyItemStack.getAmount() + " " + materials;
		}).collect(Collectors.toSet());
	}

	default Set<FuzzyItemStack> getRemainingItems(IChallenge challenge) {
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
