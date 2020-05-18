package me.pugabyte.bncore.features.atp;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.WorldGuardUtils;

import java.util.List;

@Aliases("atp")
public class AnimalTeleportPensCommand extends CustomCommand {

	WarpService service = new WarpService();

	public AnimalTeleportPensCommand(CommandEvent event) {
		super(event);
	}

	@Permission("group.seniorstaff")
	@Path("(warp|warps) set <warp>")
	void setWarp(String name) {
		Warp warp = service.get(name, WarpType.ATP);
		if (warp != null) error("That warp already exists");
		warp = new Warp(name, player().getLocation(), WarpType.ATP.name());
		service.save(warp);
		send(PREFIX + "Successfully created ATP &e" + name);
	}

	@Permission("group.seniorstaff")
	@Path("(warp|warps) (del|delete|rm|remove) <warp>")
	void delete(String name) {
		Warp warp = service.get(name, WarpType.ATP);
		if (warp == null) error("That warp does not exist");
		service.delete(warp);
		send(PREFIX + "Successfully deleted ATP &e" + name);
	}

	@Path("(warp|warps) list")
	void list() {
		List<Warp> warps;
		if (player().getWorld().getName().equalsIgnoreCase("world"))
			warps = service.getWarpsByType(WarpType.LEGACY_ATP);
		else
			warps = service.getWarpsByType(WarpType.ATP);
		JsonBuilder builder = new JsonBuilder();
		for (Warp warp : warps) {
			if (!builder.isInitialized())
				builder.initialize();
			else
				builder.next("&e, ").group();
			builder.next("&3" + warp.getName());
			if (player().getWorld().getName().equalsIgnoreCase("world"))
				builder.command("atp warp " + warp.getName() + " legacy");
			else
				builder.command("atp warp " + warp.getName());
			builder.group();
		}
		line();
		send("&3List of available ATPs &e(Click to teleport)");
		send(builder);
	}

	@Path("(warp|warps) <warp>")
	void warp(String name) {
		Warp warp = service.get(name, WarpType.ATP);
		if (warp == null) error("That warp does not exist");
		warp.teleport(player());
	}

	@Path("(warp|warps) <warp> legacy")
	void warpLegacy(String name) {
		Warp warp = service.get(name, WarpType.LEGACY_ATP);
		if (warp == null) error("That warp does not exist");
		warp.teleport(player());
	}

	@Path()
	void menu() {
		if (!isInATP())
			error("You are not in an ATP region.");
		if (player().getVehicle() != null) {
			send(PREFIX + "You cannot be riding a mount while using an ATP");
			player().leaveVehicle();
		}
		if (player().getWorld().getName().equalsIgnoreCase("world"))
			new ATPMenu().openLegacy(player());
		else
			new ATPMenu().open(player());
	}

	public boolean isInATP() {
		WorldGuardUtils WGUtils = new WorldGuardUtils(player().getWorld());
		for (ProtectedRegion region : WGUtils.getRegionsAt(player().getLocation())) {
			if (region.getId().contains("atp"))
				return true;
		}
		return false;
	}

}
