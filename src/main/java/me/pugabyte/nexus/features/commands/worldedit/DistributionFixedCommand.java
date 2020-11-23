package me.pugabyte.nexus.features.commands.worldedit;

import com.sk89q.worldedit.regions.Region;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.WorldEditUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	@Path()
	void run() {
		Map<Material, Integer> counts = new HashMap<>();
		Map<Material, Double> percentages = new HashMap<>();

		List<Block> blocks = worldEditUtils.getBlocks(region);
		for (Block block : blocks)
			counts.put(block.getType(), counts.getOrDefault(block.getType(), 0) + 1);

		for (Material material : counts.keySet())
			percentages.put(material, ((double) counts.get(material) / (double) blocks.size()) * 100);

		sortByValue(percentages).forEach((material, percentage) ->
				send("&e - " + StringUtils.getDf().format(percentage) + "% &7- " + material));
	}

}

