package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.ReflectionUtils;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity.ClientSideEntityType;
import gg.projecteden.nexus.features.equipment.skins.ToolSkin;
import gg.projecteden.nexus.features.recipes.functionals.DiamondTotemOfUndying;
import gg.projecteden.nexus.features.recipes.functionals.Glue;
import gg.projecteden.nexus.features.recipes.functionals.InvisibleItemFrame;
import gg.projecteden.nexus.features.recipes.functionals.Proportionator;
import gg.projecteden.nexus.features.recipes.functionals.RepairCostDiminisher;
import gg.projecteden.nexus.features.recipes.functionals.WardCharm;
import gg.projecteden.nexus.features.resourcepack.CustomContentUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog.Tab;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog.Theme;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.Backpack;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.titan.models.CustomCreativeItem;
import gg.projecteden.nexus.features.workbenches.ToolModificationTable;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice.MineralChoice;
import gg.projecteden.nexus.features.workbenches.dyestation.CreativeBrushMenu;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStationMenu;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"deprecation", "removal"})
public class DecorationUtils {
	@Getter
	private static final List<BlockFace> cardinalFaces = List.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

	public static void dye(ItemStack item, ChatColor color, Player debugger) {
		Colored.of(color).apply(item);
		updateLore(item, debugger);
	}

	public static void dye(ItemStack item, ColorChoice colorChoice, Player debugger) {
		colorChoice.apply(item);
		updateLore(item, debugger);
	}

	public static ItemStack updateLore(ItemStack item, Player debugger) {
		ItemBuilder resultBuilder = new ItemBuilder(item);
		if (!resultBuilder.isDyeable())
			return item;

		Color color = resultBuilder.dyeColor();
		if (color == null)
			return item;

		String colorHex = StringUtils.toHex(color);
		String colorName = colorHex;
		boolean isStain = false;
		boolean isMineral = false;
		for (StainChoice stainChoice : StainChoice.values()) {
			if (stainChoice.getColor().equals(color)) {
				isStain = true;
				colorName = StringUtils.camelCase(stainChoice.name());
				break;
			}
		}

		if (!isStain) {
			for (MineralChoice metallicChoice : MineralChoice.values()) {
				if (metallicChoice.getColor().equals(color)) {
					isMineral = true;
					colorName = StringUtils.camelCase(metallicChoice.name());
					break;
				}
			}
		}

		DecorationLang.debug(debugger, "Color Name: " + colorName);

		boolean isPaintbrush = Objects.equals(resultBuilder.model(), ItemModelType.PAINTBRUSH.getModel());
		boolean handledPaintbrushUses = false;

		List<String> finalLore = new ArrayList<>();

		// Change lore
		List<String> newLore = new ArrayList<>();
		for (String line : resultBuilder.getLore()) {
			String _line = StringUtils.stripColor(line);
			// remove color line
			if (_line.contains("Color: "))
				continue;
			if (_line.contains("Stain: "))
				continue;
			if (_line.contains("Mineral: "))
				continue;

			// reset uses
			if (isPaintbrush && _line.contains(StringUtils.stripColor(DyeStation.USES_LORE))) {
				newLore.add(DyeStation.USES_LORE + DyeStation.MAX_USES_PAINTBRUSH);
				handledPaintbrushUses = true;
				continue;
			}

			newLore.add(line);
		}

		// add uses if missing
		if (isPaintbrush && !handledPaintbrushUses) {
			newLore.add(DyeStation.USES_LORE + DyeStation.MAX_USES_PAINTBRUSH);
		}

		// Add color line
		String colorLine = "&3Color: &";
		if (isStain)
			colorLine = "&3Stain: &";
		else if (isMineral)
			colorLine = "&3Mineral: &";

		finalLore.add(colorLine + colorHex + colorName);
		DecorationLang.debug(debugger, "Adding color line: " + colorLine + colorHex + colorName);

		finalLore.addAll(newLore);

		resultBuilder.setLore(finalLore);

		ItemStack result = resultBuilder.build();
		item.setItemMeta(result.getItemMeta());

		List<String> lore = item.getLore();
		if (lore == null)
			lore = new ArrayList<>();

		DecorationLang.debug(debugger, new JsonBuilder("[Item Lore]").hover(lore));

		return item;
	}

	@Getter
	public static final List<BlockFace> directions = List.of(
			BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST,
			BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST
	);

