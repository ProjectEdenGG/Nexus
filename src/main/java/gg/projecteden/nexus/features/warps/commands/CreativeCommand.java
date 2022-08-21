package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.warps.WarpType;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public class CreativeCommand extends CustomCommand {

	public CreativeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	CompletableFuture<Boolean> warp() {
		return WarpType.NORMAL.get("creative").teleportAsync(player());
	}

	@Path("home [number] [player]")
	void home(@Arg("1") int number, @Arg("self") Nerd nerd) {
		warp().thenRun(() -> runCommand("plot visit %s creative %s".formatted(nerd.getName(), number)));
	}

}
