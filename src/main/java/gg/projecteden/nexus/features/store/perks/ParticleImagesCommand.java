package gg.projecteden.nexus.features.store.perks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

public class ParticleImagesCommand extends CustomCommand {

	public ParticleImagesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("List available particle images")
	void run() {
		runCommand("powder emojis");
	}

}
