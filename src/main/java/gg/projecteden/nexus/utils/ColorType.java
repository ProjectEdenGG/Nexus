package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.framework.interfaces.IsColored;
import gg.projecteden.nexus.utils.GlowUtils.GlowColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
@Getter
@AllArgsConstructor
public enum ColorType implements IsColored {

	WHITE(
			"white",
			Color.WHITE,
			ChatColor.WHITE,
			ChatColor.WHITE,
			NamedTextColor.WHITE,
			DyeColor.WHITE,
			GlowColor.WHITE
	),
	LIGHT_GRAY(
			"light gray",
			Color.SILVER,
			ChatColor.GRAY,
			ChatColor.GRAY,
			NamedTextColor.GRAY,
			DyeColor.LIGHT_GRAY,
			GlowColor.GRAY
	),
	GRAY(
			"gray",
			Color.GRAY,
			ChatColor.DARK_GRAY,
			ChatColor.DARK_GRAY,
			NamedTextColor.DARK_GRAY,
			DyeColor.GRAY,
			GlowColor.DARK_GRAY
	),
	BLACK(
			"black",
			Color.BLACK,
			ChatColor.BLACK,
			ChatColor.BLACK,
			NamedTextColor.BLACK,
			DyeColor.BLACK,
			GlowColor.BLACK
	),
	BROWN(
			"brown",
			Color.fromRGB(139, 69, 42),
			ChatColor.of(new java.awt.Color(139, 69, 42)),
			ChatColor.GOLD,
			NamedTextColor.GOLD,
			DyeColor.BROWN,
			null
	),
	RED(
			"red",
			Color.RED,
			ChatColor.DARK_RED,
			ChatColor.DARK_RED,
			NamedTextColor.DARK_RED,
			DyeColor.RED,
			GlowColor.DARK_RED
	),
	LIGHT_RED(
			"light red",
			Color.fromRGB(255, 85, 85),
			ChatColor.RED,
			ChatColor.RED,
			NamedTextColor.RED,
			null,
			DyeColor.RED,
			GlowColor.RED
	),
	ORANGE(
			"orange",
			Color.ORANGE,
			ChatColor.GOLD,
			ChatColor.GOLD,
			NamedTextColor.GOLD,
			DyeColor.ORANGE,
			GlowColor.GOLD
	),
	YELLOW(
			"yellow",
			Color.YELLOW,
			ChatColor.YELLOW,
			ChatColor.YELLOW,
			NamedTextColor.YELLOW,
			DyeColor.YELLOW,
			GlowColor.YELLOW
	),
	LIGHT_GREEN(
			"lime",
			Color.LIME,
			ChatColor.GREEN,
			ChatColor.GREEN,
			NamedTextColor.GREEN,
			DyeColor.LIME,
			GlowColor.GREEN
	),
	GREEN(
			"green",
			Color.GREEN,
			ChatColor.DARK_GREEN,
			ChatColor.DARK_GREEN,
			NamedTextColor.DARK_GREEN,
			DyeColor.GREEN,
			GlowColor.DARK_GREEN
	),
	CYAN(
			"cyan",
			Color.TEAL,
			ChatColor.DARK_AQUA,
			ChatColor.DARK_AQUA,
			NamedTextColor.DARK_AQUA,
			DyeColor.CYAN,
			GlowColor.DARK_AQUA
	),
	LIGHT_BLUE(
			"light blue",
			Color.AQUA,
			ChatColor.AQUA,
			ChatColor.AQUA,
			NamedTextColor.AQUA,
			DyeColor.LIGHT_BLUE,
			GlowColor.AQUA
	),
	BLUE(
			"blue",
			Color.BLUE,
			ChatColor.BLUE,
			ChatColor.BLUE,
			NamedTextColor.BLUE,
			DyeColor.BLUE,
			GlowColor.BLUE
	),
	PURPLE(
			"purple",
			Color.PURPLE,
			ChatColor.DARK_PURPLE,
			ChatColor.DARK_PURPLE,
			NamedTextColor.DARK_PURPLE,
			DyeColor.PURPLE,
			GlowColor.DARK_PURPLE
	),
	MAGENTA(
			"magenta",
			Color.FUCHSIA,
			ChatColor.of(new java.awt.Color(0xFF, 0, 0xFF)),
			ChatColor.LIGHT_PURPLE,
			NamedTextColor.LIGHT_PURPLE,
			DyeColor.MAGENTA,
			GlowColor.PURPLE
	),
	PINK(
			"pink",
			Color.fromRGB(255, 105, 180),
			ChatColor.LIGHT_PURPLE,
			ChatColor.LIGHT_PURPLE,
			NamedTextColor.LIGHT_PURPLE,
			DyeColor.PINK,
			GlowColor.PURPLE
	);

