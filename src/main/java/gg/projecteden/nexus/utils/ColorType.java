package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.framework.interfaces.IsColored;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.inventivetalent.glow.GlowAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
			GlowAPI.Color.WHITE
	),
	LIGHT_GRAY(
			"light gray",
			Color.SILVER,
			ChatColor.GRAY,
			ChatColor.GRAY,
			NamedTextColor.GRAY,
			DyeColor.LIGHT_GRAY,
			GlowAPI.Color.GRAY
	),
	GRAY(
			"gray",
			Color.GRAY,
			ChatColor.DARK_GRAY,
			ChatColor.DARK_GRAY,
			NamedTextColor.DARK_GRAY,
			DyeColor.GRAY,
			GlowAPI.Color.DARK_GRAY
	),
	BLACK(
			"black",
			Color.BLACK,
			ChatColor.BLACK,
			ChatColor.BLACK,
			NamedTextColor.BLACK,
			DyeColor.BLACK,
			GlowAPI.Color.BLACK
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
			GlowAPI.Color.DARK_RED
	),
	LIGHT_RED(
			"light red",
			Color.fromRGB(255, 85, 85),
			ChatColor.RED,
			ChatColor.RED,
			NamedTextColor.RED,
			null,
			DyeColor.RED,
			GlowAPI.Color.RED
	),
	ORANGE(
			"orange",
			Color.ORANGE,
			ChatColor.GOLD,
			ChatColor.GOLD,
			NamedTextColor.GOLD,
			DyeColor.ORANGE,
			GlowAPI.Color.GOLD
	),
	YELLOW(
			"yellow",
			Color.YELLOW,
			ChatColor.YELLOW,
			ChatColor.YELLOW,
			NamedTextColor.YELLOW,
			DyeColor.YELLOW,
			GlowAPI.Color.YELLOW
	),
	LIGHT_GREEN(
			"lime",
			Color.LIME,
			ChatColor.GREEN,
			ChatColor.GREEN,
			NamedTextColor.GREEN,
			DyeColor.LIME,
			GlowAPI.Color.GREEN
	),
	GREEN(
			"green",
			Color.GREEN,
			ChatColor.DARK_GREEN,
			ChatColor.DARK_GREEN,
			NamedTextColor.DARK_GREEN,
			DyeColor.GREEN,
			GlowAPI.Color.DARK_GREEN
	),
	CYAN(
			"cyan",
			Color.TEAL,
			ChatColor.DARK_AQUA,
			ChatColor.DARK_AQUA,
			NamedTextColor.DARK_AQUA,
			DyeColor.CYAN,
			GlowAPI.Color.DARK_AQUA
	),
	LIGHT_BLUE(
			"light blue",
			Color.AQUA,
			ChatColor.AQUA,
			ChatColor.AQUA,
			NamedTextColor.AQUA,
			DyeColor.LIGHT_BLUE,
			GlowAPI.Color.AQUA
	),
	BLUE(
			"blue",
			Color.BLUE,
			ChatColor.BLUE,
			ChatColor.BLUE,
			NamedTextColor.BLUE,
			DyeColor.BLUE,
			GlowAPI.Color.BLUE
	),
	PURPLE(
			"purple",
			Color.PURPLE,
			ChatColor.DARK_PURPLE,
			ChatColor.DARK_PURPLE,
			NamedTextColor.DARK_PURPLE,
			DyeColor.PURPLE,
			GlowAPI.Color.DARK_PURPLE
	),
	MAGENTA(
			"magenta",
			Color.FUCHSIA,
			ChatColor.of(new java.awt.Color(0xFF, 0, 0xFF)),
			ChatColor.LIGHT_PURPLE,
			NamedTextColor.LIGHT_PURPLE,
			DyeColor.MAGENTA,
			GlowAPI.Color.PURPLE
	),
	PINK(
			"pink",
			Color.fromRGB(255, 105, 180),
			ChatColor.LIGHT_PURPLE,
			ChatColor.LIGHT_PURPLE,
			NamedTextColor.LIGHT_PURPLE,
			DyeColor.PINK,
			GlowAPI.Color.PURPLE
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
	private final @Nullable GlowAPI.Color glowColor;

	ColorType(@NotNull String name, @NotNull Color bukkitColor, @NotNull ChatColor chatColor, @NotNull ChatColor bukkitChatColor,
			  NamedTextColor namedColor, @NotNull DyeColor dyeColor, @Nullable GlowAPI.Color glowColor) {
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
	public static ColorType of(@Nullable GlowAPI.Color glowColor) {
		if (glowColor == null) return null;
		return Arrays.stream(values()).filter(colorType -> glowColor.equals(colorType.getGlowColor())).findFirst().orElse(null);
	}

	@Nullable
	public static ColorType of(@Nullable Material material) {
		if (material == null) return null;
		return of(Arrays.stream(DyeColor.values()).filter(dyeColor -> material.name().startsWith(dyeColor.name())).findFirst().orElse(null));
	}

	@NotNull
	public Material switchColor(@NotNull Material material) {
		return switchColor(material, this);
	}

	@NotNull
	public static Material switchColor(@NotNull Material material, @NotNull ColorType colorType) {
		return switchColor(material, colorType.getSimilarDyeColor());
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
	public String getDisplayName() {
		return chatColor + StringUtils.camelCase(name);
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
			final java.awt.Color awtColor = color.getColor();
			return Color.fromRGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
		} catch (IllegalArgumentException e) {
			return null;
		}
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

	public static Color hexToBukkit(String hex) {
		if (!hex.startsWith("#"))
			hex = "#" + hex;

		var decode = java.awt.Color.decode(hex);
		return Color.fromRGB(decode.getRed(), decode.getGreen(), decode.getBlue());
	}

}
