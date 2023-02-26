package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Location;

import static gg.projecteden.nexus.features.commands.staff.admin.LocationCodeCommand.asJava;
import static gg.projecteden.nexus.utils.LocationUtils.getCenteredLocation;

@Description("Makes the player look in the center of certain directions.")
@Aliases("lookcenter")
@Permission(Group.STAFF)
public class BlockCenterCommand extends CustomCommand {
	Location centered;

	public BlockCenterCommand(CommandEvent event) {
		super(event);
		centered = getCenteredLocation(location());
	}

	@Path
	@Description("Makes the player look in the center of the screen and teleports them to the center of the block they are standing on.")
	void center() {
		player().teleportAsync(centered);
	}

	@Path("yaw")
	@Description("Aligns the player's viewing angle to be horizontally center.")
	void yaw() {
		Location newLocation = location().clone();
		newLocation.setYaw(centered.getYaw());
		player().teleportAsync(newLocation);
	}

	@Path("pitch")
	@Description("Aligns the player's viewing angle to be vertically center.")
	void pitch() {
		Location newLocation = location().clone();
		newLocation.setPitch(centered.getPitch());
		player().teleportAsync(newLocation);
	}

	@Path("look")
	@Description("Makes the player look in the center of the screen.")
	void look() {
		Location newLocation = location().clone();
		newLocation.setYaw(centered.getYaw());
		newLocation.setPitch(centered.getPitch());
		player().teleportAsync(newLocation);
	}

	@Path("corner")
	@Description("Aligns the player in the corner of the block they are standing on and makes them look in the center of the screen.")
	void corner() {
		centered.setX(Math.round(location().getX()));
		centered.setZ(Math.round(location().getZ()));
		player().teleportAsync(centered);
	}

	@Path("java")
	@Description("Sends the Java for if the player ran /blockcenter.")
	void java() {
		send(asJava(getCenteredLocation(location())));
	}
}
