package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IItemChallengeProgress;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ObtainChallengeProgress implements IItemChallengeProgress {
	private final List<ItemStack> items = new ArrayList<>();

	@Override
	public String getTask() {
		return "Obtain";
	}

}
