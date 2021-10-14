package gg.projecteden.nexus.features.events.mobevents;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.mobevents.events.NewDayEvent;
import gg.projecteden.nexus.features.events.mobevents.events.NewNightEvent;
import gg.projecteden.nexus.features.events.mobevents.types.common.DayPhase;
import gg.projecteden.nexus.features.events.mobevents.types.common.IMobEvent;
import gg.projecteden.nexus.features.sleep.SkipNightEvent;
import gg.projecteden.nexus.features.sleep.Sleep;
import gg.projecteden.nexus.features.sleep.SleepableWorld;
import gg.projecteden.nexus.features.sleep.SleepableWorld.State;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.utils.RandomUtils;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MobEventsListener implements Listener {

	public MobEventsListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onNewDay(NewDayEvent event) {
		MobEventUtils.debug("New Day Event");
		World world = event.getWorld();
		IMobEvent mobEvent = MobEventUtils.getCurrentEvent(world);
		if (mobEvent != null)
			return;

		MobEventUtils.queueEvent(world, DayPhase.DAY);
	}

	@EventHandler
	public void onNewNight(NewNightEvent event) {
		MobEventUtils.debug("New Night Event");
		World world = event.getWorld();
		IMobEvent mobEvent = MobEventUtils.getCurrentEvent(world);
		if (mobEvent != null)
			return;

		MobEventUtils.queueEvent(world, DayPhase.NIGHT);
	}

	@EventHandler
	public void onSkipNight(SkipNightEvent event) {
		World world = event.getWorld();
		IMobEvent mobEvent = MobEventUtils.getCurrentEvent(world);
		if (mobEvent == null)
			return;

		if (mobEvent.isActive()) {
			MobEventUtils.debug("Skipping night, ending event " + mobEvent.getName());
			mobEvent.endEvent(world);
		}
	}

	@EventHandler
	public void onMobDamage(EntityDamageEvent event) {
		if (!event.getCause().equals(DamageCause.FALL))
			return;

		if (event.getEntity().hasMetadata(MobEvents.METADATA_NO_FALL_DAMAGE))
			event.setCancelled(true);
	}

	@EventHandler
	public void onSleepInBed(PlayerBedEnterEvent event) {
		if (!event.getBedEnterResult().equals(BedEnterResult.OK))
			return;

		World world = event.getBed().getWorld();
		if (!DayPhase.of(world).equals(DayPhase.NIGHT))
			return;

		IMobEvent mobEvent = MobEventUtils.getCurrentEvent(world);
		if (mobEvent == null || mobEvent.getModifier().canBeSkipped())
			return;

		SleepableWorld sleepableWorld = Sleep.getSleepableWorld(world);
		if (sleepableWorld == null)
			return;

		if (sleepableWorld.getPercent() > 0 || sleepableWorld.getState() == State.LOCKED)
			return;

		event.setCancelled(true);
		PlayerUtils.send(event.getPlayer(), "&cCan't skip this night.");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityTransform(EntityTransformEvent event) {
		Entity entity = event.getEntity();
		IMobEvent mobEvent = MobEventUtils.getCurrentEvent(entity.getWorld());
		if (mobEvent == null)
			return;

		if (!mobEvent.getSpawnedEntities().contains(entity.getUniqueId()))
			return;

		List<UUID> newUUIDs = event.getTransformedEntities().stream().map(Entity::getUniqueId).collect(Collectors.toList());
		newUUIDs.add(event.getTransformedEntity().getUniqueId());

		mobEvent.getSpawnedEntities().addAll(newUUIDs);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		IMobEvent mobEvent = MobEventUtils.getCurrentEvent(entity.getWorld());
		if (mobEvent == null || !mobEvent.isActive())
			return;

		if (!mobEvent.getSpawnedEntities().contains(entity.getUniqueId()))
			return;

		if (RandomUtils.chanceOf(50))
			return;

		event.getDrops().clear();
	}

}
