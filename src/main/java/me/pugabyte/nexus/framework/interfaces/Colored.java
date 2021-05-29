package me.pugabyte.nexus.framework.interfaces;

import me.pugabyte.nexus.framework.interfaces.impl.ColoredImpl;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;

import static me.pugabyte.nexus.utils.StringUtils.toHex;

public interface Colored extends TextColor, IsColored {
	/**
	 * Returns the color corresponding to this object.
	 * @return an RGB color
	 */
	@NotNull Color getColor();

	/**
	 * Returns the chat color corresponding to this object.
	 * <br>This method does not use official color codes and thus will not work with Scoreboards or other displays that only use strings.
	 */
	default @NotNull ChatColor getChatColor() {
		return ChatColor.of(getColor());
	}

	/**
	 * Returns the Bukkit color corresponding to this object.
	 */
	default @NotNull org.bukkit.Color getBukkitColor() {
		return org.bukkit.Color.fromRGB(getColor().getRed(), getColor().getGreen(), getColor().getBlue());
	}

	/**
	 * Returns one of the official vanilla colors. Overriding methods may choose to return a similar chat color.
	 * If none are found, White is returned.
	 */
	default @NotNull ChatColor getVanillaChatColor() {
		return Arrays.stream(ChatColor.values()).filter(chatColor -> chatColor.getColor() != null && chatColor.getColor().equals(getColor())).findAny().orElse(ChatColor.WHITE);
	}

	/**
	 * Returns the Minecraft hex string of the color corresponding to this object, as defined by {@link #getChatColor()}.
	 * @return a string like "&#ABCDEF"
	 */
	default @NotNull String getHex() {
		return "&" + toHex(getChatColor());
	}

	@Override
	default int value() {
		return getColor().getRGB();
	}

	@Override
	default @NotNull Colored colored() {
		return this;
	}

	static Colored colored(int rgb) {
		return new ColoredImpl(rgb);
	}

	static Colored colored(int r, int g, int b) {
		int rgb = r;
		rgb = (rgb << 8) + g;
		rgb = (rgb << 8) + b;
		return colored(rgb);
	}

	static Colored colored(Color color) {
		return colored(color.getRed(), color.getGreen(), color.getBlue());
	}

	static Colored colored(ChatColor chatColor) {
		Color color = chatColor.getColor();
		if (color == null)
			throw new IllegalArgumentException("Cannot create color of formatting type");
		return colored(color);
	}

	static Colored colored(org.bukkit.Color color) {
		return colored(color.asRGB());
	}

	static Colored colored(RGBLike color) {
		return colored(color.red(), color.green(), color.blue());
	}
}
