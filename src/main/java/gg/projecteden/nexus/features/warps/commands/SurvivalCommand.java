package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.features.warps.Warps;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public class SurvivalCommand extends CustomCommand {

	public SurvivalCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	CompletableFuture<Boolean> warp() {
		return Warps.survival(player());
	}

}