	private final @NotNull String name;
	private final @NotNull Color bukkitColor;
	private final @NotNull ChatColor chatColor;
	/**
	 * A similar official vanilla chat color
	 */
	private final @NotNull ChatColor vanillaChatColor;
	private final @NotNull NamedTextColor namedColor;
	private final @Nullable DyeColor dyeColor;
	private final @NotNull DyeColor similarDyeColor;
	private final @Nullable GlowUtils.GlowColor glowColor;

	ColorType(@NotNull String name, @NotNull Color bukkitColor, @NotNull ChatColor chatColor, @NotNull ChatColor bukkitChatColor,
			  NamedTextColor namedColor, @NotNull DyeColor dyeColor, @Nullable GlowUtils.GlowColor glowColor) {
		this(name, bukkitColor, chatColor, bukkitChatColor, namedColor, dyeColor, dyeColor, glowColor);
	}

	@Override
	public @NotNull Colored colored() {
		return Colored.of(chatColor);
	}

	@Nullable
	public static ColorType of(@Nullable String name) {
		if (name == null) return null;
		return Arrays.stream(values()).filter(colorType -> name.equalsIgnoreCase(colorType.getName())).findFirst().orElse(null);
	}

	@Nullable
	public static ColorType of(@Nullable Color color) {
		if (color == null) return null;
		return Arrays.stream(values()).filter(colorType -> color.equals(colorType.getBukkitColor())).findFirst().orElse(null);
	}

	@Nullable
	public static ColorType of(@Nullable ChatColor chatColor) {
		if (chatColor == null) return null;
		return Arrays.stream(values()).filter(colorType -> colorType.getChatColor().getColor().equals(chatColor.getColor())).findFirst().orElse(null);
	}

	@Nullable
	public static ColorType of(@Nullable NamedTextColor namedColor) {
		if (namedColor == null) return null;
		return Arrays.stream(values()).filter(colorType -> namedColor.equals(colorType.getNamedColor())).findFirst().orElse(null);
	}

	@Nullable
	public static ColorType of(@Nullable DyeColor dyeColor) {
		if (dyeColor == null) return null;
		return Arrays.stream(values()).filter(colorType -> dyeColor.equals(colorType.getDyeColor())).findFirst().orElse(null);
	}

	@Nullable
	public static ColorType of(@Nullable GlowUtils.GlowColor glowColor) {
		if (glowColor == null) return null;
		return Arrays.stream(values()).filter(colorType -> glowColor.equals(colorType.getGlowColor())).findFirst().orElse(null);
	}

	@Nullable
	public static ColorType of(@Nullable Material material) {
		if (material == null) return null;
		return of(Arrays.stream(DyeColor.values()).filter(dyeColor -> material.name().startsWith(dyeColor.name())).findFirst().orElse(null));
	}

	@Nullable
	public static ColorType of(@Nullable CustomBlock customBlock) {
		if (customBlock == null) return null;
		return of(Arrays.stream(DyeColor.values()).filter(dyeColor -> customBlock.name().startsWith(dyeColor.name())).findFirst().orElse(null));
	}

	@NotNull
	public CustomBlock switchColor(@NotNull CustomBlock customBlock) {
		return switchColor(customBlock, this);
	}

