package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IItemChallengeProgress;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ObtainChallengeProgress implements IItemChallengeProgress {
	@NonNull
	private Minigamer minigamer;

	@Override
	public String getTask() {
		return "Obtain";
	}

	public List<ItemStack> getItems() {
		return Arrays.asList(minigamer.getOnlinePlayer().getInventory().getContents());
	}

}
