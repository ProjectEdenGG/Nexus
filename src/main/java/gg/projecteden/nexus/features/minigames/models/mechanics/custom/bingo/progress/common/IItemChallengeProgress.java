package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.Challenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IItemChallenge;
import gg.projecteden.nexus.utils.FuzzyItemStack;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface IItemChallengeProgress extends IChallengeProgress {

	List<ItemStack> getItems();

	String getTask();

	@Override
	default Set<String> getRemainingTasks(Challenge challenge) {
		final int LIMIT = 5;
		return getRemainingItems(challenge).stream().map(fuzzyItemStack -> {
			String materials = fuzzyItemStack.getMaterials().stream()
				.limit(LIMIT)
				.map(StringUtils::camelCase)
				.collect(Collectors.joining(" or "));

			if (fuzzyItemStack.getMaterials().size() > LIMIT)
				materials += " or etc.";

			return getTask() + " " + fuzzyItemStack.getAmount() + " " + materials;
		}).collect(Collectors.toSet());
	}

	default Set<FuzzyItemStack> getRemainingItems(Challenge challenge) {
		final Set<FuzzyItemStack> required = ((IItemChallenge) challenge.getChallenge()).getItems();
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
