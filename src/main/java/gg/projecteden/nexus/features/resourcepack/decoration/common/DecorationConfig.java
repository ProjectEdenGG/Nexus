package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPlaceEvent;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/* TODO: Implement "Structure" type:
	- Allows for the use of decorations that require more than 1 itemframe, such as:
		- the pugmas train
		- the pugmas air balloon
		- more customizable chairs (dyeable wood + dyeable cushion)

 */
@Data
public class DecorationConfig {
	public static final String NBT_OWNER_KEY = "DecorationOwner";
	protected String id;
	protected String name;
	protected @NonNull Material material = Material.PAPER;
	protected int modelId;
	protected Predicate<Integer> modelIdPredicate;
	protected String placeSound = Sound.ENTITY_ITEM_FRAME_ADD_ITEM.getKey().getKey();
	protected String hitSound = Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM.getKey().getKey();
	protected String breakSound = Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM.getKey().getKey();
	protected List<String> lore = Collections.singletonList("Decoration");

	protected List<Hitbox> hitboxes = Hitbox.NONE();
	protected RotationType rotationType = RotationType.BOTH;
	protected List<PlacementType> disabledPlacements = new ArrayList<>();

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

		if (this.isMultiBlock())
			this.rotationType = RotationType.DEGREE_90;
	}

	public DecorationConfig(String name, @NotNull CustomMaterial material, List<Hitbox> hitboxes) {
		this(name, material.getMaterial(), material.getModelId(), modelId -> modelId == material.getModelId(), hitboxes);
	}

	public DecorationConfig(String name, CustomMaterial material) {
		this(name, material, Hitbox.NONE());
	}

	@Getter
	private static final List<DecorationConfig> allDecorationTypes = new ArrayList<>();

	public static DecorationConfig of(ItemStack tool) {
		if (Nullables.isNullOrAir(tool))
			return null;

		for (DecorationConfig decoration : allDecorationTypes)
			if (decoration.isFuzzyMatch(tool))
				return decoration;

		return null;
	}

	public static DecorationConfig of(String id) {
		for (DecorationConfig decoration : allDecorationTypes)
			if (decoration.getId().equalsIgnoreCase(id))
				return decoration;

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

	public boolean isStructure() {
		return this.getClass().getAnnotation(Structure.class) != null;
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

		ItemBuilder itemCopy = ItemBuilder.oneOf(item);
		ItemUtils.subtract(player, item);

		block.getWorld().spawn(origin, ItemFrame.class, itemFrame -> {
			itemFrame.customName(null);
			itemFrame.setCustomNameVisible(false);
			itemFrame.setFacingDirection(clickedFace, true);
			itemFrame.setRotation(frameRotation.getRotation());
			itemFrame.setVisible(false);
			itemFrame.setGlowing(false);
			itemFrame.setSilent(true);
			itemFrame.setItem(itemCopy.nbt(nbt -> nbt.setString(NBT_OWNER_KEY, player.getUniqueId().toString())).build(), false);
		});

		Hitbox.place(getHitboxes(), origin, frameRotation.getBlockFace());

		new SoundBuilder(hitSound).location(origin).play();

		return true;
	}
}
