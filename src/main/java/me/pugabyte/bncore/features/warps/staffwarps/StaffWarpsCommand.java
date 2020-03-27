package me.pugabyte.bncore.features.warps.staffwarps;

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
public class StaffWarpsCommand extends CustomCommand {

	public StaffWarpsCommand(CommandEvent event) {
		super(event);
	}

	WarpService service = new WarpService();

	@Path("list")
	void list() {
		List<Warp> warps = service.getWarpsByType(WarpType.STAFF);
		JsonBuilder builder = new JsonBuilder();
		for (Warp warp : warps) {
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
	void set(String name) {
		Warp warp = service.getStaffWarp(name);
		if (warp != null) error("That warp is already set.");
		Warp newWarp = new Warp(name, player().getLocation(), WarpType.STAFF.name());
		service.save(newWarp);
		send(PREFIX + "&e" + name + " &3set to your current location");
	}

	@Path("(rm|remove|delete|del) <name>")
	void delete(String name) {
		Warp warp = service.getStaffWarp(name);
		if (warp == null) error("That warp is not set.");
		service.delete(warp);
		send(PREFIX + "Sucessfully deleted the staff warp &e" + warp.getName());
	}

	@Path("(teleport|tp) <name>")
	void teleport(String name) {
		Warp warp = service.getStaffWarp(name);
		if (warp == null) error("That warp is not set.");
		warp.teleport(player());
	}

	@Path("<name>")
	void name(String name) {
		teleport(name);
	}

	@Path("")
	void usage() {
		send(PREFIX + "Not a valid action! Use &c/Warps <list|tp|set|del> [name]");
	}


}
