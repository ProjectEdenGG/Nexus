package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.GameMode;
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
import org.jetbrains.annotations.Nullable;

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
	protected Color defaultColor;

	public Decoration(String name, int modelData, @NotNull Material material, List<Hitbox> hitboxes) {
		this.name = name;
		this.modelData = modelData;
		this.material = material;
		this.hitboxes = hitboxes;

		if (this.isMultiBlock())
			this.disabledRotation = DisabledRotation.DEGREE_45;
	}

	public Decoration(String name, int modelData, List<Hitbox> hitboxes) {
		this(name, modelData, Material.PAPER, hitboxes);
	}

	public Decoration(String name, int modelData) {
		this(name, modelData, Hitbox.NONE());
	}

	public Decoration(String name, int modelData, Material material) {
		this(name, modelData, material, Hitbox.NONE());
	}

	public ItemStack getItem() {
		ItemBuilder decor = new ItemBuilder(material).customModelData(modelData).name(name).lore(lore);
		if (defaultColor != null)
			decor.dyeColor(defaultColor);

		return decor.build();
	}

	public static Color getDefaultStain() {
		return ColorType.hexToBukkit("#F4C57A");
	}

	public static Color getDefaultColor() {
		return ColorType.hexToBukkit("#FF5555");
	}

	public boolean isMultiBlock() {
		return this.getClass().getAnnotation(MultiBlock.class) != null;
	}

	//

	public boolean place(Player player, Block block, BlockFace clickedFace, ItemStack item) {
		if (!isValidPlacement(clickedFace))
			return false;

		Location origin = block.getRelative(clickedFace).getLocation().clone();

		// TODO: maybe add a toggleable to this, allowing for furniture to be placed inside of other blocks-- wouldn't replace
		ItemFrameRotation frameRotation = findValidFrameRotation(origin, ItemFrameRotation.of(player));
		if (frameRotation == null)
			return false;
		//

		ItemStack _item = item.clone();
		_item.setAmount(1);
		if (!player.getGameMode().equals(GameMode.CREATIVE))
			item.subtract();

		ItemFrame itemFrame = (ItemFrame) block.getWorld().spawnEntity(origin, EntityType.ITEM_FRAME);
		itemFrame.setFacingDirection(clickedFace, true);
		itemFrame.setRotation(frameRotation.getRotation());
//		itemFrame.setVisible(false);
		itemFrame.setGlowing(false);
		itemFrame.setSilent(true);
		itemFrame.setItem(_item, false);

		Hitbox.place(getHitboxes(), origin, frameRotation.getBlockFace());
		return true;
	}

	public boolean destroy(@NonNull Player player, @NonNull ItemFrame itemFrame) {
		if (this instanceof Seat seat) {
			if (seat.isOccupied(this, itemFrame)) {
				PlayerUtils.send(player, StringUtils.getPrefix("Decoration") + "&cSeat is occupied");
				return false;
			}
		}

		World world = player.getWorld();
		ItemStack item = itemFrame.getItem().clone();
		Location origin = itemFrame.getLocation().toBlockLocation().clone();

		itemFrame.remove();
		Hitbox.destroy(getHitboxes(), origin, ItemFrameRotation.of(itemFrame).getBlockFace());

		world.dropItemNaturally(origin, item);
		return true;
	}

	public boolean interact(Player player, ItemFrame itemFrame, Block block) {
		if (this instanceof Seat seat)
			seat.trySit(player, block, itemFrame.getRotation(), this);

		return true;
	}

	// validation

	private boolean isValidPlacement(BlockFace clickedFace) {
		for (DisabledPlacement disabledPlacement : getDisabledPlacements()) {
			if (disabledPlacement.getBlockFaces().contains(clickedFace))
				return false;
		}

		return true;
	}

	private @Nullable ItemFrameRotation findValidFrameRotation(Location origin, ItemFrameRotation frameRotation) {
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
		if (this.disabledRotation.equals(DisabledRotation.NONE))
			return true;

		if (!this.disabledRotation.contains(frameRotation))
			return true;

		return false;
	}
}
