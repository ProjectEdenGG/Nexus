package gg.projecteden.nexus.features.resourcepack.customblocks.worldedit;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WorldEditListener implements Listener {

	@EventHandler
	public void onTabComplete(AsyncTabCompleteEvent event) {
		List<String> args = Arrays.stream(event.getBuffer().split(" ")).toList();
		if (!event.getBuffer().startsWith("//") || args.isEmpty()) return;

		List<String> ids = getSuggestions(args.getLast());

		ids.addAll(event.getCompletions());
		event.setCompletions(ids);
	}

	public List<String> getSuggestions(String input) {
		if (input.isEmpty())
			return new ArrayList<>();

		return Arrays.stream(CustomBlock.values())
			.map(customBlock -> customBlock.name().toLowerCase())
			.filter(blockName -> blockName.contains(input))
			.collect(Collectors.toList());
	}
}
