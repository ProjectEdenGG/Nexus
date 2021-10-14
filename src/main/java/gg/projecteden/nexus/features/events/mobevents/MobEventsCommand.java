package gg.projecteden.nexus.features.events.mobevents;

import gg.projecteden.nexus.features.events.mobevents.types.common.IMobEvent;
import gg.projecteden.nexus.features.events.mobevents.types.common.MobEventType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.World;

@Permission("group.admin")
public class MobEventsCommand extends CustomCommand {

	public MobEventsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("forceQueue <type> [world]")
	void forceQueue(MobEventType mobEventType, @Arg("current") World world) {
		MobEventUtils.queueEvent(world, mobEventType, player());
	}

	@Path("forceStart [world]]")
	void forceStart(@Arg("current") World world) {
		IMobEvent mobEvent = MobEventUtils.getCurrentEvent(world);
		if (mobEvent == null)
			error("No mob events are queued in world " + world.getName());

		mobEvent.startEvent(world, player());
	}

	@Path("forceEnd [world]]")
	void forceEnd(@Arg("current") World world) {
		IMobEvent mobEvent = MobEventUtils.getCurrentEvent(world);
		if (mobEvent == null || mobEvent.isActive())
			error("No mob events are queued or active in world " + world.getName());

		mobEvent.startEvent(world, player());
	}

	@Path("info [world]")
	void info(@Arg("current") World world) {
		if (!MobEvents.enabledWorlds.contains(world.getName().toLowerCase()))
			error("Mob Events are not enabled in " + world.getName());

		IMobEvent mobEvent = MobEventUtils.getCurrentEvent(world);
		if (mobEvent == null)
			error("There is no event queued in " + world.getName());

		MobEventType modifier = mobEvent.getModifier();

		send("Current Event: " + mobEvent.getName());
		send("Active: " + mobEvent.isActive());
		send("Start Time: " + modifier.getStartTime());
		send("Duration: " + modifier.getDuration());
		send("Skippable: " + modifier.canBeSkipped());
	}

	@Path("debug [enabled]")
	void debug(Boolean enabled) {
		if (enabled == null)
			enabled = !MobEvents.isDebug();

		MobEvents.setDebug(enabled);

		send(PREFIX + (enabled ? "&aEnabled" : "&cDisabled"));
	}
}
