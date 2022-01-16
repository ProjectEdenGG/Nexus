package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.preconfigured.NoPermissionException;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.models.warps.Warps;
import gg.projecteden.nexus.models.warps.Warps.Warp;
import gg.projecteden.nexus.models.warps.WarpsService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.utils.Utils.MinMaxResult;
import lombok.NoArgsConstructor;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.Utils.getMin;

@NoArgsConstructor
public abstract class _WarpCommand extends CustomCommand {
	protected final WarpsService service = new WarpsService();
	protected final Warps warps = service.get0();

	public _WarpCommand(CommandEvent event) {
		super(event);
	}

	public abstract WarpType getWarpType();

	public String getPermission() {
		return null;
	}

	private void checkPermission() {
		if (!isPlayer())
			return;

		String permission = getPermission();
		if (!isNullOrEmpty(permission))
			if (!sender().hasPermission(permission))
				throw new NoPermissionException();
	}

	protected void save() {
		service.save(warps);
	}

	@Path("(list|warps) [filter]")
	public void list(@Arg(tabCompleter = Warp.class) String filter) {
		checkPermission();
		List<String> warps = tabCompleteWarp(filter);
		JsonBuilder builder = new JsonBuilder();
		for (String warp : warps) {
			if (!builder.isInitialized())
				builder.initialize();
			else
				builder.next("&e, ").group();

			builder.next("&3" + warp)
					.command(getAliasUsed() + " tp " + warp)
					.group();
		}
		line();
		send(PREFIX + "&3List of warps &e(Click to teleport)");
		send(builder);
	}

	@Path("(set|create) <name>")
	@Permission(Group.STAFF)
	public void set(@Arg(tabCompleter = Warp.class) String name) {
		checkPermission();
		Warp warp = getWarpType().get(name);
		if (warp != null)
			error("That warp is already set.");

		getWarpType().add(name, location());
		save();
		send(PREFIX + "&e" + name + " &3set to your current location");
	}

	@Path("reset <name>")
	@Permission(Group.STAFF)
	public void reset(@Arg(tabCompleter = Warp.class) String name) {
		checkPermission();
		getWarpType().delete(name);
		getWarpType().add(name, location());
		save();
		send(PREFIX + "&e" + name + " &3set to your current location");
	}

	@Path("(rm|remove|delete|del) <name>")
	@Permission(Group.STAFF)
	public void delete(Warp warp) {
		checkPermission();
		warps.delete(warp);
		save();
		send(PREFIX + "Successfully deleted warp &e" + warp.getName());
	}

	@Path("(teleport|tp|warp) <name>")
	public void teleport(Warp warp) {
		checkPermission();
		if (warp == null)
			error("That warp is not set");
		warp.teleportAsync(player());
		send(PREFIX + "&3Warping to &e" + warp.getName());
	}

	@Path("<name>")
	public void tp(Warp warp) {
		checkPermission();
		teleport(warp);
	}

	@Path("tp nearest")
	public void teleportNearest() {
		checkPermission();
		getNearestWarp(location()).ifPresent(this::teleport);
	}

	@Path("nearest")
	public void nearest() {
		checkPermission();
		Optional<Warp> warp = getNearestWarp(location());
		if (!warp.isPresent())
			error("No nearest warp found");
		send(PREFIX + "Nearest warp is &e" + warp.get().getName() + " &3(&e" + (int) warp.get().getLocation().distance(location()) + " &3blocks away)");
	}

	public Optional<Warp> getNearestWarp(Location location) {
		List<Warp> warps = getWarpType().getAll();

		MinMaxResult<Warp> result = getMin(warps, warp -> {
			if (location == null || location.getWorld() == null || warp.getLocation() == null) return null;
			if (!location.getWorld().equals(warp.getLocation().getWorld())) return null;
			return location.distance(warp.getLocation());
		});

		return Optional.ofNullable(result.getObject());
	}

	@ConverterFor(Warp.class)
	Warp convertToWarp(String value) {
		if ("skyblock".equalsIgnoreCase(value))
			error("&cSkyblock is currently disabled while we update it");

		Warp warp = getWarpType().get(value);
		if (warp == null)
			error("That warp is not set");

		return warp;
	}

	@TabCompleterFor(Warp.class)
	List<String> tabCompleteWarp(String filter) {
		return getWarpType().getAll().stream()
				.map(Warp::getName)
				.filter(name -> filter == null || name.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

}
