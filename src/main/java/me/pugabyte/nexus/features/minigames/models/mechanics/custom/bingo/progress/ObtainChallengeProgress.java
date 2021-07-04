package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IItemChallengeProgress;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ObtainChallengeProgress implements IItemChallengeProgress {
	@NonNull
	private Minigamer minigamer;
	private final List<ItemStack> items = new ArrayList<>();

	@Override
	public String getTask() {
		return "Obtain";
	}

}
