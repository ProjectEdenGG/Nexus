package me.pugabyte.nexus.utils;

import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Getter
public enum ColorType {

	WHITE(
			"white",
			Color.WHITE,
			ChatColor.WHITE,
			DyeColor.WHITE,
			0
	),
	LIGHT_GRAY(
			"light gray",
			Color.SILVER,
			ChatColor.GRAY,
			DyeColor.LIGHT_GRAY,
			8
	),
	GRAY(
			"gray",
			Color.GRAY,
			ChatColor.DARK_GRAY,
			DyeColor.GRAY,
			7
	),
	BLACK(
			"black",
			Color.BLACK,
			ChatColor.BLACK,
			DyeColor.BLACK,
			15
	),
	BROWN(
			"brown",
			Color.fromRGB(139, 69, 42),
			null,
			DyeColor.BROWN,
			12
	),
	RED(
			"red",
			Color.RED,
			ChatColor.DARK_RED,
			DyeColor.RED,
			14
	),
	LIGHT_RED(
			"light red",
			Color.fromRGB(255, 85, 85),
			ChatColor.RED,
			null,
			DyeColor.RED,
			14
	),
	ORANGE(
			"orange",
			Color.ORANGE,
			ChatColor.GOLD,
			DyeColor.ORANGE,
			1
	),
	YELLOW(
			"yellow",
			Color.YELLOW,
			ChatColor.YELLOW,
			DyeColor.YELLOW,
			4
	),
	LIGHT_GREEN(
			"lime",
			Color.LIME,
			ChatColor.GREEN,
			DyeColor.LIME,
			5
	),
	GREEN(
			"green",
			Color.GREEN,
			ChatColor.DARK_GREEN,
			DyeColor.GREEN,
			13
	),
	CYAN(
			"cyan",
			Color.TEAL,
			ChatColor.DARK_AQUA,
			DyeColor.CYAN,
			9
	),
	LIGHT_BLUE(
			"light blue",
			Color.AQUA,
			ChatColor.AQUA,
			DyeColor.LIGHT_BLUE,
			3
	),
	BLUE(
			"blue",
			Color.BLUE,
			ChatColor.BLUE,
			DyeColor.BLUE,
			11
	),
	PURPLE(
			"purple",
			Color.PURPLE,
			ChatColor.DARK_PURPLE,
			DyeColor.PURPLE,
			10
	),
	MAGENTA(
			"magenta",
			Color.FUCHSIA,
			null,
			DyeColor.MAGENTA,
			2
	),
	PINK(
			"pink",
			Color.fromRGB(255, 105, 180),
			ChatColor.LIGHT_PURPLE,
			DyeColor.PINK,
			6
	);

	private final String name;
	private final Color color;
	private final ChatColor chatColor;
	private final DyeColor dyeColor;
	private final DyeColor similarDyeColor;
	private final Integer durability;

	ColorType(String name, Color color, ChatColor chatColor, DyeColor dyeColor, int durability) {
		this(name, color, chatColor, dyeColor, dyeColor, durability);
	}

	ColorType(String name, Color color, ChatColor chatColor, DyeColor dyeColor, DyeColor similarDyeColor, int durability) {
		this.name = name;
		this.color = color;
		this.chatColor = chatColor;
		this.dyeColor = dyeColor;
		this.similarDyeColor = similarDyeColor;
		this.durability = durability;
	}

	public static ColorType of(String name) {
		return Arrays.stream(values()).filter(colorType -> colorType.getName().equals(name)).findFirst().orElse(null);
	}

	public static ColorType of(Color color) {
		return Arrays.stream(values()).filter(colorType -> colorType.getColor().equals(color)).findFirst().orElse(null);
	}

	public static ColorType of(ChatColor chatColor) {
		return Arrays.stream(values()).filter(colorType -> colorType.getChatColor().equals(chatColor)).findFirst().orElse(null);
	}

	public static ColorType of(DyeColor dyeColor) {
		if (dyeColor == null)
			return null;
		return Arrays.stream(values()).filter(colorType -> colorType.getDyeColor().equals(dyeColor)).findFirst().orElse(null);
	}

	public static ColorType of(Material material) {
		return of(Arrays.stream(DyeColor.values()).filter(dyeColor -> material.name().startsWith(dyeColor.name())).findFirst().orElse(null));
	}

	public Material switchColor(Material material) {
		return switchColor(material, this);
	}

	public static Material switchColor(Material material, ColorType colorType) {
		return switchColor(material, colorType.getSimilarDyeColor());
	}

	public static Material switchColor(Material material, DyeColor dyeColor) {
		return Material.valueOf(material.name().replace(of(material).getSimilarDyeColor().name(), dyeColor.name()));
	}

	private static String generic(Material material) {
		return material.name().replace("WHITE", "");
	}

	public Material getWool() {
		return getWool(this);
	}

	public static Material getWool(ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_WOOL));
	}

	public Material getDye() {
		return getDye(this);
	}

	public static Material getDye(ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_DYE));
	}

	public Material getCarpet() {
		return getCarpet(this);
	}

	public static Material getCarpet(ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_CARPET));
	}

	public Material getBed() {
		return getBed(this);
	}

	public static Material getBed(ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_BED));
	}

	public Material getBanner() {
		return getBanner(this);
	}

	public static Material getBanner(ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_BANNER));
	}

	public Material getWallBanner() {
		return getWallBanner(this);
	}

	public static Material getWallBanner(ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_WALL_BANNER));
	}

	public Material getStainedGlass() {
		return getStainedGlass(this);
	}

	public static Material getStainedGlass(ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_STAINED_GLASS));
	}

	public Material getStainedGlassPane() {
		return getStainedGlassPane(this);
	}

	public static Material getStainedGlassPane(ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_STAINED_GLASS_PANE));
	}

	public Material getTerracotta() {
		return getTerracotta(this);
	}

	public static Material getTerracotta(ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_TERRACOTTA));
	}

	public Material getGlazedTerracotta() {
		return getGlazedTerracotta(this);
	}

	public static Material getGlazedTerracotta(ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_GLAZED_TERRACOTTA));
	}

	public Material getConcrete() {
		return getConcrete(this);
	}

	public static Material getConcrete(ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_CONCRETE));
	}

	public Material getConcretePowder() {
		return getConcretePowder(this);
	}

	public static Material getConcretePowder(ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_CONCRETE_POWDER));
	}

	public Material getShulkerBox() {
		return getShulkerBox(this);
	}

	public static Material getShulkerBox(ColorType colorType) {
		return Material.valueOf(colorType.getSimilarDyeColor() + generic(Material.WHITE_SHULKER_BOX));
	}

	public String getDisplayName() {
		return chatColor == null ? "" : chatColor + StringUtils.camelCase(name);
	}

	public static org.bukkit.ChatColor toBukkit(ChatColor color) {
		return org.bukkit.ChatColor.valueOf(color.getName().toUpperCase());
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

}