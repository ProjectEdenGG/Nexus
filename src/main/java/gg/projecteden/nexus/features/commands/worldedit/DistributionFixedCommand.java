package gg.projecteden.nexus.features.commands.worldedit;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.DoubleSlash;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldEditUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@DoubleSlash
@Aliases("distrfixed")
@Permission("worldedit.analysis.distr")
public class DistributionFixedCommand extends CustomCommand {
	private final WorldEditUtils worldedit;
	private final Region region;

	public DistributionFixedCommand(CommandEvent event) {
		super(event);
		worldedit = new WorldEditUtils(player());
		region = worldedit.getPlayerSelection(player());
	}

	@Async
	@Path("[page]")
	@Description("View an accurate (but much slower) block distribution if WorldEdit's is incorrect")
	void run(@Arg("1") int page) {
		if (region == null)
			error("Make a selection first");

		Map<Material, Integer> counts = new HashMap<>();
		Map<Material, Double> percentages = new HashMap<>();

		List<Block> blocks = worldedit.getBlocks(region);

		if (blocks.isEmpty())
			error("No blocks found in selection");

		for (Block block : blocks)
			counts.put(block.getType(), counts.getOrDefault(block.getType(), 0) + 1);

		for (Material material : counts.keySet())
			percentages.put(material, ((double) counts.get(material) / (double) blocks.size()) * 100);

		send(PREFIX + "Selection distribution  |  Total: &e" + percentages.size());

		BiFunction<Material, String, JsonBuilder> formatter = (material, index) ->
				json("&e - " + StringUtils.getDf().format(percentages.get(material)) + "% &7- " + camelCase(material));

		paginate((Utils.sortByValue(percentages)).keySet(), formatter, "/distributionfixed", page);
	}

}

