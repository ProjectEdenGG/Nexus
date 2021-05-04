package me.pugabyte.nexus.features.commands.worldedit;

import com.sk89q.worldedit.regions.Region;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.WorldEditUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static me.pugabyte.nexus.utils.Utils.sortByValue;

@DoubleSlash
@Aliases("distrfixed")
@Permission("worldedit.analysis.distr")
public class DistributionFixedCommand extends CustomCommand {
	private final WorldEditUtils worldEditUtils;
	private final Region region;

	public DistributionFixedCommand(CommandEvent event) {
		super(event);
		worldEditUtils = new WorldEditUtils(player());
		region = worldEditUtils.getPlayerSelection(player());
	}

	@Async
	@Path("[page]")
	void run(@Arg("1") int page) {
		if (region == null)
			error("Make a selection first");

		Map<Material, Integer> counts = new HashMap<>();
		Map<Material, Double> percentages = new HashMap<>();

		List<Block> blocks = worldEditUtils.getBlocks(region);

		if (blocks.isEmpty())
			error("No blocks found in selection");

		for (Block block : blocks)
			counts.put(block.getType(), counts.getOrDefault(block.getType(), 0) + 1);

		for (Material material : counts.keySet())
			percentages.put(material, ((double) counts.get(material) / (double) blocks.size()) * 100);

		send(PREFIX + "Selection distribution  |  Total: &e" + percentages.size());

		BiFunction<Material, String, JsonBuilder> formatter = (material, index) ->
				json("&e - " + StringUtils.getDf().format(percentages.get(material)) + "% &7- " + camelCase(material));

		paginate((sortByValue(percentages)).keySet(), formatter, "/distributionfixed", page);
	}

}

