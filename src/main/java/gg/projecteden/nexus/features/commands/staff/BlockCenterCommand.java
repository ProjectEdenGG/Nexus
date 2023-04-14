package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import org.bukkit.Location;

import static gg.projecteden.nexus.utils.LocationUtils.getCenteredLocation;
import static gg.projecteden.nexus.utils.LocationUtils.getIntercardinalCenteredLocation;

@Aliases("lookcenter")
@Permission(Group.STAFF)
public class BlockCenterCommand extends CustomCommand {
	private final Location centered, intercardinalCentered;

	public BlockCenterCommand(CommandEvent event) {
		super(event);
		centered = getCenteredLocation(location());
		intercardinalCentered = getIntercardinalCenteredLocation(location());
	}

	@NoLiterals
	@Description("Cardinally center yourself on the block you are standing on")
	void center() {
		player().teleportAsync(centered);
	}

	@Description("Intercardinally center yourself on the block you are standing on")
	void intercardinal() {
		player().teleportAsync(intercardinalCentered);
	}

	@Description("Set your yaw to the nearest 90° angle")
	void yaw() {
		Location newLocation = location().clone();
		newLocation.setYaw(centered.getYaw());
		player().teleportAsync(newLocation);
	}

	@Description("Set your yaw to the nearest 45° angle")
	void yaw_intercardinal() {
		Location newLocation = location().clone();
		newLocation.setYaw(intercardinalCentered.getYaw());
		player().teleportAsync(newLocation);
	}

	@Description("Set your pitch to 0")
	void pitch() {
		Location newLocation = location().clone();
		newLocation.setPitch(centered.getPitch());
		player().teleportAsync(newLocation);
	}

	@Description("Set your yaw to the nearest 90° angle and your pitch to 0")
	void look() {
		Location newLocation = location().clone();
		newLocation.setYaw(centered.getYaw());
		newLocation.setPitch(centered.getPitch());
		player().teleportAsync(newLocation);
	}


	@Description("Set your yaw to the nearest 45° angle and your pitch to 0")
	void look_intercardinal() {
		Location newLocation = location().clone();
		newLocation.setYaw(intercardinalCentered.getYaw());
		newLocation.setPitch(intercardinalCentered.getPitch());
		player().teleportAsync(newLocation);
	}

	@Description("Cardinally center yourself on the corner of the block")
	void corner() {
		centered.setX(Math.round(location().getX()));
		centered.setZ(Math.round(location().getZ()));
		player().teleportAsync(centered);
	}

	@Description("Intercardinally center yourself on the corner of the block")
	void corner_intercardinal() {
		intercardinalCentered.setX(Math.round(location().getX()));
		intercardinalCentered.setZ(Math.round(location().getZ()));
		player().teleportAsync(intercardinalCentered);
	}
}
