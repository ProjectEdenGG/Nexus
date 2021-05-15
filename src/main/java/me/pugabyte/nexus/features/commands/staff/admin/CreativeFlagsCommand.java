package me.pugabyte.nexus.features.commands.staff.admin;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.WorldGuardFlagUtils;
import org.bukkit.World;

@Permission("group.seniorstaff")
public class CreativeFlagsCommand extends CustomCommand {

	public CreativeFlagsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<world>")
	void run(World world) {
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.SNOW_FALL.getName() + " " + StateFlag.State.DENY.name());
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.SNOW_MELT.getName() + " " + StateFlag.State.DENY.name());
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.ICE_FORM.getName() + " " + StateFlag.State.DENY.name());
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.ICE_MELT.getName() + " " + StateFlag.State.DENY.name());
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.FROSTED_ICE_FORM.getName() + " " + StateFlag.State.DENY.name());
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.FROSTED_ICE_MELT.getName() + " " + StateFlag.State.DENY.name());
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.MUSHROOMS.getName() + " " + StateFlag.State.DENY.name());
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.LEAF_DECAY.getName() + " " + StateFlag.State.DENY.name());
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.GRASS_SPREAD.getName() + " " + StateFlag.State.DENY.name());
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.MYCELIUM_SPREAD.getName() + " " + StateFlag.State.DENY.name());
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.VINE_GROWTH.getName() + " " + StateFlag.State.DENY.name());
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.CROP_GROWTH.getName() + " " + StateFlag.State.DENY.name());
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.SOIL_DRY.getName() + " " + StateFlag.State.DENY.name());
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + Flags.CORAL_FADE.getName() + " " + StateFlag.State.DENY.name());
		runCommand("rg flag -w \"" + world.getName() + "\" __global__ " + WorldGuardFlagUtils.Flags.GRASS_DECAY.get().getName() + " " + StateFlag.State.DENY.name());
	}

}
