package gg.projecteden.nexus.features.customblocks.worldedit;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import java.util.Arrays;
import java.util.stream.Stream;


public class CustomBlockParser extends InputParser<BaseBlock> {

	public CustomBlockParser(WorldEdit worldEdit) {
		super(worldEdit);
	}

	@Override
	public Stream<String> getSuggestions(String input) {
		return Arrays.stream(CustomBlock.values())
			.map(block -> block.name().toLowerCase())
			.filter(blockName -> blockName.startsWith(input));
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