	@NotNull
	public CustomBlock switchColor(@NotNull CustomBlockTag customBlockTag) {
		return switchColor(customBlockTag.first(), this);
	}

	@NotNull
	public Material switchColor(@NotNull Material material) {
		return switchColor(material, this);
	}

	@NotNull
	public Material switchColor(@NotNull Tag<Material> materialTag) {
		return switchColor(materialTag.getValues().iterator().next(), this);
	}

	@NotNull
	public static CustomBlock switchColor(@NotNull CustomBlock customBlock, @NotNull ColorType colorType) {
		return switchColor(customBlock, colorType.getSimilarDyeColor());
	}

	@NotNull
	public static Material switchColor(@NotNull Material material, @NotNull ColorType colorType) {
		return switchColor(material, colorType.getSimilarDyeColor());
	}

	@NotNull
	public static CustomBlock switchColor(@NotNull CustomBlock customBlock, @NotNull DyeColor dyeColor) {
		ColorType colorType = of(customBlock);
		if (colorType == null)
			throw new InvalidInputException("Could not determine color of " + customBlock);
		return CustomBlock.valueOf(customBlock.name().replace(colorType.getSimilarDyeColor().name(), dyeColor.name()));
	}

	@NotNull
	public static Material switchColor(@NotNull Material material, @NotNull DyeColor dyeColor) {
		ColorType colorType = of(material);
		if (colorType == null)
			throw new InvalidInputException("Could not determine color of " + material);
		return Material.valueOf(material.name().replace(colorType.getSimilarDyeColor().name(), dyeColor.name()));
	}

	@NotNull
	private static String generic(Material material) {
		return material.name().replace("WHITE", "");
	}

	@NotNull
	private static String generic(CustomBlock customBlock) {
		return customBlock.name().replace("WHITE", "");
	}

	@NotNull
	public Material getWool() {
		return getWool(this);
	}

