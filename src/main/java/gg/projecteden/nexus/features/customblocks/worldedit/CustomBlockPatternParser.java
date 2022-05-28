package gg.projecteden.nexus.features.customblocks.worldedit;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import java.util.Arrays;
import java.util.stream.Stream;


public class CustomBlockPatternParser extends InputParser<Pattern> {

	public CustomBlockPatternParser(WorldEdit worldEdit) {
		super(worldEdit);
	}

	// works
	@Override
	public Stream<String> getSuggestions(String input) {
		return Arrays.stream(CustomBlock.values())
			.filter($ -> $.name().toLowerCase().startsWith(input))
			.map($ -> $.name().toLowerCase())
			.toList()
			.stream();
	}

	// doesn't seem to get called
	@Override
	public BaseBlock parseFromInput(String input, ParserContext context) throws InputParseException {
		Dev.WAKKA.send("parsing from input: " + input);

		CustomBlock customBlock = Arrays.stream(CustomBlock.values())
			.filter($ -> $.name().equalsIgnoreCase(input))
			.findFirst()
			.orElse(null);

		if (customBlock == null) {
			Dev.WAKKA.send("Custom Block == null, input: " + input);
			return null;
		}

		BlockData blockData = customBlock.get().getBlockData(BlockFace.UP, null);
		BlockState newBlock = BukkitAdapter.adapt(blockData);

		return newBlock.toBaseBlock();
//		return new CustomBlockPattern(context.getExtent(), context, customBlock);
	}


}
