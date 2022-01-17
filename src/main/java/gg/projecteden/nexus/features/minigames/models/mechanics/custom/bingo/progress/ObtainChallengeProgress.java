package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IItemChallengeProgress;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
