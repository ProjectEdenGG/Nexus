package me.pugabyte.bncore.features.warps.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.JsonBuilder;

import java.util.List;

@Permission("group.staff")
public class ArenasCommand extends CustomCommand {

	public ArenasCommand(CommandEvent event) {
		super(event);
	}

	WarpType warpType = WarpType.ARENA;
	WarpService service = new WarpService();

	@Path("list")
	void list() {
		List<Warp> warps = service.getWarpsByType(warpType);
		JsonBuilder builder = new JsonBuilder();
		for (Warp warp : warps) {
			if (!builder.isInitialized())
				builder.initialize();
			else
				builder.next("&e, ").group();
			builder.next("&3" + warp.getName())
					.command("arenas tp " + warp.getName())
					.group();
		}
		line();
		send("&3List of arenas &e(Click to teleport)");
		send(builder);
	}

	@Path("set <name>")
	void set(String name) {
		Warp warp = service.get(name, warpType);
		if (warp != null) error("That arena is already set.");
		Warp newWarp = new Warp(name, player().getLocation(), warpType.name());
		service.save(newWarp);
		send(PREFIX + "&e" + name + " &3set to your current location");
	}

	@Path("(rm|remove|delete|del) <name>")
	void delete(String name) {
		Warp warp = service.get(name, warpType);
		if (warp == null) error("That arena is not set.");
		service.delete(warp);
		send(PREFIX + "Successfully deleted the arena &e" + warp.getName());
	}

	@Path("(teleport|tp) <name>")
	void teleport(String name) {
		Warp warp = service.get(name, warpType);
		if (warp == null) error("That arena is not set.");
		warp.teleport(player());
	}

	@Path("<name>")
	void name(String name) {
		teleport(name);
	}

	@Path("")
	void usage() {
		send(PREFIX + "Not a valid action! Use &c/arenas <list|tp|set|del> [name]");
	}

}
