package gg.projecteden.nexus.features.commands.staff.admin;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.WorldGuardFlagUtils;
import lombok.NonNull;
import org.bukkit.World;

@Permission(Group.SENIOR_STAFF)
public class CreativeFlagsCommand extends CustomCommand {

	public CreativeFlagsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<world>")
	void run(World world) {
		final String deny = State.DENY.name();
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.SNOW_FALL.getName() + " " + deny);
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.SNOW_MELT.getName() + " " + deny);
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.ICE_FORM.getName() + " " + deny);
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.ICE_MELT.getName() + " " + deny);
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.FROSTED_ICE_FORM.getName() + " " + deny);
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.FROSTED_ICE_MELT.getName() + " " + deny);
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.MUSHROOMS.getName() + " " + deny);
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.LEAF_DECAY.getName() + " " + deny);
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.GRASS_SPREAD.getName() + " " + deny);
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.MYCELIUM_SPREAD.getName() + " " + deny);
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.VINE_GROWTH.getName() + " " + deny);
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.CROP_GROWTH.getName() + " " + deny);
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.SOIL_DRY.getName() + " " + deny);
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.CORAL_FADE.getName() + " " + deny);
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + WorldGuardFlagUtils.Flags.GRASS_DECAY.get().getName() + " " + deny);
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + WorldGuardFlagUtils.Flags.HANGING_BREAK.get().getName() + " " + deny);
	}

}