	@NotNull
	public static Material getWool(@NotNull ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_WOOL));
	}

	@NotNull
	public Material getDye() {
		return getDye(this);
	}

	@NotNull
	public static Material getDye(@NotNull ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_DYE));
	}

	@NotNull
	public Material getCarpet() {
		return getCarpet(this);
	}

	@NotNull
	public static Material getCarpet(@NotNull ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_CARPET));
	}

	@NotNull
	public Material getBed() {
		return getBed(this);
	}

	@NotNull
	public static Material getBed(@NotNull ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_BED));
	}

	@NotNull
	public Material getBanner() {
		return getBanner(this);
	}

	@NotNull
	public static Material getBanner(@NotNull ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_BANNER));
	}

	@NotNull
	public Material getWallBanner() {
		return getWallBanner(this);
	}

	@NotNull
	public static Material getWallBanner(@NotNull ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_WALL_BANNER));
	}

	@NotNull
	public Material getStainedGlass() {
		return getStainedGlass(this);
	}

	@NotNull
	public static Material getStainedGlass(@NotNull ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_STAINED_GLASS));
	}

	@NotNull
	public Material getStainedGlassPane() {
		return getStainedGlassPane(this);
	}

	@NotNull
	public static Material getStainedGlassPane(@NotNull ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_STAINED_GLASS_PANE));
	}

	@NotNull
	public Material getTerracotta() {
		return getTerracotta(this);
	}

	@NotNull
	public static Material getTerracotta(@NotNull ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_TERRACOTTA));
	}

	@NotNull
	public Material getGlazedTerracotta() {
		return getGlazedTerracotta(this);
	}

	@NotNull
	public static Material getGlazedTerracotta(@NotNull ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_GLAZED_TERRACOTTA));
	}

	@NotNull
	public Material getConcrete() {
		return getConcrete(this);
	}

	@NotNull
	public static Material getConcrete(@NotNull ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_CONCRETE));
	}

	@NotNull
	public Material getConcretePowder() {
		return getConcretePowder(this);
	}

	@NotNull
	public static Material getConcretePowder(@NotNull ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_CONCRETE_POWDER));
	}

	@NotNull
	public Material getShulkerBox() {
		return getShulkerBox(this);
	}

	@NotNull
	public static Material getShulkerBox(@NotNull ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_SHULKER_BOX));
	}

	@NotNull
	public Material getCandle() {
		return getCandle(this);
	}

	@NotNull
	public static Material getCandle(@NotNull ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_CANDLE));
	}

	@NotNull
	public CustomBlock getCustomBlock(CustomBlock whiteType) {
		return getCustomBlock(this, whiteType);
	}

	@NotNull
	public static CustomBlock getCustomBlock(@NotNull ColorType colorType, CustomBlock whiteType) {
		return CustomBlock.valueOf(colorType.getSimilarDyeColor() + generic(whiteType));
	}

	@NotNull
	public String getDisplayName() {
		return chatColor + StringUtils.camelCase(name);
	}

	@Getter
	private static Map<String, ChatColor> all;

	static {
		try {
			Field BY_NAME = ChatColor.class.getDeclaredField("BY_NAME");
			BY_NAME.setAccessible(true);
			all = (Map<String, ChatColor>) BY_NAME.get(null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static List<ColorType> getDyes() {
		return Arrays.stream(values()).filter(colorType -> colorType.getDyeColor() != null).toList();
	}

	@SneakyThrows
	public static Map<String, ChatColor> getColors() {
		return getAll().entrySet().stream()
				.filter(entry -> entry.getValue().getColor() != null)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	@SneakyThrows
	public static Map<String, ChatColor> getFormats() {
		return getAll().entrySet().stream()
				.filter(entry -> entry.getValue().getColor() == null)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	public static ColorType getRandom() {
		return RandomUtils.randomElement(ColorType.class);
	}

	@Nullable
	public org.bukkit.ChatColor toBukkitChatColor() {
		return toBukkitChatColor(getVanillaChatColor());
	}

	@Nullable
	public static org.bukkit.ChatColor toBukkitChatColor(@NotNull ChatColor color) {
		try {
			return org.bukkit.ChatColor.valueOf(color.getName().toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	@Nullable
	public static org.bukkit.Color toBukkitColor(@NotNull ChatColor color) {
		try {
			return toBukkit(color.getColor());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static Color toBukkit(java.awt.Color javaColor) {
		return Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue());
	}

	public static java.awt.Color toJava(Color bukkitColor) {
		return new java.awt.Color(bukkitColor.getRed(), bukkitColor.getGreen(), bukkitColor.getBlue());
	}

	public static Color hexToBukkit(String hex) {
		if (!hex.startsWith("#"))
			hex = "#" + hex;

		return toBukkit(java.awt.Color.decode(hex));
	}

	private static IndexColorModel colorModel;
	private static java.awt.Color[] colors;

	public static ColorType ofClosest(Color bukkitColor) {
		java.awt.Color color = new java.awt.Color(bukkitColor.getRed(), bukkitColor.getGreen(), bukkitColor.getBlue(), bukkitColor.getAlpha());
		final byte index = ((byte[]) getColorModel().getDataElements(color.getRGB(), null))[0];
		return of(ColorType.toBukkit(colors[index]));
	}

	private static IndexColorModel getColorModel() {
		if (colorModel != null)
			return colorModel;

		colors = new java.awt.Color[values().length];
		for (int i = 0; i < values().length; i++) {
			colors[i] = ColorType.toJava(values()[i].getBukkitColor());
		}

		colorModel = createColorModel(colors);
		return colorModel;
	}

	private static IndexColorModel createColorModel(java.awt.Color[] colors) {
		final int[] colorMap = new int[colors.length];
		for (int i = 0; i < colors.length; i++) {
			colorMap[i] = colors[i].getRGB();
		}
		final int bits = (int) Math.ceil(Math.log(colorMap.length) / Math.log(2));
		return new IndexColorModel(bits, colorMap.length, colorMap, 0, false, -1, DataBuffer.TYPE_BYTE);
	}

}
