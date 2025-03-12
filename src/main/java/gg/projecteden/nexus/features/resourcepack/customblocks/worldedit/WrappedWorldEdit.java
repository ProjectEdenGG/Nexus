package gg.projecteden.nexus.features.resourcepack.customblocks.worldedit;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.customblocks.worldedit.WorldEditUtils.CustomBlockInputParser;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class WrappedWorldEdit {
	public static boolean loaded;

	public static void init() {
		loaded = Bukkit.getPluginManager().isPluginEnabled("WorldEdit") || Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit");
	}

	public static void registerParser() {
		if (loaded) {
			new CustomBlockInputParser();
			Bukkit.getPluginManager().registerEvents(new WorldEditListener(), Nexus.getInstance());
		}
	}

	public static void pasteSchematic(Location loc, File schematic, Boolean replaceBlocks, Boolean shouldCopyBiomes, Boolean shouldCopyEntities) {
		if (loaded)
			WorldEditUtils.pasteSchematic(loc, schematic, replaceBlocks, shouldCopyBiomes, shouldCopyEntities);
	}

	public static List<Block> getBlocksInSchematic(Location loc, File schematic) {
		if (loaded)
			return WorldEditUtils.getBlocksInSchematic(loc, schematic);
		else
			return new ArrayList<>();
	}
}
