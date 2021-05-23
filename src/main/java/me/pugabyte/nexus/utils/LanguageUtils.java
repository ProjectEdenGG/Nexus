package me.pugabyte.nexus.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_16_R3.LocaleLanguage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class LanguageUtils {
	/**
	 * Gets the default English translation for a specified key
	 * @param key Minecraft key
	 * @return English translation
	 */
	public String translate(@NotNull String key) {
		return LocaleLanguage.getInstance().translateKey(key);
	}

	/**
	 * Gets the default English translation for a specified block
	 * @param block a block
	 * @return English translation
	 */
	public String translate(@NotNull Block block) {
		return translate(Bukkit.getUnsafe().getTranslationKey(block));
	}

	/**
	 * Gets the default English translation for a specified material
	 * @param material a material
	 * @return English translation
	 */
	public String translate(@NotNull Material material) {
		return translate(Bukkit.getUnsafe().getTranslationKey(material));
	}

	/**
	 * Gets the default English translation for a specified entity
	 * @param entity an entity
	 * @return English translation
	 */
	public String translate(@NotNull EntityType entity) {
		return translate(Bukkit.getUnsafe().getTranslationKey(entity));
	}

	/**
	 * Gets the default English translation for a specified material
	 * @param item an item
	 * @return English translation
	 */
	public String translate(@NotNull ItemStack item) {
		return translate(Bukkit.getUnsafe().getTranslationKey(item));
	}
}