	public static BlockFace rotateClockwise(BlockFace blockFace) {
		int size = directions.size() - 1;
		int index = (directions.indexOf(blockFace) + 1);

		if (index > size)
			index = 0;

		return directions.get(index);
	}

	public static Set<Class<?>> getInstancesOf(DecorationConfig config) {
		Set<Class<?>> classes = new HashSet<>();
		for (Class<? extends DecorationConfig> clazz : ReflectionUtils.superclassesOf(config.getClass())) {
			classes.add(clazz);
			classes.addAll(List.of(clazz.getInterfaces()));
		}
		return classes;
	}

	public static List<String> getSimpleNameInstancesOf(DecorationConfig config) {
		Set<Class<?>> classes = getInstancesOf(config);

		List<String> result = new ArrayList<>();
		for (Class<?> clazz : classes)
			result.add(clazz.getSimpleName());

		return result;
	}

	@Nullable
	public static Object getItemFrame(Block clicked, int radius, BlockFace blockFaceOverride, Player debugger, boolean isClientside) {
		if (Nullables.isNullOrAir(clicked))
			return null;

		Location location = clicked.getLocation().toCenterLocation();
		if (!hasNearbyItemFrames(location, radius, isClientside))
			return null;

		Set<Material> hitboxTypes = DecorationConfig.getHitboxTypes();
		if (!hitboxTypes.contains(clicked.getType()))
			return null;

		HitboxMaze maze = new HitboxMaze(debugger, clicked, radius);

		Object itemFrame = findNearbyItemFrame(location, isClientside, debugger);

		if (itemFrame != null) {
			DecorationLang.debug(debugger, "Single");
			return findItemFrame(maze, clicked.getLocation(), blockFaceOverride, debugger, isClientside);
		}

		DecorationLang.debug(debugger, "Maze Search");
		return getConnectedHitboxes(maze, blockFaceOverride, debugger, isClientside);
	}

	// It isn't pretty, but it works
	// TODO: optimize by checking nearest neighbors first
	private static @Nullable Object getConnectedHitboxes(HitboxMaze maze, BlockFace blockFaceOverride, Player debugger, boolean isClientside) {
		if (maze.getTries() > 10) {
			DecorationLang.deepDebug(debugger, "&cMaze Tries > 10");
			return null;
		}

		if (maze.getDirectionsLeft().isEmpty()) {
			Location newLoc = maze.getBlock().getLocation();

			if (newLoc.equals(maze.getOrigin().getLocation())) {
				maze.debugDot(newLoc, ColorType.ORANGE);
				DecorationLang.deepDebug(debugger, "&6Maze returned to origin");
				return null;
			}

			maze.goBack();
			maze.debugDot(newLoc, ColorType.RED);

			maze.incTries();
			return getConnectedHitboxes(maze, blockFaceOverride, debugger, isClientside);
		}

		maze.nextDirection();
		maze.setTries(0);

		Block previousBlock = maze.getBlock();
		maze.setBlock(previousBlock.getRelative(maze.getBlockFace()));

		Block currentBlock = maze.getBlock();
		Location currentLoc = currentBlock.getLocation().clone();
		Material currentType = currentBlock.getType();

		Distance distance = Distance.distance(maze.getOrigin(), currentLoc);
		Set<Material> hitboxTypes = DecorationConfig.getHitboxTypes();
		if (maze.getTried().contains(currentLoc) || !hitboxTypes.contains(currentType) || distance.gt(6)) {
			maze.setBlock(previousBlock);
			return getConnectedHitboxes(maze, blockFaceOverride, debugger, isClientside);
		}

		maze.getTried().add(currentLoc);
		maze.addToPath(previousBlock.getLocation(), maze.getDirectionsLeft());

		// Is correct item frame?
		Object itemFrame = findItemFrame(maze, currentLoc, blockFaceOverride, debugger, isClientside);
		if (itemFrame != null) {
			DecorationLang.debug(debugger, "  &aMaze found item frame\n");
			return itemFrame;
		}

		// Keep looking
		maze.resetDirections();
		maze.addToPath(currentLoc, maze.getDirectionsLeft());
		maze.debugDot(currentLoc, ColorType.BLACK);
		return getConnectedHitboxes(maze, blockFaceOverride, debugger, isClientside);
	}

