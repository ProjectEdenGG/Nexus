package gg.projecteden.nexus.features.hub;

import gg.projecteden.nexus.features.resourcepack.commands.ImageStandCommand.ImageStandInteractEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@NoArgsConstructor
public class Hub extends Feature implements Listener {

	@Override
	public void onStart() {
		new HubTreasureHunt();
	}

	@EventHandler
	public void on(ImageStandInteractEvent event) {
		String id = event.getImageStand().getId();
		if (!id.startsWith("hub_"))
			return;

		id = id.replaceFirst("hub_", "");

		switch (id) {
			case "survival", "minigames", "creative", "oneblock" -> {
				PlayerUtils.send(event.getPlayer(), "/warp " + id);
//				WarpType.NORMAL.get(id).teleportAsync(event.getPlayer());
			}
		}
	}

}
