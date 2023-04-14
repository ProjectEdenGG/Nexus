package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.warps.WarpType;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public class CreativeCommand extends CustomCommand {

	public CreativeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Teleport to the creative world spawn")
	CompletableFuture<Boolean> warp() {
		return WarpType.NORMAL.get("creative").teleportAsync(player());
	}

	@Path("home [number] [player]")
	@Description("Visit your or another player's creative plot")
	void home(@Optional("1") int number, @Optional("self") Nerd nerd) {
		warp().thenRun(() -> runCommand("plot visit %s creative %s".formatted(nerd.getName(), number)));
	}

}