	private static @Nullable Object findItemFrame(@NonNull HitboxMaze maze, @NonNull Location currentLoc, BlockFace blockFaceOverride, Player debugger, boolean isClientside) {
		ItemStack itemStack;
		BlockFace blockFace;

		Object itemFrame = findNearbyItemFrame(currentLoc, isClientside, debugger);
		if (itemFrame == null) {
			DecorationLang.deepDebug(debugger, "&6- no item frames found nearby");
			return null;
		}

		if (isClientside) {
			ClientSideItemFrame _itemFrame = (ClientSideItemFrame) itemFrame;

			blockFace = ItemFrameRotation.of(_itemFrame).getBlockFace();
			itemStack = _itemFrame.content();
		} else {
			ItemFrame _itemFrame = (ItemFrame) itemFrame;

			blockFace = ItemFrameRotation.of(_itemFrame).getBlockFace();
			itemStack = _itemFrame.getItem();
		}

		if (Nullables.isNullOrAir(itemStack)) {
			DecorationLang.deepDebug(debugger, "&6- item frame is empty");
			return null;
		}

		DecorationConfig config = DecorationConfig.of(itemStack);
		if (config == null) {
			DecorationLang.deepDebug(debugger, "&6- item frame does not have decoration");
			return null;
		}

		DecorationLang.deepDebug(debugger, "&eHitbox BlockFace: " + blockFace);
		if (config.isMultiBlockWallThing() && blockFaceOverride != null) {
			blockFace = blockFaceOverride;
			DecorationLang.debug(debugger, "&eBlockFace Override 1: " + blockFace);
		}

		List<Hitbox> hitboxes = Hitbox.rotateHitboxes(config, blockFace);

		Location originLoc = maze.getOrigin().getLocation();

		for (Hitbox hitbox : hitboxes) {
			Block _block = hitbox.getOffsetBlock(currentLoc);
			Location _blockLoc = _block.getLocation();

			maze.debugDot(_blockLoc, ColorType.CYAN);

			if (LocationUtils.isFuzzyEqual(_blockLoc, originLoc)) {
				DecorationLang.deepDebug(debugger, "&aorigin is in hitbox, returning item frame");
				return itemFrame;
			}
		}

		DecorationLang.deepDebug(debugger, "&6- origin isn't in hitbox");
		return null;
	}

	private static boolean hasNearbyItemFrames(Location location, double radius, boolean isClientside) {
		if (isClientside)
			return !ClientSideConfig.getEntities(location, radius).isEmpty();

		return !location.getNearbyEntitiesByType(ItemFrame.class, radius).isEmpty();
	}

	public static @Nullable Object findNearbyItemFrame(Location location, boolean isClientside, Player debugger) {
		Location _location = location.toCenterLocation();
		double _radius = 0.5;

		if (isClientside)
			return ClientSideConfig.getEntities(_location, ClientSideEntityType.ITEM_FRAME, _radius).stream().findFirst().orElse(null);

		return _location.getNearbyEntitiesByType(ItemFrame.class, _radius).stream().findFirst().orElse(null);
	}

	public static boolean hasBypass(Player player) {
		return CustomContentUtils.hasBypass(player);
	}

	public static boolean canUsePaintbrush(Player player, ItemStack tool) {
		if (!DyeStation.isPaintbrush(tool))
			return false;

		DecorationLang.debug(player, "Can use paintbrush?");

		int usesLeft = DyeStationMenu.getUses(tool);
		if (usesLeft <= 0) {
			DecorationLang.debug(player, "&c<- no more uses");
			return false;
		}

		DecorationLang.debug(player, "&ayes");
		return true;
	}

	public static boolean isSameColor(ItemStack paintbrush, ItemStack thing) {
		Color color = new ItemBuilder(thing).dyeColor();
		if (color == null)
			return false;

		return isSameColor(paintbrush, color);
	}

	public static boolean isSameColor(ItemStack paintbrush, SignSide signSide) {
		DyeColor signColor = signSide.getColor();
		if (signColor == null)
			return false;

		DyeColor brushColor = ColorType.ofClosest(new ItemBuilder(paintbrush).dyeColor()).getDyeColor();
		if (brushColor == null)
			return false;

		return brushColor.equals(signColor);
	}

	public static boolean isSameColor(ItemStack paintbrush, Color color) {
		Color paintbrushColor = new ItemBuilder(paintbrush).dyeColor();
		return paintbrushColor.equals(color);
	}

