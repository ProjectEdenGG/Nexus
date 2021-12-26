package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.common.BlockDecor;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.LargeFireplace;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MobPlushie;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@AllArgsConstructor
public enum Decorations {
	MOB_PLUSHIE_ENDERDRAGON(new MobPlushie("Ender Dragon MobPlushie", -1, 5.0)),
	FIREPLACE_LARGE_DARK(new LargeFireplace("Dark Fireplace", -1)),
	DYE_STATION(new BlockDecor("Dye Station", 1, Material.CRAFTING_TABLE));

	Decoration decoration;

	public ItemStack getItem() {
		return decoration.getItem().clone();
	}

	public void place(Player player, Block block, BlockFace blockFace) {
		World world = block.getWorld();
		Location origin = block.getRelative(blockFace).getLocation().clone();

		ItemFrame itemFrame = (ItemFrame) world.spawnEntity(origin, EntityType.ITEM_FRAME);
//		itemFrame.setVisible(false);
		itemFrame.setItem(decoration.getItem(), false);

		// TODO: Place hitbox according to frame rotation
		ItemFrameRotation frameRotation = decoration.getValidRotation(Utils.ItemFrameRotation.of(player));
		itemFrame.setRotation(frameRotation.getRotation());

		for (Hitbox hitbox : decoration.getHitboxes()) {
			Material material = hitbox.getMaterial();

			Block offsetBlock = origin.clone().getBlock();
			Map<BlockFace, Integer> offsets = hitbox.getOffsets();
			for (BlockFace _blockFace : offsets.keySet()) {
				offsetBlock = offsetBlock.getRelative(_blockFace, offsets.get(_blockFace));
			}

			if (ItemUtils.isNullOrAir(material))
				material = Material.AIR;

			offsetBlock.setType(material);
		}
	}

	public static Decorations of(ItemStack tool) {
		for (Decorations decoration : values()) {
			if (ItemUtils.isFuzzyMatch(decoration.getItem(), tool))
				return decoration;
		}

		return null;
	}


}
