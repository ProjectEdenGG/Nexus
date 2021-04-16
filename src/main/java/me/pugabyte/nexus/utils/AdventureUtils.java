package me.pugabyte.nexus.utils;

import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AdventureUtils {
	public static Component stripColor(Component component) {
		component = component.style(Style.empty());
		if (component instanceof TranslatableComponent) {
			TranslatableComponent tComponent = (TranslatableComponent) component;
			component = tComponent.args(stripColor(tComponent.args()));
		}
		return component.children(stripColor(component.children()));
	}

	public static List<Component> stripColor(Collection<Component> components) {
		return components.stream().map(AdventureUtils::stripColor).collect(Collectors.toList());
	}

	public static String asPlainText(Component component) {
		return PlainComponentSerializer.plain().serialize(component);
	}

	public static String asLegacyText(Component component) {
		return LegacyComponentSerializer.legacySection().serialize(component);
	}

	public static Component fromLegacyText(String string) {
		return LegacyComponentSerializer.legacySection().deserialize(string);
	}

	public static Component fromLegacyAmpersandText(String string) {
		return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
	}

	public static Component fromJson(JsonBuilder json) {
		return GsonComponentSerializer.gson().deserialize(json.serialize());
	}

	public static Identity identityOf(Identified object) {
		return object.identity();
	}

	public static Identity identityOf(UUID uuid) {
		return Identity.identity(uuid);
	}

	public static Identity identityOf(OfflinePlayer player) {
		return identityOf(player.getUniqueId());
	}

	public static TextComponent getPrefix(String prefix) {
		return Component.text("", NamedTextColor.DARK_AQUA)
				.append(Component.text("[", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
				.append(Component.text(prefix, NamedTextColor.YELLOW))
				.append(Component.text("]", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
				.append(Component.text(" ", NamedTextColor.DARK_AQUA));
	}

	public static TextComponent colorText(@Nullable ChatColor color, @NotNull String text) {
		if (color == null)
			return Component.text(text);
		return Component.text(text, TextColor.color(color.getColor().getRGB()));
	}

	public static TextComponent colorText(@Nullable ColorType color, @NotNull String text) {
		if (color == null || color.getColor() == null)
			return Component.text(text);
		return Component.text(text, TextColor.color(color.getColor().getRed()));
	}

	/**
	 * Returns a component that has separated the input list of components with commas.
	 * <p>
	 * If the input list is empty, a blank component will be returned.
	 * <br>
	 * If the list has one item, it will be returned.
	 * <br>
	 * If the list has two items, "[component1] and [component2]" will be returned.
	 * <br>
	 * Else, "[component1], [component2], [...], and [componentX]" will be returned.
	 * @param components components to separate by commas.
	 * @param color optional color to use for the commas
	 * @return a formatted TextComponent
	 */
	public static TextComponent commaJoinText(List<Component> components, @Nullable TextColor color) {
		TextComponent component = Component.text("", color);

		if (components.isEmpty())
			return component;
		if (components.size() == 1)
			return component.append(components.get(0));
		if (components.size() == 2)
			return component.append(components.get(0))
				    .append(Component.text(" and "))
					.append(components.get(1));

		TextComponent.Builder builder = component.toBuilder();
		for (int i = 0; i < components.size(); i++) {
			builder.append(components.get(i));
			if (i < components.size()-2)
				builder.append(Component.text(", "));
			else if (i == components.size()-2)
				builder.append(Component.text(", and "));
		}
		return builder.build();
	}

	/**
	 * Returns a component that has separated the input list of components with commas.
	 * <p>
	 * If the input list is empty, a blank component will be returned.
	 * <br>
	 * If the list has one item, it will be returned.
	 * <br>
	 * If the list has two items, "[component1] and [component2]" will be returned.
	 * <br>
	 * Else, "[component1], [component2], [...], and [componentX]" will be returned.
	 * @param components components to separate by commas.
	 * @return a formatted TextComponent
	 */
	public static TextComponent commaJoinText(List<Component> components) {
		return commaJoinText(components, null);
	}
}
