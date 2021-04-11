package me.pugabyte.nexus.utils;

import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.OfflinePlayer;

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

	public static Component getPrefix(String prefix) {
		return Component.text("").color(NamedTextColor.DARK_AQUA)
				.append(Component.text("[").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.BOLD))
				.append(Component.text(prefix).color(NamedTextColor.YELLOW))
				.append(Component.text("]").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.BOLD))
				.append(Component.text(" ").color(NamedTextColor.DARK_AQUA));
	}
}
