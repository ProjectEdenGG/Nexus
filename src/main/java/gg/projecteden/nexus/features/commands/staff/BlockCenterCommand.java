package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Location;

import static gg.projecteden.nexus.features.commands.staff.admin.LocationCodeCommand.asJava;
import static gg.projecteden.nexus.utils.LocationUtils.getCenteredLocation;

@Aliases("lookcenter")
@Permission("group.staff")
public class BlockCenterCommand extends CustomCommand {
	Location centered;

	public BlockCenterCommand(CommandEvent event) {
		super(event);
		centered = getCenteredLocation(location());
	}

	@Path
	void center() {
		player().teleport(centered);
	}

	@Path("yaw")
	void yaw() {
		Location newLocation = location().clone();
		newLocation.setYaw(centered.getYaw());
		player().teleport(newLocation);
	}

	@Path("pitch")
	void pitch() {
		Location newLocation = location().clone();
		newLocation.setPitch(centered.getPitch());
		player().teleport(newLocation);
	}

	@Path("look")
	void look() {
		Location newLocation = location().clone();
		newLocation.setYaw(centered.getYaw());
		newLocation.setPitch(centered.getPitch());
		player().teleport(newLocation);
	}

	@Path("corner")
	void corner() {
		centered.setX(Math.round(location().getX()));
		centered.setZ(Math.round(location().getZ()));
		player().teleport(centered);
	}

	@Path("java")
	void java() {
		send(asJava(getCenteredLocation(location())));
	}
}
