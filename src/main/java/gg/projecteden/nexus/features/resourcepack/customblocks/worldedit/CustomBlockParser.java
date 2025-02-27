package gg.projecteden.nexus.features.resourcepack.customblocks.worldedit;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.ICustomTripwire;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import java.util.Arrays;
import java.util.stream.Stream;

public class CustomBlockParser<P> extends InputParser<Pattern> {

	public CustomBlockParser(WorldEdit worldEdit) {
		super(worldEdit);
	}

	public static boolean isCustomBlockType(BlockType type) {
		if (type == BlockTypes.NOTE_BLOCK)
			return true;

		// TODO: Disable tripwire customblocks
		if (!ICustomTripwire.isNotEnabled() && type == BlockTypes.TRIPWIRE)
			return true;
		//

		return false;
	}

	@Override
	public Stream<String> getSuggestions(String input) {
		if (input.isEmpty())
			return Stream.empty();

		return Arrays.stream(CustomBlock.values())
				.map(customBlock -> customBlock.name().toLowerCase())
				.filter(blockName -> blockName.contains(input));
	}

	@Override
	public BaseBlock parseFromInput(String input, ParserContext context) throws InputParseException {
		CustomBlock customBlock = Arrays.stream(CustomBlock.values())
			.filter(block -> block.name().equalsIgnoreCase(input))
			.findFirst()
			.orElse(null);

		if (customBlock == null)
			return null;

		BlockData blockData = customBlock.get().getBlockData(BlockFace.UP, null);
		BlockState newBlock = BukkitAdapter.adapt(blockData);

		return newBlock.toBaseBlock();
	}

}
