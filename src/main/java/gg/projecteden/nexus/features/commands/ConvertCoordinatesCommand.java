package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.Location;

public class ConvertCoordinatesCommand extends CustomCommand {
	public String PREFIX = StringUtils.getPrefix("Coords");

	public ConvertCoordinatesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		final Location location = location();
		final double x = location.getX();
		final double y = location.getY();
		final double z = location.getZ();

		int otherY = (int) y;

		switch (world().getEnvironment()) {
			case NETHER -> {
				int otherX = (int) (x * 8);
				int otherZ = (int) (z * 8);
				send(PREFIX + "Overworld equivalent to %d %d %d: &e%d %d %d".formatted((int) x, (int) y, (int) z, otherX, otherY, otherZ));
			}
			case NORMAL -> {
				int otherX = (int) (x / 8);
				int otherZ = (int) (z / 8);
				send(PREFIX + "Nether equivalent to %d %d %d: &e%d %d %d".formatted((int) x, (int) y, (int) z, otherX, otherY, otherZ));
			}
			default -> error("This command is only supported in the Overworld and Nether");
		}
	}

}
