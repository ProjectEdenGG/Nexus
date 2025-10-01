package gg.projecteden.nexus.features.commands.staff.operator;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.WorldGuardFlagUtils.CustomFlags;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NonNull;
import org.bukkit.World;

import java.util.List;
import java.util.function.Consumer;

@Permission(Group.SENIOR_STAFF)
public class CreativeFlagsCommand extends CustomCommand {

	public CreativeFlagsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	public static void setFlags(World world) {
		final ProtectedRegion globalRegion = new WorldGuardUtils(world).getManager().getRegion("__global__");
		if (globalRegion == null)
			throw new InvalidInputException("Could not find global region for " + world.getName());

		setFlags(globalRegion);
	}

	private static final List<Consumer<ProtectedRegion>> setters = List.of(
		region -> region.setFlag(Flags.SNOW_FALL, State.DENY),
		region -> region.setFlag(Flags.SNOW_MELT, State.DENY),
		region -> region.setFlag(Flags.ICE_FORM, State.DENY),
		region -> region.setFlag(Flags.ICE_MELT, State.DENY),
		region -> region.setFlag(Flags.FROSTED_ICE_FORM, State.DENY),
		region -> region.setFlag(Flags.FROSTED_ICE_MELT, State.DENY),
		region -> region.setFlag(Flags.MUSHROOMS, State.DENY),
		region -> region.setFlag(Flags.LEAF_DECAY, State.DENY),
		region -> region.setFlag(Flags.GRASS_SPREAD, State.DENY),
		region -> region.setFlag(Flags.MYCELIUM_SPREAD, State.DENY),
		region -> region.setFlag(Flags.VINE_GROWTH, State.DENY),
		region -> region.setFlag(Flags.CROP_GROWTH, State.DENY),
		region -> region.setFlag(Flags.SOIL_DRY, State.DENY),
		region -> region.setFlag(Flags.CORAL_FADE, State.DENY),
		region -> region.setFlag(CustomFlags.GRASS_DECAY.get(), State.DENY),
		region -> region.setFlag(CustomFlags.SAPLING_GROWTH.get(), State.DENY),
		region -> region.setFlag(CustomFlags.HANGING_BREAK.get(), State.DENY),
		region -> region.setFlag(CustomFlags.BLOCK_GROW.get(), State.DENY),
		region -> region.setFlag(CustomFlags.BLOCK_FADE.get(), State.DENY)
	);

	public static void setFlags(ProtectedRegion protectedRegion) {
		setters.forEach(consumer -> consumer.accept(protectedRegion));
	}

	@Path("<world>")
	@Description("Set default WorldGuard flags on a world to prevent unwanted decay/growth/etc")
	void run(World world) {
		setFlags(world);
	}

}
