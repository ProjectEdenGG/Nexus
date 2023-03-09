package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Location;

import static gg.projecteden.nexus.utils.LocationUtils.getCenteredLocation;

@Aliases("lookcenter")
@Permission(Group.STAFF)
public class BlockCenterCommand extends CustomCommand {
	Location centered;

	public BlockCenterCommand(CommandEvent event) {
		super(event);
		centered = getCenteredLocation(location());
	}

	@Path
	@Description("Centers yourself on the block your are standing on")
	void center() {
		player().teleportAsync(centered);
	}

	@Path("yaw")
	@Description("Set your yaw to the nearest 90° angle")
	void yaw() {
		Location newLocation = location().clone();
		newLocation.setYaw(centered.getYaw());
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

	@Path("corner")
	@Description("Center yourself on the corner of the block")
	void corner() {
		centered.setX(Math.round(location().getX()));
		centered.setZ(Math.round(location().getZ()));
		player().teleportAsync(centered);
	}
}
