package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.staffwarp.StaffWarp;
import me.pugabyte.bncore.models.staffwarp.StaffWarpService;
import me.pugabyte.bncore.utils.JsonBuilder;

import java.util.List;

@Permission("group.staff")
public class StaffWarpsCommand extends CustomCommand {
	StaffWarpService service = new StaffWarpService();

	public StaffWarpsCommand(CommandEvent event) {
		super(event);
	}

	@Path("list")
	void list() {
		List<StaffWarp> warps = service.getStaffWarps();
		JsonBuilder builder = new JsonBuilder();
		for (StaffWarp warp : warps) {
			if (!builder.isInitialized())
				builder.initialize();
			else
				builder.next("&e, ").group();
			builder.next("&3" + warp.getName())
					.command("staffwarps tp " + warp.getName())
					.group();
		}
		line();
		send("&3List of available staff warps &e(Click to teleport)");
		send(builder);
	}

	@Path("set <name>")
	void run(String name) {
		StaffWarp warp = service.get(name);
		if (warp != null) error("That warp is already set.");
		StaffWarp newWarp = new StaffWarp(name, player().getLocation());
		service.save(newWarp);
		send(PREFIX + "&e" + name + " &3set to your current location");
	}

	@Path("(rm|remove|delete|del) <name>")
	void delete(String name) {
		StaffWarp warp = service.get(name);
		if (warp == null) error("That warp is not set.");
		service.delete(warp);
		send(PREFIX + "Sucessfully deleted the staff warp &e" + warp.getName());
	}

	@Path("(teleport|tp) <name>")
	void teleport(String name) {
		StaffWarp warp = service.get(name);
		if (warp == null) error("That warp is not set.");
		player().teleport(warp.getLocation());
	}

	@Path("<name>")
	void name(String name) {
		teleport(name);
	}

	@Path("")
	void usage() {
		send(PREFIX + "Not a valid action! Use &c/staffwarps <list|tp|set|del> [name]");
	}


}
