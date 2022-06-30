package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.warps.Warps.Warp;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class _WarpSubCommand extends _WarpCommand {

	public _WarpSubCommand(CommandEvent event) {
		super(event);
	}

	@Path("(warp|warps) list [filter]")
	@Description("List available warps")
	public void list(@Arg(tabCompleter = Warp.class) String filter) {
		super.list(filter);
	}

	@Path("(warp|warps) (set|create) <name>")
	@Description("Create a new warp")
	public void set(@Arg(tabCompleter = Warp.class) String name) {
		super.set(name);
	}

	@Path("(warp|warps) reset <name>")
	@Description("Update a warp's location")
	public void reset(@Arg(tabCompleter = Warp.class) String name) {
		super.reset(name);
	}

	@Path("(warp|warps) (rm|remove|delete|del) <name>")
	@Description("Delete a warp")
	public void delete(Warp warp) {
		super.delete(warp);
	}

	@Path("(warp|warps) (teleport|tp|warp) <name>")
	@Description("Teleport to a warp")
	public void teleport(Warp warp) {
		super.teleport(warp);
	}

	@Path("(warp|warps) <name>")
	@Description("Teleport to a warp")
	public void tp(Warp warp) {
		super.tp(warp);
	}

	@Path("(warp|warps) tp nearest")
	@Description("Teleport to the nearest warp")
	public void teleportNearest() {
		super.teleportNearest();
	}

	@Path("(warp|warps) nearest")
	@Description("View the nearest warp")
	public void nearest() {
		super.nearest();
	}

}