	public static void usePaintbrush(Player player, ItemStack tool) {
		getSoundBuilder(CustomSound.DECOR_PAINT).category(SoundCategory.PLAYERS).location(player.getLocation()).play();

		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		ItemBuilder toolBuilder = new ItemBuilder(tool);
		ItemBuilder toolResult = DyeStationMenu.decreaseUses(toolBuilder);
		tool.setItemMeta(toolResult.build().getItemMeta());
	}

	public static SoundBuilder getSoundBuilder(CustomSound sound) {
		return getSoundBuilder(sound.getPath());
	}

	public static SoundBuilder getSoundBuilder(Sound sound) {
		return getSoundBuilder(sound.getKey().getKey());
	}

	public static SoundBuilder getSoundBuilder(String sound) {
		return new SoundBuilder(sound).category(SoundCategory.BLOCKS);
	}

	public static @Nullable DecorationConfig getTargetConfig(Player player) {
		ItemStack targetItemStack = DecorationStoreUtils.getTargetEntityItem(DecorationStoreUtils.getTargetEntity(player));
		if (Nullables.isNullOrAir(targetItemStack))
			return null;

		return DecorationConfig.of(targetItemStack);
	}

	public static Decoration getTargetDecoration(Player player) {
		Entity targetEntity = DecorationStoreUtils.getTargetEntity(player);
		if (targetEntity == null)
			return null;

		if (!(targetEntity instanceof ItemFrame itemFrame))
			return null;

		DecorationConfig config = DecorationConfig.of(itemFrame);
		if (config == null)
			return null;

		return new Decoration(config, itemFrame);
	}

	public static CustomCreativeItem[] getCreativeCategories() {
		return Arrays.stream(Theme.values())
			.filter(theme -> theme != Theme.ALL)
			.map(theme -> {
				ItemBuilder builder = theme.getItemBuilder();
				return new CustomCreativeItem(builder, "Decorations: " + StringUtils.camelCase(theme));
			})
			.toArray(CustomCreativeItem[]::new);
	}

	public static CustomCreativeItem[] getCreativeItems() {
		List<CustomCreativeItem> items = new ArrayList<>();
		items.addAll(Arrays.stream(DecorationType.values())
			.filter(decorationType -> Arrays.stream(decorationType.getTypeConfig().tabs()).noneMatch(tab -> tab == Tab.INTERNAL))
			.map(CustomCreativeItem::new)
			.toList());

		items.add(new CustomCreativeItem(ToolModificationTable.getWorkbench(), "Project Eden"));
		items.add(new CustomCreativeItem(DyeStation.getWorkbench(), "Project Eden"));
		items.add(new CustomCreativeItem(DyeStation.getPaintbrush(), "Project Eden"));
		items.add(new CustomCreativeItem(DyeStation.getMagicDye(), "Project Eden"));
		items.add(new CustomCreativeItem(DyeStation.getMagicMineral(), "Project Eden"));
		items.add(new CustomCreativeItem(DyeStation.getMagicStain(), "Project Eden"));
		items.add(new CustomCreativeItem(DiamondTotemOfUndying.getItem(), "Project Eden"));
		items.add(new CustomCreativeItem(RepairCostDiminisher.getItem(), "Project Eden"));
		items.add(new CustomCreativeItem(WardCharm.getItem(), "Project Eden"));
		items.add(new CustomCreativeItem(Glue.ITEM, "Project Eden"));
		items.add(new CustomCreativeItem(InvisibleItemFrame.getItem(), "Project Eden"));
		items.add(new CustomCreativeItem(CreativeBrushMenu.getCreativeBrush(), "Project Eden"));
		items.add(new CustomCreativeItem(new ItemBuilder(Material.LIGHT), "Project Eden"));
		items.add(new CustomCreativeItem(Proportionator.ITEM, "Project Eden"));

		for (ToolSkin toolSkin : EnumUtils.valuesExcept(ToolSkin.class, ToolSkin.DEFAULT))
			items.add(new CustomCreativeItem(toolSkin.getTemplate(), "Project Eden"));

		for (DecorationConfig config : DecorationConfig.getALL_DECOR_CONFIGS())
			if (config instanceof Backpack)
				items.add(new CustomCreativeItem(config.getItem(), "Project Eden"));

		return items.toArray(CustomCreativeItem[]::new);
	}
}
