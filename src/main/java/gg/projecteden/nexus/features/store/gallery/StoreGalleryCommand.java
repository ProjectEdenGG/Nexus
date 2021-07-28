package gg.projecteden.nexus.features.store.gallery;

import gg.projecteden.nexus.features.store.gallery.StoreGalleryNPCs.DisplaySet;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@Permission("group.admin")
public class StoreGalleryCommand extends CustomCommand {

	public StoreGalleryCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("updateSkins")
	void updateSkins() {
		StoreGalleryNPCs.updateSkins();
		send(PREFIX + "Updated skins");
	}

	@Path("debug displays")
	void debug_displays() {
		for (DisplaySet display : StoreGalleryNPCs.getDisplays()) {
			send(display.getId() + ":");
			send(" 1: " + display.getDisplay1().getId());
			send(" 2: " + display.getDisplay2().getId());
			send(" 3: " + display.getDisplay3().getId());
		}
	}

}
