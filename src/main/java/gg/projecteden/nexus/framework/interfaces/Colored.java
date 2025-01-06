package gg.projecteden.nexus.framework.interfaces;

import gg.projecteden.nexus.framework.interfaces.impl.ColoredImpl;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.StringUtils;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;

public interface Colored extends TextColor, IsColored {
	/**
	 * Returns the color corresponding to this object.
	 *
	 * @return an RGB color
	 */
	@NotNull Color getColor();

	default ColorType getColorType() {
		return ColorType.of(getBukkitColor());
	}

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
		return "&" + StringUtils.toHex(getChatColor());
	}

	@Override
	default int value() {
		return getColor().getRGB();
	}

	@Override
	default @NotNull Colored colored() {
		return this;
	}

	static Colored of(int rgb) {
		return new ColoredImpl(rgb);
	}

	static Colored of(int r, int g, int b) {
		int rgb = r;
		rgb = (rgb << 8) + g;
		rgb = (rgb << 8) + b;
		return of(rgb);
	}

	static Colored of(Color color) {
		return of(color.getRed(), color.getGreen(), color.getBlue());
	}

	static Colored of(ColorType colorType) {
		return of(colorType.getBukkitColor());
	}

	static Colored of(ChatColor chatColor) {
		Color color = chatColor.getColor();
		if (color == null)
			throw new IllegalArgumentException("Cannot create color of formatting type");
		return of(color);
	}

	static Colored of(org.bukkit.Color color) {
		return of(color.asRGB());
	}

	static Colored of(RGBLike color) {
		return of(color.red(), color.green(), color.blue());
	}

	default void apply(ItemStack itemStack) {
		if (!(itemStack.getItemMeta() instanceof LeatherArmorMeta armorMeta))
			return;

		armorMeta.setColor(this.getBukkitColor());
		itemStack.setItemMeta(armorMeta);
	}
}
