package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationTagType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.HasVirtualInventory;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Structure;
import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreCurrencyType;
import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableWallThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
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
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

@Data
public class DecorationConfig {
	protected String id;
	protected String name;
	protected @NonNull Material material = Material.PAPER;
	protected String model;
	protected Predicate<String> modelPredicate;
	protected String placeSound = "entity.item_frame.add_item";
	protected String hitSound = "entity.item_frame.rotate_item";
	protected String breakSound = "entity.item_frame.remove_item";
	protected List<String> lore = new ArrayList<>();

	protected List<Hitbox> hitboxes = Hitbox.NONE();
	protected RotationSnap rotationSnap = RotationSnap.BOTH;
	protected List<PlacementType> disabledPlacements = new ArrayList<>();
	protected boolean rotatable = false;
	protected boolean multiBlock = false;
	protected boolean exclusive = false;

	protected boolean overrideTabComplete = false;

	public DecorationConfig() {
		ALL_DECOR_CONFIGS.add(this);
	}

	public DecorationConfig(boolean exclusive, boolean multiBlock, String name, @NotNull Material material, String model, Predicate<String> modelIdPredicate, CustomHitbox hitbox) {
		this();
		this.exclusive = exclusive;
		this.multiBlock = multiBlock;
		this.id = name.toLowerCase().replaceAll(" ", "_");
		this.name = name;
		this.material = material;
		this.model = model;
		this.modelPredicate = modelIdPredicate;
		this.hitboxes = hitbox.getHitboxes();

		if (this.isMultiBlock()) {
			this.rotationSnap = RotationSnap.DEGREE_90;
			this.rotatable = false;
		}

		if (this.hitboxes.size() == 1 && this.hitboxes.getFirst().getMaterial() != Material.BARRIER)
			this.rotatable = true;

		DecorationTagType.setLore(this);
	}

	public DecorationConfig(boolean multiBlock, String name, @NonNull ItemModelType itemModelType, CustomHitbox hitbox) {
		this(false, multiBlock, name, itemModelType.getMaterial(), itemModelType.getModel(), model -> Objects.equals(model, itemModelType.getModel()), hitbox);
	}

	public DecorationConfig(boolean multiBlock, String name, ItemModelType itemModelType) {
		this(multiBlock, name, itemModelType, HitboxSingle.NONE);
	}

	public DecorationConfig(boolean exclusive, boolean multiBlock, String name, @NonNull ItemModelType itemModelType, CustomHitbox hitbox) {
		this(exclusive, multiBlock, name, itemModelType.getMaterial(), itemModelType.getModel(), model -> Objects.equals(model, itemModelType.getModel()), hitbox);
	}

	public DecorationConfig(boolean exclusive, boolean multiBlock, String name, ItemModelType itemModelType) {
		this(exclusive, multiBlock, name, itemModelType, HitboxSingle.NONE);
	}

	@Getter
	private static final List<DecorationConfig> ALL_DECOR_CONFIGS = new ArrayList<>();

	public static @Nullable DecorationConfig of(ItemFrame itemFrame) {
		if (!itemFrame.isValid())
			return null;

		ItemStack item = itemFrame.getItem();
		if (Nullables.isNullOrAir(item))
			return null;

		return DecorationConfig.of(item);
	}

	public static DecorationConfig of(ItemStack itemStack) {
		if (Nullables.isNullOrAir(itemStack))
			return null;

		if (!ItemBuilder.Model.hasModel(itemStack))
			return null;

		for (DecorationConfig decoration : new ArrayList<>(ALL_DECOR_CONFIGS))
			if (decoration.isFuzzyMatch(itemStack))
				return decoration;

		return null;
	}

	public static DecorationConfig of(String id) {
		for (DecorationConfig config : new ArrayList<>(ALL_DECOR_CONFIGS)) {
			if (config.getId().equalsIgnoreCase(id))
				return config;
		}

		for (DecorationType type : DecorationType.values()) {
			if (type.name().equalsIgnoreCase(id))
				return type.getConfig();
		}

		return null;
	}

