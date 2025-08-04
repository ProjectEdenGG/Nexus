package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

@Permission(Group.ADMIN)
public class BeaconCommand extends CustomCommand {

	public BeaconCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("set effectRange <range>")
	void set_effectRange(double range) {
		Beacon beacon = getTargetBeacon();
		if (beacon == null)
			return;

		beacon.setEffectRange(range);
		beacon.update();
		send(PREFIX + "Set effect range to " + beacon.getEffectRange() + " blocks");
	}

	@Path("get effectRange")
	void get_effectRange() {
		Beacon beacon = getTargetBeacon();
		if (beacon == null)
			return;

		send(PREFIX + "Effect range is " + beacon.getEffectRange() + " blocks");
	}

	private @Nullable Beacon getTargetBeacon() {
		Block block = getTargetBlockRequired(Material.BEACON);
		if (!(block.getState() instanceof Beacon beacon))
			return null;

		return beacon;
	}
}
