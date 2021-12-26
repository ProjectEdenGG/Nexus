package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class Decoration {
	String name;
	int modelData;
	@NonNull Material material = Material.PAPER;
	List<String> lore = Collections.singletonList("Decoration");

	List<Hitbox> hitboxes = Hitbox.NONE();
	DisabledRotation disabledRotation = DisabledRotation.NONE;
	List<DisabledPlacement> disabledPlacements = new ArrayList<>();

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
		itemFrame.setSilent(true);
//		itemFrame.setVisible(false);
		itemFrame.setItem(_item, false);
		itemFrame.setRotation(frameRotation.getRotation());
		itemFrame.setFacingDirection(blockFace, true);

		// TODO:
		//  - Place hitbox according to frame rotation

		for (Hitbox hitbox : getHitboxes()) {
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
		for (Hitbox hitbox : getHitboxes()) {
			Block offsetBlock = origin.clone().getBlock();
			Map<BlockFace, Integer> offsets = hitbox.getOffsets();
			for (BlockFace _blockFace : offsets.keySet()) {
				offsetBlock = offsetBlock.getRelative(_blockFace, offsets.get(_blockFace));
			}

			offsetBlock.setType(Material.AIR);
		}

		world.dropItemNaturally(origin, item);
	}


	@Data
	@RequiredArgsConstructor
	public static class Hitbox {
		@NonNull Material material;
		Map<BlockFace, Integer> offsets = new HashMap<>();

		public Hitbox(@NotNull Material material, Map<BlockFace, Integer> offset) {
			this.material = material;
			this.offsets = offset;
		}

		public static Hitbox origin(Material material) {
			return new Hitbox(material);
		}

		public static List<Hitbox> single(Material material) {
			return Collections.singletonList(origin(material));
		}

		public static List<Hitbox> NONE() {
			return Collections.singletonList(new Hitbox(Material.AIR));
		}
	}

	@AllArgsConstructor
	public enum DisabledRotation {
		NONE(),
		DEGREE_45(ItemFrameRotation.DEGREE_45, ItemFrameRotation.DEGREE_135, ItemFrameRotation.DEGREE_225, ItemFrameRotation.DEGREE_315),
		DEGREE_90(ItemFrameRotation.DEGREE_0, ItemFrameRotation.DEGREE_90, ItemFrameRotation.DEGREE_180, ItemFrameRotation.DEGREE_270);

		List<Utils.ItemFrameRotation> frameRotations;

		DisabledRotation(ItemFrameRotation... rotations) {
			this.frameRotations = Arrays.asList(rotations);
		}

		public boolean contains(ItemFrameRotation frameRotation) {
			return this.frameRotations.contains(frameRotation);
		}

		public boolean contains(Rotation rotation) {
			return this.frameRotations.contains(ItemFrameRotation.from(rotation));
		}
	}

	@AllArgsConstructor
	public enum DisabledPlacement {
		FLOOR(BlockFace.UP),
		WALL(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST),
		CEILING(BlockFace.DOWN),
		;

		DisabledPlacement(BlockFace... blockFaces) {
			this.blockFaces = Arrays.asList(blockFaces);
		}

		@Getter
		List<BlockFace> blockFaces;
	}

	public ItemStack getItem() {
		return new ItemBuilder(material).customModelData(modelData).name(name).lore(lore).build();
	}

	public void interact(Player player, ItemFrame itemFrame) {
		if (this instanceof Seat seat)
			seat.trySit(player, itemFrame);
	}
}
