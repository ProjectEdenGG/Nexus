package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.FixBackpackCommand;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Backpack extends DecorationConfig {
	@Getter
	private final BackpackTier tier;

	public Backpack(BackpackTier tier) {
		this.tier = tier;
		this.id = "backpack_3d_" + tier.name().toLowerCase();
		this.name = "Backpack";
		this.material = Backpacks.getDefaultBackpack().getType();
		this.modelId = tier.getModelID();
		this.hitboxes = Hitbox.NONE();
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
		this.overrideTabComplete = true;
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = FixBackpackCommand.fix(super.getItem());
		return Backpacks.setTier(item, tier);
	}
}
