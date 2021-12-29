package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.features.resourcepack.decoration.types.Seat;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class Decoration {
	protected String name;
	protected int modelData;
	protected @NonNull Material material = Material.PAPER;
	protected List<String> lore = Collections.singletonList("Decoration");

	protected List<Hitbox> hitboxes = Hitbox.NONE();
	protected DisabledRotation disabledRotation = DisabledRotation.NONE;
	protected List<DisabledPlacement> disabledPlacements = new ArrayList<>();

	public Decoration(String name, int modelData, @NotNull Material material, List<Hitbox> hitboxes) {
		this.name = name;
		this.modelData = modelData;
		this.material = material;
		this.hitboxes = hitboxes;
	}


	public ItemFrameRotation getValidRotation(ItemFrameRotation frameRotation) {
		if (this.disabledRotation.equals(DisabledRotation.NONE))
			return frameRotation;

		if (!this.disabledRotation.contains(frameRotation))
			return frameRotation;

		return ItemFrameRotation.from(frameRotation.getRotation().rotateClockwise());
	}

	public void place(Player player, Block block, BlockFace blockFace, ItemStack item) {
		if (!isValidBlockFace(blockFace))
			return;

		ItemStack _item = item.clone();
		_item.setAmount(1);
		item.subtract();

		World world = block.getWorld();
		Location origin = block.getRelative(blockFace).getLocation().clone();
		ItemFrameRotation frameRotation = getValidRotation(ItemFrameRotation.of(player));

		ItemFrame itemFrame = (ItemFrame) world.spawnEntity(origin, EntityType.ITEM_FRAME);
		itemFrame.setRotation(frameRotation.getRotation());
		itemFrame.setFacingDirection(blockFace, true);
//		itemFrame.setVisible(false);
		itemFrame.setGlowing(false);
		itemFrame.setSilent(true);
		itemFrame.setItem(_item, false);

		Hitbox.place(getHitboxes(), origin, frameRotation.getBlockFace());
	}

	private boolean isValidBlockFace(BlockFace blockFace) {
		for (DisabledPlacement disabledPlacement : getDisabledPlacements()) {
			if (disabledPlacement.getBlockFaces().contains(blockFace))
				return false;
		}
		return true;
	}

	public void destroy(Player player, ItemFrame itemFrame) {
		if (Seat.isOccupied(itemFrame.getLocation()))
			return;

		World world = player.getWorld();
		ItemStack item = itemFrame.getItem().clone();
		Location origin = itemFrame.getLocation().toBlockLocation().clone();

		itemFrame.remove();
		Hitbox.destroy(getHitboxes(), origin, ItemFrameRotation.of(itemFrame).getBlockFace());

		world.dropItemNaturally(origin, item);
	}

	public ItemStack getItem() {
		return new ItemBuilder(material).customModelData(modelData).name(name).lore(lore).build();
	}

	public void interact(Player player, ItemFrame itemFrame) {
		if (this instanceof Seat seat)
			seat.trySit(player, itemFrame);
	}
}
