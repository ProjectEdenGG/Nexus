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
		runCommand(WorldGuardFlagUtils.command(world, Flags.SNOW_FALL, State.DENY));
		runCommand(WorldGuardFlagUtils.command(world, Flags.SNOW_MELT, State.DENY));
		runCommand(WorldGuardFlagUtils.command(world, Flags.ICE_FORM, State.DENY));
		runCommand(WorldGuardFlagUtils.command(world, Flags.ICE_MELT, State.DENY));
		runCommand(WorldGuardFlagUtils.command(world, Flags.FROSTED_ICE_FORM, State.DENY));
		runCommand(WorldGuardFlagUtils.command(world, Flags.FROSTED_ICE_MELT, State.DENY));
		runCommand(WorldGuardFlagUtils.command(world, Flags.MUSHROOMS, State.DENY));
		runCommand(WorldGuardFlagUtils.command(world, Flags.LEAF_DECAY, State.DENY));
		runCommand(WorldGuardFlagUtils.command(world, Flags.GRASS_SPREAD, State.DENY));
		runCommand(WorldGuardFlagUtils.command(world, Flags.MYCELIUM_SPREAD, State.DENY));
		runCommand(WorldGuardFlagUtils.command(world, Flags.VINE_GROWTH, State.DENY));
		runCommand(WorldGuardFlagUtils.command(world, Flags.CROP_GROWTH, State.DENY));
		runCommand(WorldGuardFlagUtils.command(world, Flags.SOIL_DRY, State.DENY));
		runCommand(WorldGuardFlagUtils.command(world, Flags.CORAL_FADE, State.DENY));
		runCommand(WorldGuardFlagUtils.command(world, WorldGuardFlagUtils.Flags.GRASS_DECAY.get(), State.DENY));
		runCommand(WorldGuardFlagUtils.command(world, WorldGuardFlagUtils.Flags.HANGING_BREAK.get(), State.DENY));
	}

}
