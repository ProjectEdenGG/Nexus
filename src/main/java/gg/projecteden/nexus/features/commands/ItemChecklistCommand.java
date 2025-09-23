package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static gg.projecteden.nexus.utils.Nullables.isNotNullOrAir;

public class ItemChecklistCommand extends CustomCommand {
	private static final Map<String, String> CACHE = new HashMap<>();

	public ItemChecklistCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<paste> [radius] [--reverse] [--page] [--clearCache]")
	@Description("Show an item checklist from contents of nearby containers")
	void run(
		String paste,
		@Arg(value = "10", min = 2, max = 25) int radius,
		@Arg("1") @Switch int page,
		@Switch boolean reverse,
		@Switch boolean clearCache
	) {
		var code = paste.replace("https://paste.projecteden.gg/raw/", "");

		if (clearCache)
			CACHE.remove(code);

		var file = CACHE.getOrDefault(code, StringUtils.getPaste(code));

		Map<Material, Integer> list = new HashMap<>();
		for (var line : file.split("\n")) {
			String input = line.split(" ", 2)[1];
			Material material = Material.matchMaterial(input);
			if (material == null)
				error("Invalid material: " + input);
			list.put(material, Integer.parseInt(line.split(" ")[0]));
		}

		Map<Material, Integer> gathered = new HashMap<>();

		for (Block block : BlockUtils.getBlocksInRadius(location(), radius))
			if (block.getState() instanceof Container container)
				for (var content : container.getInventory().getContents())
					if (isNotNullOrAir(content))
						gathered.put(content.getType(), gathered.getOrDefault(content.getType(), 0) + content.getAmount());

		Map<Material, Integer> diff = new HashMap<>(list);
		for (var material : list.keySet())
			diff.put(material, list.get(material) * -1);

		for (var material : gathered.keySet())
			diff.put(material, gathered.get(material) - list.getOrDefault(material, 0));

		for (var material : new HashSet<>(diff.keySet()))
			if (diff.get(material) == 0)
				diff.remove(material);

		var sorted = reverse ? Utils.sortByValueReverse(diff) : Utils.sortByValue(diff);

		CACHE.put(code, file);

		if (sorted.isEmpty()) {
			send(PREFIX + "&a✔ All items accounted for!");
			return;
		}

		new Paginator<Material>()
			.values(sorted.keySet())
			.command("/itemchecklist " + paste + " " + radius + " --reverse=" + reverse + " --page=")
			.formatter((material, index) -> {
				Integer i = sorted.get(material);
				if (i == null)
					return json(index + " &e" + camelCase(material) + " &7- &70");
				else
					return json(index + " &e" + camelCase(material) + " &7- " + (i > 0 ? "&6" + i : "&c" + i));
			})
			.page(page)
			.send();
	}
}

