package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPlacedEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPrePlaceEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableWallThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils.debug;

/* TODO: Implement "Structure" type:
	- Allows for the use of decorations that require more than 1 itemframe, such as:
		- the pugmas train
		- the pugmas air balloon
		- more customizable chairs (dyeable wood + dyeable cushion)

 */
@Data
public class DecorationConfig {
	public static final String NBT_OWNER_KEY = "DecorationOwner";
	public static final String NBT_DECOR_NAME = "DecorationName";
	protected String id;
	protected String name;
	protected @NonNull Material material = Material.PAPER;
	protected int modelId;
	protected Predicate<Integer> modelIdPredicate;
	protected String placeSound = Sound.ENTITY_ITEM_FRAME_ADD_ITEM.getKey().getKey();
	protected String hitSound = Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM.getKey().getKey();
	protected String breakSound = Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM.getKey().getKey();
	protected String lore = "&e&oDecoration";

	protected List<Hitbox> hitboxes = Hitbox.NONE();
	protected RotationType rotationType = RotationType.BOTH;
	protected List<PlacementType> disabledPlacements = new ArrayList<>();
	protected boolean rotatable = true;

	public DecorationConfig() {
		allDecorationTypes.add(this);
	}

	public DecorationConfig(String name, @NotNull Material material, int modelId, Predicate<Integer> modelIdPredicate, List<Hitbox> hitboxes) {
		this();
		this.id = name.toLowerCase().replaceAll(" ", "_");
		this.name = name;
		this.material = material;
		this.modelId = modelId;
		this.modelIdPredicate = modelIdPredicate;
		this.hitboxes = hitboxes;

		if (this.isMultiBlock()) {
			this.rotationType = RotationType.DEGREE_90;
			this.rotatable = false;
		}
	}

	public DecorationConfig(String name, @NonNull CustomMaterial customMaterial, List<Hitbox> hitboxes) {
		this(name, customMaterial.getMaterial(), customMaterial.getModelId(), modelId -> modelId == customMaterial.getModelId(), hitboxes);
	}

	public DecorationConfig(String name, CustomMaterial material) {
		this(name, material, Hitbox.NONE());
	}

	@Getter
	private static final List<DecorationConfig> allDecorationTypes = new ArrayList<>();

	public static @Nullable DecorationConfig of(ItemFrame itemFrame) {
		if (!itemFrame.isValid())
			return null;

		ItemStack item = itemFrame.getItem();
		if (Nullables.isNullOrAir(item))
			return null;

		return DecorationConfig.of(item);
	}

	public static DecorationConfig of(ItemStack tool) {
		if (Nullables.isNullOrAir(tool))
			return null;

		if (ModelId.of(tool) == 0)
			return null;

		for (DecorationConfig decoration : allDecorationTypes)
			if (decoration.isFuzzyMatch(tool))
				return decoration;

		return null;
	}

	public static DecorationConfig of(String id) {
		for (DecorationConfig config : allDecorationTypes) {
			if (config.getId().equalsIgnoreCase(id))
				return config;
		}

		for (DecorationType type : DecorationType.values()) {
			DecorationConfig config = type.getConfig();
			if (config.getId().equalsIgnoreCase(id))
				return config;
		}

		return null;
	}

	public static DecorationConfig of(CustomMaterial material) {
		for (DecorationConfig config : allDecorationTypes)
			if (config.getMaterial() == material.getMaterial() && config.getModelId() == material.getModelId())
				return config;

		return null;
	}

	public boolean isFuzzyMatch(ItemStack item2) {
		ItemStack item1 = getItem().clone();

		if (item2 == null)
			return false;

		if (!item1.getType().equals(item2.getType()))
			return false;

		int decorModelData = ModelId.of(item1);
		int itemModelData = ModelId.of(item2);

		if (modelIdPredicate != null)
			return modelIdPredicate.test(itemModelData);
		else
			return decorModelData == itemModelData;
	}

	private static final Set<Material> hitboxTypes = new HashSet<>();

	public static Set<Material> getHitboxTypes() {
		if (!hitboxTypes.isEmpty())
			return hitboxTypes;

		allDecorationTypes.forEach(decorationType ->
			hitboxTypes.addAll(decorationType.getHitboxes()
				.stream()
				.map(Hitbox::getMaterial)
				.filter(material -> !MaterialTag.ALL_AIR.isTagged(material))
				.toList()));

		return hitboxTypes;
	}