	public static DecorationConfig of(ItemModelType itemModelType) {
		for (DecorationConfig config : ALL_DECOR_CONFIGS)
			if (config.getMaterial() == itemModelType.getMaterial() && config.getModel().equals(itemModelType.getModel()))
				return config;

		return null;
	}

	public boolean isFuzzyMatch(ItemStack item2) {
		ItemStack item1 = getItem();

		if (item2 == null)
			return false;

		if (!item1.getType().equals(item2.getType()))
			return false;

		String decorModelData = Model.of(item1);
		String itemModelData = Model.of(item2);

		if (modelPredicate != null)
			return modelPredicate.test(itemModelData);
		else
			return Objects.equals(decorModelData, itemModelData);
	}

	private static final Set<Material> hitboxTypes = new HashSet<>();

	public static Set<Material> getHitboxTypes() {
		if (!hitboxTypes.isEmpty())
			return hitboxTypes;

		ALL_DECOR_CONFIGS.forEach(decorationType ->
			hitboxTypes.addAll(decorationType.getHitboxes()
				.stream()
				.map(Hitbox::getMaterial)
				.filter(material -> !MaterialTag.ALL_AIR.isTagged(material))
				.toList()));

		return hitboxTypes;
	}

	public ItemBuilder getItemBuilder() {
		ItemBuilder itemBuilder = new ItemBuilder(material)
			.model(getModel())
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

	public @Nullable ItemStack getPricedCatalogItem(Player viewer, DecorationStoreCurrencyType currency, DecorationStoreType storeType) {
		return currency.getPricedCatalogItem(viewer, this, storeType);
	}

	public Integer getCatalogPrice(DecorationStoreType storeType) {
		return storeType.getCurrency().getPriceDecor(this, storeType);
	}

	public boolean isMultiBlockWallThing() {
		return isMultiBlock() && isWallThing();
	}

	public boolean isWallThing() {
		return this instanceof WallThing || this instanceof DyeableWallThing || !this.disabledPlacements.contains(PlacementType.WALL);
	}

	public boolean isStructure() {
		return this.getClass().getAnnotation(Structure.class) != null;
	}

	public boolean hasInventory() {
		return this.getClass().getAnnotation(HasVirtualInventory.class) != null;
	}

	public boolean shouldInteract(ItemStack tool) {
		return false;
	}

	// validation

	boolean isValidPlacement(Block block, BlockFace clickedFace, Player debugger) {
		for (PlacementType placementType : disabledPlacements) {
			if (placementType.getBlockFaces().contains(clickedFace)) {
				DecorationLang.debug(debugger, "denied placement type");
				return false;
			}
		}

		Block placed = block.getRelative(clickedFace);
		List<ItemFrame> itemFrames = new ArrayList<>(placed.getLocation().toCenterLocation().getNearbyEntitiesByType(ItemFrame.class, 0.5));

		for (ItemFrame itemFrame : itemFrames) {
			DecorationLang.debug(debugger, "loc = " + StringUtils.xyzw(itemFrame.getLocation()));
			DecorationLang.debug(debugger, "attached face = " + itemFrame.getAttachedFace() + " -> " + itemFrame.getAttachedFace().getOppositeFace());
			if (itemFrame.getAttachedFace().getOppositeFace() == clickedFace) {
				DecorationLang.debug(debugger, "itemframe exists in location, face = " + clickedFace);
				return false;
			}
		}

		return true;
	}

	@Nullable
	protected Utils.ItemFrameRotation findValidFrameRotation(Location origin, ItemFrameRotation frameRotation, Player debugger) {
		if (isValidLocation(origin, frameRotation, debugger)) {
			DecorationLang.debug(debugger, "is valid rotation: " + frameRotation);
			return frameRotation;
		}

		BlockFace rotated = frameRotation.getBlockFace();
		for (int tries = 0; tries < DecorationUtils.getDirections().size(); tries++) {
			rotated = DecorationUtils.rotateClockwise(rotated);

			ItemFrameRotation newFrameRotation = ItemFrameRotation.of(rotated);
			if (isValidLocation(origin, newFrameRotation, debugger)) {
				DecorationLang.debug(debugger, "found valid rotation: " + newFrameRotation);
				return newFrameRotation;
			}
		}

		DecorationLang.debug(debugger, "couldn't find a valid rotation");
		return null;
	}

	private boolean isValidLocation(Location origin, ItemFrameRotation frameRotation, Player debugger) {
		return isValidLocation(origin, frameRotation, true, debugger);
	}

	private boolean isValidLocation(Location origin, ItemFrameRotation frameRotation, boolean validateRotation, Player debugger) {
		return isValidLocation(origin, frameRotation, frameRotation.getBlockFace(), validateRotation, debugger);
	}

	boolean isValidLocation(Location origin, ItemFrameRotation frameRotation, BlockFace blockFace, boolean validateRotation, Player debugger) {
		if (validateRotation) {
			if (!isValidRotation(frameRotation)) {
				DecorationLang.debug(debugger, "- invalid rotation: " + frameRotation);
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


		DecorationLang.debug(debugger, "Frame Rotation: " + frameRotation + " | BlockFace: " + blockFace);

		List<Hitbox> hitboxes = Hitbox.rotateHitboxes(this, blockFace);
		for (Hitbox hitbox : hitboxes) {
			Block block = hitbox.getOffsetBlock(origin);
			if (!MaterialTag.ALL_AIR.isTagged(block) && !block.getType().equals(Material.WATER)) {
				DecorationLang.debug(debugger, "- rotated hitbox found non-air");
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

	public ItemStack getFrameItem(ItemBuilder itemBuilder) {
		return itemBuilder.resetName().build();
	}

	public void sendInfo(Player player) {
		String enumName = "null";

		DecorationType decorationType = DecorationType.of(this);
		if (decorationType != null)
			enumName = decorationType.name();


		PlayerUtils.sendLine(5);
		PlayerUtils.send(player, "&3Name: &e" + this.getName());
		PlayerUtils.send(player, "&3Id: &e" + this.getId());
		PlayerUtils.send(player, "&3Enum: &e" + enumName);

		Integer priceMoney = DecorationStoreCurrencyType.MONEY.getPriceDecor(this, DecorationStoreType.CATALOG);
		PlayerUtils.send(player, "&3Price: &e" + (priceMoney == null ? "Unbuyable" : priceMoney));
		Integer priceTokens = DecorationStoreCurrencyType.TOKENS.getPriceDecor(this, DecorationStoreType.CATALOG);
		PlayerUtils.send(player, "&3Tokens: &e" + (priceTokens == null ? "Unbuyable" : priceTokens));

		PlayerUtils.send(player, "&3Material: &e" + gg.projecteden.api.common.utils.StringUtils.camelCase(this.getMaterial()));
		PlayerUtils.send(player, "&3Model: &e" + this.getModel());
		PlayerUtils.send(player, "&3Lore: &f[" + String.join(",", this.getLore()) + "&f]");
		PlayerUtils.sendLine(player);

		PlayerUtils.send(player, "&3Place Sound: &e" + this.getPlaceSound());
		PlayerUtils.send(player, "&3Hit Sound: &e" + this.getHitSound());
		PlayerUtils.send(player, "&3Break Sound: &e" + this.getBreakSound());
		PlayerUtils.sendLine(player);

		PlayerUtils.send(player, "&3Rotation Type: &e" + this.getRotationSnap());
		PlayerUtils.send(player, "&3Disabled Placements: &e" + this.getDisabledPlacements());
		PlayerUtils.send(player, "&3Rotatable: &e" + this.isRotatable());
		PlayerUtils.sendLine(player);

		PlayerUtils.send(player, "&3Inherited Classes:");
		for (String clazz : DecorationUtils.getSimpleNameInstancesOf(this)) {
			PlayerUtils.send(player, " &e- " + clazz);
		}
		PlayerUtils.sendLine(player);

		PlayerUtils.send(player, "&3Hitboxes: ");
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

			PlayerUtils.send(player, hitboxType);

		}
		PlayerUtils.sendLine(player);
	}
}
