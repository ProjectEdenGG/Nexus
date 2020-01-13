package me.pugabyte.bncore.utils;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

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
			DyeColor.SILVER,
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
	LIME(
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

	ColorType(String name, Color color, ChatColor chatColor, DyeColor dyeColor, int durability) {
		this.name = name;
		this.color = color;
		this.chatColor = chatColor;
		this.dyeColor = dyeColor;
		this.durability = durability;
	}

	private String name;
	private Color color;
	private ChatColor chatColor;
	private DyeColor dyeColor;
	private Integer durability;

	public static ColorType fromString(String name) {
		return Arrays.stream(values()).filter(colorConfig -> name.equals(colorConfig.getName())).findFirst().orElse(null);
	}

	public static ColorType fromColor(Color color) {
		return Arrays.stream(values()).filter(colorConfig -> color.equals(colorConfig.getColor())).findFirst().orElse(null);
	}

	public static ColorType fromChatColor(ChatColor chatColor) {
		return Arrays.stream(values()).filter(colorConfig -> chatColor.equals(colorConfig.getChatColor())).findFirst().orElse(null);
	}

	public static ColorType fromDyeColor(DyeColor dyeColor) {
		return Arrays.stream(values()).filter(colorConfig -> dyeColor.equals(colorConfig.getDyeColor())).findFirst().orElse(null);
	}

	public static ColorType fromDurability(int durability) {
		return Arrays.stream(values()).filter(colorConfig -> durability == (colorConfig.getDurability())).findFirst().orElse(null);
	}

	public ItemStack getItemStack(Material material) {
		return new ItemStack(material, 1, getDurability().shortValue());
	}

	public String getDisplayName() {
		return chatColor + Utils.camelCase(name);
	}

}