package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.features.recipes.functionals.backpacks.BackpackCommand;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Backpack extends DecorationConfig {
	@Getter
	protected BackpackTier tier;

	public Backpack(BackpackTier tier) {
		this.tier = tier;
		this.id = "backpack_3d_" + tier.name().toLowerCase();
		this.name = "Backpack";
		this.material = Backpacks.getDefaultBackpack().getType();
		this.model = tier.getModel().getModel();
		this.hitboxes = Hitbox.NONE();
		this.disabledPlacements = PlacementType.FLOOR.getDisabledPlacements();
		this.rotatable = true;
		this.overrideTabComplete = true;
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = BackpackCommand.fix(super.getItem());
		return tier.apply(item);
	}
}