	public ItemStack getItem() {
		ItemBuilder decor = new ItemBuilder(material).modelId(modelId).name(name).lore(lore);

		if (this instanceof Colorable colorable && colorable.isColorable())
			decor.dyeColor(colorable.getColor());

		return decor.build();
	}

	public boolean isMultiBlock() {
		return this.getClass().getAnnotation(MultiBlock.class) != null;
	}

	public boolean isMultiBlockWallThing() {
		return isMultiBlock() && isWallThing();
	}

	public boolean isWallThing() {
		return this instanceof WallThing || this instanceof DyeableWallThing;
	}

	public boolean isStructure() {
		return this.getClass().getAnnotation(Structure.class) != null;
	}

	public boolean hasInventory() {
		return this.getClass().getAnnotation(VirtualInventory.class) != null;
	}

	public boolean isSeat() {
		return this instanceof Seat;
	}

	// validation

	boolean isValidPlacement(Block block, BlockFace clickedFace, Player debugger) {
		for (PlacementType placementType : disabledPlacements) {
			if (placementType.getBlockFaces().contains(clickedFace)) {
				debug(debugger, "denied placement type");
				return false;
			}
		}

		Block placed = block.getRelative(clickedFace);
		List<ItemFrame> itemFrames = new ArrayList<>(placed.getLocation().toCenterLocation().getNearbyEntitiesByType(ItemFrame.class, 0.5));

		for (ItemFrame itemFrame : itemFrames) {
			debug(debugger, "loc = " + StringUtils.getShortLocationString(itemFrame.getLocation()));
			debug(debugger, "attached face = " + itemFrame.getAttachedFace() + " -> " + itemFrame.getAttachedFace().getOppositeFace());
			if (itemFrame.getAttachedFace().getOppositeFace() == clickedFace) {
				debug(debugger, "itemframe exists in location, face = " + clickedFace);
				return false;
			}
		}

		return true;
	}

	@Nullable
	protected Utils.ItemFrameRotation findValidFrameRotation(Location origin, ItemFrameRotation frameRotation, Player debugger) {
		if (isValidLocation(origin, frameRotation, debugger)) {
			debug(debugger, "is valid rotation: " + frameRotation);
			return frameRotation;
		}

		BlockFace rotated = frameRotation.getBlockFace();
		for (int tries = 0; tries < DecorationUtils.getDirections().size(); tries++) {
			rotated = DecorationUtils.rotateClockwise(rotated);

			ItemFrameRotation newFrameRotation = ItemFrameRotation.from(rotated);
			if (isValidLocation(origin, newFrameRotation, debugger)) {
				debug(debugger, "found valid rotation: " + newFrameRotation);
				return newFrameRotation;
			}
		}

		debug(debugger, "couldn't find a valid rotation");
		return null;
	}

	private boolean isValidLocation(Location origin, ItemFrameRotation frameRotation, Player debugger) {
		return isValidLocation(origin, frameRotation, true, debugger);
	}

	private boolean isValidLocation(Location origin, ItemFrameRotation frameRotation, boolean validateRotation, Player debugger) {
		return isValidLocation(origin, frameRotation, frameRotation.getBlockFace(), validateRotation, debugger);
	}

	private boolean isValidLocation(Location origin, ItemFrameRotation frameRotation, BlockFace blockFace, boolean validateRotation, Player debugger) {
		if (validateRotation) {
			if (!isValidRotation(frameRotation)) {
				debug(debugger, "- invalid rotation: " + frameRotation);
				return false;
			}
		}

		debug(debugger, "Frame Rotation: " + frameRotation + " | BlockFace: " + blockFace);

		List<Hitbox> hitboxes = Hitbox.rotateHitboxes(this, blockFace);
		for (Hitbox hitbox : hitboxes) {
			Block block = hitbox.getOffsetBlock(origin);
			if (!MaterialTag.ALL_AIR.isTagged(block)) {
				debug(debugger, "- rotated hitbox found non-air");
				return false;
			}
		}

		return true;
	}

	public boolean isValidRotation(ItemFrameRotation frameRotation) {
		if (rotationType == RotationType.BOTH)
			return true;

		return rotationType.contains(frameRotation);
	}

