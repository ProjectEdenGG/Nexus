package gg.projecteden.nexus.features.resourcepack.decoration.common;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPlaceEvent;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class DecorationConfig {
	public static final String NBT_OWNER_KEY = "DecorationOwner";
	protected String name;
	protected @NonNull Material material = Material.PAPER;
	protected int modelId;
	protected List<String> lore = Collections.singletonList("Decoration");

	protected List<Hitbox> hitboxes = Hitbox.NONE();
	protected RotationType rotationType = RotationType.BOTH;
	protected List<PlacementType> disabledPlacements = new ArrayList<>();

	public DecorationConfig(String name, @NotNull CustomMaterial material, List<Hitbox> hitboxes) {
		this.name = name;
		this.modelId = material.getModelId();
		this.material = material.getMaterial();
		this.hitboxes = hitboxes;

		if (this.isMultiBlock())
			this.rotationType = RotationType.DEGREE_90;
	}

	public DecorationConfig(String name, CustomMaterial material) {
		this(name, material, Hitbox.NONE());
	}

	public ItemStack getItem() {
		ItemBuilder decor = new ItemBuilder(material).modelId(modelId).name(name).lore(lore);

		if (this instanceof Colorable colorable && colorable.isColorable())
			decor.dyeColor(colorable.getType().getColor());

		return decor.build();
	}

	public boolean isMultiBlock() {
		return this.getClass().getAnnotation(MultiBlock.class) != null;
	}

	public boolean isSeat() {
		return this instanceof Seat;
	}

	// validation

	boolean isValidPlacement(BlockFace clickedFace) {
		for (PlacementType placementType : disabledPlacements) {
			if (placementType.getBlockFaces().contains(clickedFace))
				return false;
		}

		return true;
	}

	@Nullable
	protected Utils.ItemFrameRotation findValidFrameRotation(Location origin, ItemFrameRotation frameRotation) {
		if (isValidLocation(origin, frameRotation))
			return frameRotation;

		BlockFace rotated = frameRotation.getBlockFace();
		for (int tries = 0; tries < DecorationUtils.getDirections().size(); tries++) {
			rotated = DecorationUtils.rotateClockwise(rotated);

			ItemFrameRotation newFrameRotation = ItemFrameRotation.from(rotated);
			if (isValidLocation(origin, newFrameRotation))
				return newFrameRotation;
		}

		return null;
	}

	private boolean isValidLocation(Location origin, ItemFrameRotation frameRotation) {
		if (!isValidRotation(frameRotation))
			return false;

		List<Hitbox> hitboxes = Hitbox.rotateHitboxes(this, frameRotation.getBlockFace());
		for (Hitbox hitbox : hitboxes) {
			if (!MaterialTag.ALL_AIR.isTagged(hitbox.getOffsetBlock(origin).getType()))
				return false;
		}

		return true;
	}

	public boolean isValidRotation(ItemFrameRotation frameRotation) {
		if (rotationType.equals(RotationType.BOTH))
			return true;

		if (rotationType.contains(frameRotation))
			return true;

		return false;
	}

	//

	public boolean place(Player player, Block block, BlockFace clickedFace, ItemStack item) {
		final Decoration decoration = new Decoration(this, null);
		if (!isValidPlacement(clickedFace))
			return false;

		Location origin = block.getRelative(clickedFace).getLocation().clone();

		// TODO: maybe add a toggleable to this, allowing for furniture to be placed inside of other blocks-- wouldn't replace
		ItemFrameRotation frameRotation = findValidFrameRotation(origin, ItemFrameRotation.of(player));
		if (frameRotation == null)
			return false;
		//

		DecorationPlaceEvent placeEvent = new DecorationPlaceEvent(player, decoration);
		if (!placeEvent.callEvent())
			return false;

		ItemStack _item = item.clone();
		_item.setAmount(1);
		ItemUtils.subtract(player, item);

		NBTItem nbtItem = new NBTItem(_item);
		nbtItem.setString(NBT_OWNER_KEY, player.getUniqueId().toString());

		ItemFrame itemFrame = (ItemFrame) block.getWorld().spawnEntity(origin, EntityType.ITEM_FRAME);
		itemFrame.setFacingDirection(clickedFace, true);
		itemFrame.setRotation(frameRotation.getRotation());
		itemFrame.setVisible(false);
		itemFrame.setGlowing(false);
		itemFrame.setSilent(true);
		itemFrame.setItem(nbtItem.getItem(), false);

		Hitbox.place(getHitboxes(), origin, frameRotation.getBlockFace());
		return true;
	}
}
