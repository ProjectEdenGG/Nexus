package gg.projecteden.nexus.features.resourcepack.customblocks.models.common;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.SoundUtils;
import gg.projecteden.nexus.utils.SoundUtils.SoundAction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface ISupportPlants extends IInteractable {

	MaterialTag SUPPORTED_PLANTS = new MaterialTag(Material.SHORT_GRASS, Material.FERN, Material.TALL_GRASS, Material.LARGE_FERN,
		Material.DEAD_BUSH, Material.WARPED_FUNGUS, Material.CRIMSON_FUNGUS, Material.CRIMSON_ROOTS, Material.WARPED_ROOTS,
		Material.NETHER_SPROUTS, Material.BAMBOO_SAPLING, Material.SWEET_BERRY_BUSH)
		.append(MaterialTag.ALL_FLOWERS, MaterialTag.SAPLINGS);

	default boolean canSupport(@NotNull Material blockMaterial) {
		return SUPPORTED_PLANTS.isTagged(blockMaterial);
	}

	List<Material> specialTypes = new ArrayList<>(List.of(Material.BAMBOO));

	@Override
	default boolean onRightClickedWithItem(Player player, CustomBlock customBlock, Block block, BlockFace face, ItemStack itemInHand) {
		Material material = itemInHand.getType();
		if (!specialTypes.contains(material))
			return false;

		if (material == Material.BAMBOO) {
			Block plant = block.getRelative(face);
			plant.setType(Material.BAMBOO_SAPLING);
			SoundUtils.playSound(plant, SoundAction.PLACE);
			return true;
		}

		return false;
	}
}