	//

	public boolean place(Player player, Block block, BlockFace clickedFace, ItemStack item) {
		final Decoration decoration = new Decoration(this, null);
		debug(player, "validating placement...");
		if (!isValidPlacement(block, clickedFace, player)) {
			debug(player, "- invalid placement");
			return false;
		}

		Location origin = block.getRelative(clickedFace).getLocation().clone();

		// TODO: maybe add a toggleable to this?, allowing for furniture to be placed inside of other blocks-- wouldn't replace
		ItemFrameRotation frameRotation;
		boolean placedOnWall = DecorationUtils.getCardinalFaces().contains(clickedFace);
		boolean canPlaceOnWall = !decoration.getConfig().disabledPlacements.contains(PlacementType.WALL);
		BlockFace blockFaceOverride = null;


		if (placedOnWall && canPlaceOnWall) {
			frameRotation = ItemFrameRotation.DEGREE_0;
			blockFaceOverride = frameRotation.getBlockFace();
			debug(player, "is placing on wall");

			if (isMultiBlock()) {
				debug(player, "is multiblock");
				blockFaceOverride = clickedFace.getOppositeFace();
				debug(player, "BlockFace Override 4: " + blockFaceOverride);
			}

			if (!isValidLocation(origin, frameRotation, blockFaceOverride, false, player)) {
				debug(player, "- invalid frame location");
				return false;
			}
		} else {
			frameRotation = findValidFrameRotation(origin, ItemFrameRotation.of(player), player);
		}

		if (frameRotation == null) {
			debug(player, "- couldn't find a valid frame rotation");
			return false;
		}
		//


		if (clickedFace == BlockFace.DOWN) {
			switch (PlayerUtils.getBlockFace(player)) {
				case EAST, WEST -> frameRotation = frameRotation.getOppositeRotation();
				case SOUTH_WEST, NORTH_EAST ->
					frameRotation = frameRotation.rotateCounterClockwise().rotateCounterClockwise();
				case NORTH_WEST, SOUTH_EAST -> frameRotation = frameRotation.rotateClockwise().rotateClockwise();
			}
		}

		debug(player, "frameRotation = " + frameRotation.name());

		DecorationPrePlaceEvent prePlaceEvent = new DecorationPrePlaceEvent(player, decoration, item, clickedFace, frameRotation);
		if (!prePlaceEvent.callEvent()) {
			debug(player, "- PrePlace event was cancelled");
			return false;
		}

		ItemStack newItem = prePlaceEvent.getItem();
		ItemBuilder itemCopy = ItemBuilder.oneOf(newItem);
		ItemUtils.subtract(player, item);

		String itemName = itemCopy.name();
		debug(player, "ItemName: " + itemName);
		final ItemStack finalItem = itemCopy
			.nbt(nbt -> nbt.setString(NBT_OWNER_KEY, player.getUniqueId().toString()))
			.nbt(nbt -> nbt.setString(NBT_DECOR_NAME, itemName))
			.resetName()
			.build();

		final BlockFace finalFace = prePlaceEvent.getAttachedFace();
		final ItemFrameRotation finalRotation = prePlaceEvent.getRotation();

		ItemFrame itemFrame = block.getWorld().spawn(origin, ItemFrame.class, _itemFrame -> {
			_itemFrame.customName(null);
			_itemFrame.setCustomNameVisible(false);
			_itemFrame.setFacingDirection(finalFace, true);
			_itemFrame.setRotation(finalRotation.getRotation());
			_itemFrame.setVisible(false);
			_itemFrame.setGlowing(false);
			_itemFrame.setSilent(true);
			_itemFrame.setItem(finalItem, false);
		});

		BlockFace placeFace = frameRotation.getBlockFace();
		if (blockFaceOverride != null) {
			placeFace = blockFaceOverride;
			debug(player, "BlockFace Override 3: " + blockFaceOverride);
		}

		Hitbox.place(getHitboxes(), origin, placeFace);

		new SoundBuilder(hitSound).location(origin).play();

		debug(player, "placed");
		new DecorationPlacedEvent(player, decoration, finalItem, finalFace, finalRotation, itemFrame.getLocation()).callEvent();
		return true;
	}
}
