package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.TypeConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.Basic;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPlacedEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPrePlaceEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
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
import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils.error;
import static gg.projecteden.nexus.utils.PlayerUtils.send;
import static gg.projecteden.nexus.utils.PlayerUtils.sendLine;

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
	public static final String decorLore = "&eDecoration";
	protected String id;
	protected String name;
	protected @NonNull Material material = Material.PAPER;
	protected int modelId;
	protected Predicate<Integer> modelIdPredicate;
	protected String placeSound = Sound.ENTITY_ITEM_FRAME_ADD_ITEM.getKey().getKey();
	protected String hitSound = Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM.getKey().getKey();
	protected String breakSound = Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM.getKey().getKey();
	protected List<String> lore = new ArrayList<>(List.of(decorLore));

	protected List<Hitbox> hitboxes = Hitbox.NONE();
	protected RotationSnap rotationSnap = RotationSnap.BOTH;
	protected List<PlacementType> disabledPlacements = new ArrayList<>();
	protected boolean rotatable = true;

	protected boolean overrideTabComplete = false;

	public DecorationConfig() {
		allDecorationTypes.add(this);
	}

	public DecorationConfig(String name, @NotNull Material material, int modelId, Predicate<Integer> modelIdPredicate, CustomHitbox hitbox) {
		this();
		this.id = name.toLowerCase().replaceAll(" ", "_");
		this.name = name;
		this.material = material;
		this.modelId = modelId;
		this.modelIdPredicate = modelIdPredicate;
		this.hitboxes = hitbox.getHitboxes();

		if (this.isMultiBlock()) {
			this.rotationSnap = RotationSnap.DEGREE_90;
			this.rotatable = false;
		}
	}

	public DecorationConfig(String name, CustomMaterial material) {
		this(name, material, Basic.NONE);
	}

	public DecorationConfig(String name, @NonNull CustomMaterial customMaterial, CustomHitbox hitbox) {
		this(name, customMaterial.getMaterial(), customMaterial.getModelId(), modelId -> modelId == customMaterial.getModelId(), hitbox);
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
			if (type.name().equalsIgnoreCase(id))
				return type.getConfig();
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
		ItemStack item1 = getItem();

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

	public ItemBuilder getItemBuilder() {
		ItemBuilder itemBuilder = new ItemBuilder(material)
			.modelId(modelId)
			.name(name)
			.lore(lore)
			.itemFlags(ItemBuilder.ItemFlags.HIDE_ALL)
			.updateDecorationLore(true);

		if (this instanceof Dyeable dyeable) {
			itemBuilder.dyeColor(dyeable.getColor());
		}

		return itemBuilder;
	}

	public ItemStack getItem() {
		return getItemBuilder().build();
	}

	public @Nullable ItemStack getCatalogItem(Player viewer) {
		Double price = getCatalogPrice();
		if (price == null)
			return null;

		if (DecorationUtils.hasBypass(viewer))
			price = 0d;

		return getItemBuilder().lore("", "&3Price: &a" + DecorationUtils.prettyMoney(price)).build();
	}

	public Double getCatalogPrice() {
		DecorationType type = DecorationType.of(this);
		if (type == null)
			return null;

		TypeConfig typeConfig = type.getTypeConfig();
		if (typeConfig == null || typeConfig.price() == -1)
			return null;

		return typeConfig.price();
	}

	public boolean isMultiBlock() {
		return this.getClass().getAnnotation(MultiBlock.class) != null;
	}

	public boolean isAddition() {
		return this.getClass().getAnnotation(Addition.class) != null;
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

			ItemFrameRotation newFrameRotation = ItemFrameRotation.of(rotated);
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

		// Skip validation if hitbox is just air
		boolean isAir = true;
		for (Hitbox hitbox : getHitboxes()) {
			if (!MaterialTag.ALL_AIR.isTagged(hitbox.getMaterial())) {
				isAir = false;
				break;
			}
		}

		if (isAir)
			return true;
		//


		debug(debugger, "Frame Rotation: " + frameRotation + " | BlockFace: " + blockFace);

		List<Hitbox> hitboxes = Hitbox.rotateHitboxes(this, blockFace);
		for (Hitbox hitbox : hitboxes) {
			Block block = hitbox.getOffsetBlock(origin);
			if (!MaterialTag.ALL_AIR.isTagged(block) && !block.getType().equals(Material.WATER)) {
				debug(debugger, "- rotated hitbox found non-air");
				return false;
			}
		}

		return true;
	}

	public boolean isValidRotation(ItemFrameRotation frameRotation) {
		if (rotationSnap == RotationSnap.BOTH)
			return true;

		return rotationSnap.contains(frameRotation);
	}

	//

	public boolean place(Player player, Block block, BlockFace clickedFace, ItemStack item) {
		return place(player, block, clickedFace, item, null, false);
	}

	public boolean place(Player player, Block block, BlockFace clickedFace, ItemStack item, ItemFrameRotation rotationOverride, boolean override) {
		if (!override) { // Extra checks for placing decorations with unique restrictions
			if (isAddition()) {
				error(player, DecorationUtils.getPrefix() + "&cYou cannot place this decoration");
				return false;
			}
		}


		final Decoration decoration = new Decoration(this, null);
		debug(player, "validating placement...");
		if (!isValidPlacement(block, clickedFace, player)) {
			debug(player, "- invalid placement");
			return false;
		}

		Location origin = block.getRelative(clickedFace).getLocation().clone();

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

		if (rotationOverride != null)
			frameRotation = rotationOverride;

		if (frameRotation == null) {
			debug(player, "- couldn't find a valid frame rotation");
			return false;
		}
		//


		if (clickedFace == BlockFace.DOWN) { // Ceiling changes
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

		ItemStack finalItem = getFrameItem(player, prePlaceEvent.getItem());
		ItemUtils.subtract(player, item);

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
		decoration.setItemFrame(itemFrame);

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

	public ItemStack getFrameItem(Player player, ItemStack itemStack) {
		ItemBuilder itemCopy = ItemBuilder.oneOf(itemStack);

		String itemName = itemCopy.name();
		debug(player, "ItemName: " + itemName);
		return itemCopy
				.nbt(nbt -> nbt.setString(NBT_OWNER_KEY, player.getUniqueId().toString()))
				.nbt(nbt -> nbt.setString(NBT_DECOR_NAME, itemName))
				.resetName()
				.build();
	}

	public void sendInfo(Player player) {
		String enumName = "null";

		DecorationType decorationType = DecorationType.of(this);
		if (decorationType != null)
			enumName = decorationType.name();

		sendLine(5);
		send(player, "&3Name: &e" + this.getName());
		send(player, "&3Id: &e" + this.getId());
		send(player, "&3Enum: &e" + enumName);
		send(player, "&3Material: &e" + gg.projecteden.api.common.utils.StringUtils.camelCase(this.getMaterial()));
		send(player, "&3Model Id: &e" + this.getModelId());
		send(player, "&3Lore: &f[" + String.join(", ", this.getLore()) + "&f]");
		sendLine(player);

		send(player, "&3Place Sound: &e" + this.getPlaceSound());
		send(player, "&3Hit Sound: &e" + this.getHitSound());
		send(player, "&3Break Sound: &e" + this.getBreakSound());
		sendLine(player);

		send(player, "&3Rotation Type: &e" + this.getRotationSnap());
		send(player, "&3Disabled Placements: &e" + this.getDisabledPlacements());
		send(player, "&3Rotatable: &e" + this.isRotatable());
		sendLine(player);

		send(player, "&3Inherited Classes:");
		for (String clazz : DecorationUtils.getInstancesOf(this)) {
			send(player, " &e- " + clazz);
		}
		sendLine(player);

		send(player, "&3Hitboxes: ");
		for (Hitbox hitbox : this.getHitboxes()) {
			String material = gg.projecteden.api.common.utils.StringUtils.camelCase(hitbox.getMaterial());

			String hitboxType = " &e- " + material;
			if (hitbox.getMaterial() == Material.LIGHT)
				hitboxType += "&3, Level: &e" + hitbox.getLightLevel();

			hitboxType += " &3-> ";
			if (hitbox.getOffsets().isEmpty()) {
				hitboxType += "&eOrigin";
			} else {
				String offsets = "&3[&e";
				for (BlockFace blockFace : hitbox.getOffsets().keySet()) {
					offsets += "&e" + gg.projecteden.api.common.utils.StringUtils.camelCase(blockFace) + "&3, &e" + hitbox.getOffsets().get(blockFace) + "&3, ";
				}

				hitboxType += offsets.substring(0, (offsets.length() - 2)) + "&3]";
			}

			send(player, hitboxType);

		}
		sendLine(player);
	}
}
