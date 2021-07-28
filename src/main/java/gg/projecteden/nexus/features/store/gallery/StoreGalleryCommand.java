package gg.projecteden.nexus.features.store.gallery;

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
}
