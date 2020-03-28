package me.pugabyte.bncore.features.warps.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.JsonBuilder;

import java.util.List;
import java.util.stream.Collectors;

public abstract class _WarpCommand extends CustomCommand {
	WarpService service = new WarpService();

	public _WarpCommand(CommandEvent event) {
		super(event);
	}

	abstract WarpType getWarpType();

	@Path("list")
	void list() {
		List<Warp> warps = service.getWarpsByType(getWarpType());
		JsonBuilder builder = new JsonBuilder();
		for (Warp warp : warps) {
			if (!builder.isInitialized())
				builder.initialize();
			else
				builder.next("&e, ").group();

			builder.next("&3" + warp.getName())
					.command(getAliasUsed() + " tp " + warp.getName())
					.group();
		}
		line();
		send(PREFIX + "&3List of warps &e(Click to teleport)");
		send(builder);
	}

	@Path("set <name>")
	void set(String name) {
		Warp warp = service.get(name, getWarpType());
		if (warp != null)
			error("That warp is already set.");

		Warp newWarp = new Warp(name, player().getLocation(), getWarpType().name());
		service.save(newWarp);
		send(PREFIX + "&e" + name + " &3set to your current location");
	}

	@Path("(rm|remove|delete|del) <name>")
	void delete(Warp warp) {
		service.delete(warp);
		send(PREFIX + "Successfully deleted warp &e" + warp.getName());
	}

	@Path("(teleport|tp) <name>")
	void teleport(Warp warp) {
		warp.teleport(player());
		send(PREFIX + "&3Warping to &e" + warp.getName());
	}

	@Path("<name>")
	void name(Warp warp) {
		teleport(warp);
	}

	@ConverterFor(Warp.class)
	Warp convertToWarp(String value) {
		Warp warp = service.get(value, getWarpType());
		if (warp == null) error("That warp is not set");
		return warp;
	}

	@TabCompleterFor(Warp.class)
	List<String> tabCompleteWarp(String filter) {
		return service.getWarpsByType(getWarpType()).stream()
				.filter(warp -> warp.getName().toLowerCase().startsWith(filter.toLowerCase()))
				.map(Warp::getName)
				.collect(Collectors.toList());
	}

}
