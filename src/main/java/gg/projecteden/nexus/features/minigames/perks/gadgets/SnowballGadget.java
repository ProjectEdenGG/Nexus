package gg.projecteden.nexus.features.minigames.perks.gadgets;

import gg.projecteden.nexus.features.minigames.models.perks.common.GadgetPerk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SnowballGadget implements GadgetPerk {
	@Override
	public @NotNull String getName() {
		return "Snowballs";
	}

	@Override
	public @NotNull List<String> getDescription() {
		return Collections.singletonList("An endless supply of snowballs to toss at your friends");
	}

	@Override
	public int getPrice() {
		return 10;
	}

	@Override
	public ItemStack getItem() {
		return basicItem(Material.SNOWBALL);
	}

	@Override
	public boolean cancelEvent() {
		return false;
	}

	@Override
	public void useGadget(Player player) {}
}
