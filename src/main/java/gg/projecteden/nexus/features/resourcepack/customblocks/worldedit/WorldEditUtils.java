package gg.projecteden.nexus.features.resourcepack.customblocks.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BaseBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor
public class WorldEditUtils {

	protected static class CustomBlockInputParser extends InputParser<BaseBlock> {

		public CustomBlockInputParser() {
			super(WorldEdit.getInstance());
			if (WrappedWorldEdit.loaded) {
				WorldEdit.getInstance().getBlockFactory().register(this);
			}
		}

		@Override
		public Stream<String> getSuggestions(String input, ParserContext context) {
			if (input.isEmpty())
				return Stream.empty();

			return Arrays.stream(CustomBlock.values())
				.map(customBlock -> customBlock.name().toLowerCase())
				.filter(blockName -> blockName.contains(input));
		}

		@Override
		public BaseBlock parseFromInput(String input, ParserContext context) {
			CustomBlock customBlock = Arrays.stream(CustomBlock.values())
				.filter(block -> block.name().equalsIgnoreCase(input))
				.findFirst()
				.orElse(null);

			if (customBlock == null)
				return null;

			BlockData blockData = customBlock.get().getBlockData(BlockFace.UP, null);
			if (blockData == null)
				return null;

			return BukkitAdapter.adapt(blockData).toBaseBlock();
		}
	}

	protected static void pasteSchematic(Location loc, File schematic, Boolean replaceBlocks, Boolean shouldCopyBiomes, Boolean shouldCopyEntities) {
		ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematic);
		if (clipboardFormat == null) return;
		Clipboard clipboard;

		try (final FileInputStream inputStream = new FileInputStream(schematic); ClipboardReader reader = clipboardFormat.getReader(inputStream)) {
			clipboard = reader.read();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			World world = loc.getWorld();
			if (world == null)
				return;

			com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(world);
			EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(adaptedWorld).maxBlocks(-1).build();
			Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
				.to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()))
				.copyBiomes(shouldCopyBiomes).copyEntities(shouldCopyEntities).ignoreAirBlocks(true).build();

			try {
				if (replaceBlocks || getBlocksInSchematic(clipboard, loc).isEmpty())
					Operations.complete(operation);
				editSession.close();
			} catch (WorldEditException ex) {
				Debug.log(ex);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static List<Block> getBlocksInSchematic(Location loc, File schematic) {
		List<Block> list = new ArrayList<>();
		World world = loc.getWorld();
		assert world != null;

		ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematic);
		if (clipboardFormat == null) return list;
		Clipboard clipboard;

		try (final FileInputStream inputStream = new FileInputStream(schematic); ClipboardReader reader = clipboardFormat.getReader(inputStream)) {
			clipboard = reader.read();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return getBlocksInSchematic(clipboard, loc);
	}

	private static List<Block> getBlocksInSchematic(Clipboard clipboard, Location loc) {
		List<Block> list = new ArrayList<>();
		World world = loc.getWorld();
		assert world != null;

		for (int x = clipboard.getMinimumPoint().x(); x <= clipboard.getMaximumPoint().x(); x++) {
			for (int y = clipboard.getMinimumPoint().y(); y <= clipboard.getMaximumPoint().y(); y++) {
				for (int z = clipboard.getMinimumPoint().z(); z <= clipboard.getMaximumPoint().z(); z++) {
					Location offset = new Location(world, x - clipboard.getOrigin().x(), y - clipboard.getOrigin().y(), z - clipboard.getOrigin().z());

					Block block = world.getBlockAt(loc.clone().add(offset));
					if (MaterialTag.REPLACEABLE.isTagged(block.getType()))
						continue;

					if (loc.toBlockLocation().equals(block.getLocation().toBlockLocation()))
						continue;

					list.add(block);
				}
			}
		}
		return list;
	}
}
