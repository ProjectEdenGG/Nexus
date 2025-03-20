package gg.projecteden.nexus.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("removal")
public class LanguageUtils {
	/**
	 * Gets the default English translation for a specified translatable component
	 * @param component Adventure TranslatableComponent
	 * @return English translation
	 */
	public static String translate(@NotNull TranslatableComponent component) {
		// uses Adventure's ComponentFlattener to convert translation keys to their translations w/ proper variable parsing
		return AdventureUtils.asPlainText(component);
	}

	/**
	 * Gets the default English translation for a specified key
	 * @param key Minecraft key
	 * @return English translation
	 */
	public static String translate(@NotNull String key) {
		return translate(Component.translatable(key));
	}

	/**
	 * Gets the default English translation for a specified block
	 * @param block a block
	 * @return English translation
	 */
	public static String translate(@NotNull Block block) {
		return translate(Bukkit.getUnsafe().getBlockTranslationKey(block.getType()));
	}

	/**
	 * Gets the default English translation for a specified material
	 * @param material a material
	 * @return English translation
	 */
	public static String translate(@NotNull Material material) {
		return translate(Bukkit.getUnsafe().getItemTranslationKey(material));
	}

	/**
	 * Gets the default English translation for a specified entity
	 * @param entity an entity
	 * @return English translation
	 */
	public static String translate(@NotNull EntityType entity) {
		return translate(Bukkit.getUnsafe().getTranslationKey(entity));
	}

	/**
	 * Gets the default English translation for a specified material
	 * @param item an item
	 * @return English translation
	 */
	public static String translate(@NotNull ItemStack item) {
		return translate(Bukkit.getUnsafe().getTranslationKey(item));
	}
}
