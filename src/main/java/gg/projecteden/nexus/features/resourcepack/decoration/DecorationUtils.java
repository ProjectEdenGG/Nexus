package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.api.common.utils.ReflectionUtils;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity.ClientSideEntityType;
import gg.projecteden.nexus.features.resourcepack.CustomContentUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationError;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.Backpack;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.PlayerPlushie;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStationMenu;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.ItemBuilder;
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
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gg.projecteden.nexus.utils.Distance.distance;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@SuppressWarnings("deprecation")
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
		for (ColorChoice.StainChoice stainChoice : ColorChoice.StainChoice.values()) {
			if (stainChoice.getColor().equals(color)) {
				isStain = true;
				colorName = StringUtils.camelCase(stainChoice.name());
				break;
			}
		}

		DecorationLang.debug(debugger, "Color Name: " + colorName);

		boolean isPaintbrush = resultBuilder.modelId() == DyeStation.getPaintbrush().modelId();
		boolean handledPaintbrushUses = false;

		List<String> finalLore = new ArrayList<>();

		// Change lore
		List<String> newLore = new ArrayList<>();
		for (String line : resultBuilder.getLore()) {
			String _line = stripColor(line);
			// remove color line
			if (_line.contains("Color: "))
				continue;
			if (_line.contains("Stain: "))
				continue;

			// reset uses
			if (isPaintbrush && _line.contains(stripColor(DyeStation.USES_LORE))) {
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
		String colorLine = isStain ? "&3Stain: &" : "&3Color: &";

		finalLore.add(colorLine + colorHex + colorName);
		DecorationLang.debug(debugger, "Adding color line: " + colorLine + colorHex + colorName);

		finalLore.addAll(newLore);

		resultBuilder.setLore(finalLore);

		ItemStack result = resultBuilder.build();
		item.setItemMeta(result.getItemMeta());

		DecorationLang.debug(debugger, "Item lore: " + item.getLore());

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

	public static List<String> getInstancesOf(DecorationConfig config) {
		Set<Class<?>> classes = new HashSet<>();
		for (Class<? extends DecorationConfig> clazz : ReflectionUtils.superclassesOf(config.getClass())) {
			classes.add(clazz);
			classes.addAll(List.of(clazz.getInterfaces()));
		}

		List<String> result = new ArrayList<>();
		for (Class<?> clazz : classes)
			result.add(clazz.getSimpleName());

		return result;
	}

	@Nullable
	public static Object getItemFrame(Block clicked, int radius, BlockFace blockFaceOverride, Player debugger, boolean isClientside) {
		if (isNullOrAir(clicked))
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
			DecorationLang.debug(debugger, true, "&cMaze Tries > 10");
			return null;
		}

		if (maze.getDirectionsLeft().isEmpty()) {
			Location newLoc = maze.getBlock().getLocation();

			if (newLoc.equals(maze.getOrigin().getLocation())) {
				maze.debugDot(newLoc, Color.ORANGE);
				DecorationLang.debug(debugger, true, "&6Maze returned to origin");
				return null;
			}

			maze.goBack();
			maze.debugDot(newLoc, Color.RED);

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

		Distance distance = distance(maze.getOrigin(), currentLoc);
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
		maze.debugDot(currentLoc, Color.BLACK);
		return getConnectedHitboxes(maze, blockFaceOverride, debugger, isClientside);
	}

	private static @Nullable Object findItemFrame(@NonNull HitboxMaze maze, @NonNull Location currentLoc, BlockFace blockFaceOverride, Player debugger, boolean isClientside) {
		ItemStack itemStack;
		BlockFace blockFace;

		Object itemFrame = findNearbyItemFrame(currentLoc, isClientside, debugger);
		if (itemFrame == null) {
			DecorationLang.debug(debugger, true, "&6- no item frames found nearby");
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

		if (isNullOrAir(itemStack)) {
			DecorationLang.debug(debugger, true, "&6- item frame is empty");
			return null;
		}

		DecorationConfig config = DecorationConfig.of(itemStack);
		if (config == null) {
			DecorationLang.debug(debugger, true, "&6- item frame does not have decoration");
			return null;
		}

		DecorationLang.debug(debugger, true, "&eHitbox BlockFace: " + blockFace);
		if (config.isMultiBlockWallThing() && blockFaceOverride != null) {
			blockFace = blockFaceOverride;
			DecorationLang.debug(debugger, true, "&eBlockFace Override 1: " + blockFace);
		}

		List<Hitbox> hitboxes = Hitbox.rotateHitboxes(config, blockFace);

		Location originLoc = maze.getOrigin().getLocation();

		for (Hitbox hitbox : hitboxes) {
			Block _block = hitbox.getOffsetBlock(currentLoc);
			Location _blockLoc = _block.getLocation();

			maze.debugDot(_blockLoc, Color.TEAL);

			if (LocationUtils.isFuzzyEqual(_blockLoc, originLoc)) {
				DecorationLang.debug(debugger, true, "&aorigin is in hitbox, returning item frame");
				return itemFrame;
			}
		}

		DecorationLang.debug(debugger, true, "&6- origin isn't in hitbox");
		return null;
	}

	private static boolean hasNearbyItemFrames(Location location, double radius, boolean isClientside) {
		if (isClientside)
			return ClientSideConfig.getEntities(location, radius).size() > 0;

		return location.getNearbyEntitiesByType(ItemFrame.class, radius).size() > 0;
	}

	public static @Nullable Object findNearbyItemFrame(Location location, boolean isClientside, Player debugger) {
		Location _location = location.toCenterLocation();
		double _radius = 0.5;

		if (isClientside)
			return ClientSideConfig.getEntities(_location, ClientSideEntityType.ITEM_FRAME, _radius).stream().findFirst().orElse(null);

		return _location.getNearbyEntitiesByType(ItemFrame.class, _radius).stream().findFirst().orElse(null);
	}

	// TODO DECORATIONS - Remove on release
	private static final List<DecorationType> BYPASS_LIST = List.of(
			DecorationType.ENCHANTED_BOOK_SPLITTER,
			DecorationType.BIRDHOUSE_FOREST_HORIZONTAL,
			DecorationType.BIRDHOUSE_FOREST_VERTICAL,
			DecorationType.BIRDHOUSE_FOREST_HANGING,
			DecorationType.BIRDHOUSE_ENCHANTED_HORIZONTAL,
			DecorationType.BIRDHOUSE_ENCHANTED_VERTICAL,
			DecorationType.BIRDHOUSE_ENCHANTED_HANGING,
			DecorationType.BIRDHOUSE_DEPTHS_HORIZONTAL,
			DecorationType.BIRDHOUSE_DEPTHS_VERTICAL,
			DecorationType.BIRDHOUSE_DEPTHS_HANGING,
			DecorationType.WINDCHIME_IRON,
			DecorationType.WINDCHIME_GOLD,
			DecorationType.WINDCHIME_COPPER,
			DecorationType.WINDCHIME_AMETHYST,
			DecorationType.WINDCHIME_LAPIS,
			DecorationType.WINDCHIME_NETHERITE,
			DecorationType.WINDCHIME_DIAMOND,
			DecorationType.WINDCHIME_REDSTONE,
			DecorationType.WINDCHIME_EMERALD,
			DecorationType.WINDCHIME_QUARTZ,
			DecorationType.WINDCHIME_COAL,
			DecorationType.WINDCHIME_ICE
	);

	// TODO DECORATIONS: Remove on release
	@Deprecated
	public static boolean canUseFeature(Player player) {
		return canUseFeature(player, null);
	}

	@Deprecated
	public static boolean canUseFeature(Player player, @Nullable DecorationConfig config) {
		if (config != null) {
			if (config instanceof PlayerPlushie)
				return true;

			if (config instanceof Backpack)
				return true;

			DecorationType type = DecorationType.of(config);
			if (type != null && BYPASS_LIST.contains(type))
				return true;
		}

		String champeon = "3da17df8-f688-46e6-a033-20adb1d1acf0";
		return Rank.of(player).isStaff() || Rank.of(player).isBuilder() || player.getUniqueId().toString().equals(champeon);
	}
	//

	public static boolean hasBypass(Player player) {
		// TODO DECORATIONS: Remove on release
		if (!canUseFeature(player)) {
			DecorationError.UNRELEASED_FEATURE.send(player);
			return false;
		}
		//

		return CustomContentUtils.hasBypass(player);
	}

	public static String prettyMoney(Double price) {
		if (price == 0)
			return "free";

		return StringUtils.prettyMoney(price);
	}

	public static boolean canUsePaintbrush(Player player, ItemStack tool) {
		DecorationLang.debug(player, "Can use paintbrush?");

		if (!DyeStation.isPaintbrush(tool)) {
			DecorationLang.debug(player, " - not a paintbrush");
			return false;
		}

		int usesLeft = DyeStationMenu.getUses(tool);
		if (usesLeft <= 0) {
			DecorationLang.debug(player, " - no more uses");
			return false;
		}

		DecorationLang.debug(player, " yes");
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
}
