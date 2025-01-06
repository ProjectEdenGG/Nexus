package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.LocationUtils;
import org.bukkit.Location;

@Aliases("lookcenter")
@Permission(Group.STAFF)
public class BlockCenterCommand extends CustomCommand {
	private final Location centered, intercardinalCentered;

	public BlockCenterCommand(CommandEvent event) {
		super(event);
		centered = LocationUtils.getCenteredLocation(location());
		intercardinalCentered = LocationUtils.getIntercardinalCenteredLocation(location());
	}

	@Path
	@Description("Cardinally center yourself on the block you are standing on")
	void center() {
		player().teleportAsync(centered);
	}

	@Path("intercardinal")
	@Description("Intercardinally center yourself on the block you are standing on")
	void intercardinal() {
		player().teleportAsync(intercardinalCentered);
	}

	@Path("yaw")
	@Description("Set your yaw to the nearest 90° angle")
	void yaw() {
		Location newLocation = location().clone();
		newLocation.setYaw(centered.getYaw());
		player().teleportAsync(newLocation);
	}

	@Path("yaw intercardinal")
	@Description("Set your yaw to the nearest 45° angle")
	void yaw_intercardinal() {
		Location newLocation = location().clone();
		newLocation.setYaw(intercardinalCentered.getYaw());
		player().teleportAsync(newLocation);
	}

	@Path("pitch")
	@Description("Set your pitch to 0")
	void pitch() {
		Location newLocation = location().clone();
		newLocation.setPitch(centered.getPitch());
		player().teleportAsync(newLocation);
	}

	@Path("look")
	@Description("Set your yaw to the nearest 90° angle and your pitch to 0")
	void look() {
		Location newLocation = location().clone();
		newLocation.setYaw(centered.getYaw());
		newLocation.setPitch(centered.getPitch());
		player().teleportAsync(newLocation);
	}


	@Path("look intercardinal")
	@Description("Set your yaw to the nearest 45° angle and your pitch to 0")
	void look_intercardinal() {
		Location newLocation = location().clone();
		newLocation.setYaw(intercardinalCentered.getYaw());
		newLocation.setPitch(intercardinalCentered.getPitch());
		player().teleportAsync(newLocation);
	}

	@Path("corner")
	@Description("Cardinally center yourself on the corner of the block")
	void corner() {
		centered.setX(Math.round(location().getX()));
		centered.setZ(Math.round(location().getZ()));
		player().teleportAsync(centered);
	}

	@Path("corner intercardinal")
	@Description("Intercardinally center yourself on the corner of the block")
	void corner_intercardinal() {
		intercardinalCentered.setX(Math.round(location().getX()));
		intercardinalCentered.setZ(Math.round(location().getZ()));
		player().teleportAsync(intercardinalCentered);
	}
}
