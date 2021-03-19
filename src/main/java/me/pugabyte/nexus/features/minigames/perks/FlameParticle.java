package me.pugabyte.nexus.features.minigames.perks;

import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.features.minigames.models.perks.common.TickablePerk;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FlameParticle extends TickablePerk {
	@Override
	public String getName() {
		return "Flame";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.MAGMA_BLOCK);
	}

	@Override
	public String[] getDescription() {
		return new String[]{"TODO"};
	}

	@Override
	public PerkCategory getCategory() {
		return PerkCategory.PARTICLE;
	}

	@Override
	public int getPrice() {
		return 0;
	}

	@Override
	public void tick(Minigamer minigamer) {

	}
}
