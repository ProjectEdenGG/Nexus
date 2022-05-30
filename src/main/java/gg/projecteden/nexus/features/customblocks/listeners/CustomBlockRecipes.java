package gg.projecteden.nexus.features.customblocks.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomBlockRecipes implements Listener {

	public CustomBlockRecipes() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent ignored) {
		for (CustomBlock customBlock : CustomBlock.values())
			customBlock.registerRecipes();
	}
}
