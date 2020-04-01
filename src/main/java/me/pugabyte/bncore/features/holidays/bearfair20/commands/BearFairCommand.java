package me.pugabyte.bncore.features.holidays.bearfair20.commands;

import me.pugabyte.bncore.features.warps.commands._WarpCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpType;

public class BearFairCommand extends _WarpCommand {

	public BearFairCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.BEAR_FAIR;
	}

	@Override
	@Path("warps list [filter]")
	public void list(@Arg(tabCompleter = Warp.class) String filter) {
		super.list(filter);
	}

	@Override
	@Path("warps set <name>")
	public void set(@Arg(tabCompleter = Warp.class) String name) {
		super.set(name);
	}

	@Override
	@Path("warps (rm|remove|delete|del) <name>")
	public void delete(Warp warp) {
		super.delete(warp);
	}

	@Override
	@Path("warps (teleport|tp) <name>")
	public void teleport(Warp warp) {
		super.teleport(warp);
	}

	@Override
	@Path("warps <name>")
	public void tp(Warp warp) {
		super.tp(warp);
	}

	@Path("warps tp nearest")
	public void teleportNearest() {
		getNearestWarp(player().getLocation()).teleport(player());
	}

	@Override
	@Path("warps nearest")
	public void nearest() {
		super.nearest();
	}

	@Path
	void bearfair() {
		send(PREFIX + "Coming soon!");
	}
}
