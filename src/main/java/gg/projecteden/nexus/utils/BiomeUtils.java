package gg.projecteden.nexus.utils;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import org.bukkit.Registry;
import org.bukkit.block.Biome;

public class BiomeUtils {

	public static Biome[] values() {
		return Lists.newArrayList(Registry.BIOME).toArray(new Biome[0]);
	}

	public static String name(Biome biome) {
		return AdventureUtils.asPlainText(Component.translatable(biome.translationKey()));
	}

}
