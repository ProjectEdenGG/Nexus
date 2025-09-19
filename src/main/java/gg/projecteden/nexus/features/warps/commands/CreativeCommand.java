package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.creative.CreativeUser;
import gg.projecteden.nexus.models.creative.CreativeUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.warps.WarpType;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

@Redirect(from = "/plot home", to = "/creative home")
public class CreativeCommand extends CustomCommand {

	public CreativeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Teleport to the creative world spawn")
	CompletableFuture<Boolean> warp() {
		return WarpType.NORMAL.get("creative").teleportAsync(player());
	}

	@Path("home [number] [player]")
	@Description("Visit your or another player's creative plot")
	void home(@Arg("1") int number, @Arg("self") Nerd nerd) {
		runCommand("plot visit %s creative %s".formatted(nerd.getName(), number));
	}

	@Path("trust <player>")
	@Permission(Group.MODERATOR)
	@Description("Allow a Guest to use items with metadata and WorldEdit with restricted materials")
	void trust(CreativeUser user) {
		user.setTrusted(!user.isTrusted());
		new CreativeUserService().save(user);
		send(PREFIX + user.getNickname() + " is now " + (user.isTrusted() ? "&aallowed" : "&cnot allowed") + " to use item metadata and restricted WorldEdit materials");
	}

}
