package gg.projecteden.nexus.features.sleep;

import gg.projecteden.nexus.features.sleep.SleepableWorld.State;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.sleep.SleepUtils.canSleep;
import static gg.projecteden.nexus.features.sleep.SleepUtils.getCanSleep;
import static gg.projecteden.nexus.features.sleep.SleepUtils.getSleeping;

public class Sleep extends Feature implements Listener {
	@Getter
	private static final String PREFIX = StringUtils.getPrefix("Sleep");
	@Getter
	private static final long SPEED = 150;
	@Getter
	private static final List<String> enabledWorlds = List.of("world", "survival");
	private static final List<SleepableWorld> sleepableWorlds = new ArrayList<>();

	public static SleepableWorld getSleepableWorld(World world) {
		if (!getEnabledWorlds().contains(world.getName()))
			return null;

		for (SleepableWorld sleepableWorld : sleepableWorlds) {
			if (sleepableWorld.getWorldName().equals(world.getName()))
				return sleepableWorld;
		}
		return new SleepableWorld(world);
	}

	@Override
	public void onStart() {
		for (String enabledWorld : enabledWorlds)
			sleepableWorlds.add(new SleepableWorld(enabledWorld));

		Tasks.repeat(0, TickTime.TICK, () -> {
			for (SleepableWorld sleepableWorld : sleepableWorlds) {
				if (sleepableWorld.isDayTime())
					continue;

				State state = sleepableWorld.getState();
				if (state == State.LOCKED)
					continue;

				World world = sleepableWorld.getWorld();
				int active = getCanSleep(world).size();
				int sleeping = getSleeping(world).size();
				int needed = (int) Math.ceil(active / (100.0 / (double) sleepableWorld.getPercent()));

				// If no one is sleeping, and state is sleeping, remove state
				if (sleeping == 0 && state == State.SLEEPING)
					sleepableWorld.setState(null);

					// If someone is sleeping, and state is null, set state
				else if (sleeping > 0 && state == null)
					sleepableWorld.setState(State.SLEEPING);

				state = sleepableWorld.getState();
				// If necessary amount is sleeping
				if (state != State.SKIPPING && active > 0 && sleeping >= needed)
					sleepableWorld.skipNight();
					// If someone is sleeping, send message
				else if (state == State.SLEEPING)
					sleepableWorld.sendActionBar("Sleepers needed to skip night: &e" + sleeping + "&3/&e" + needed);
			}
		});
	}

	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {
		World world = event.getPlayer().getWorld();

		if (!event.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK))
			return;

		SleepableWorld sleepableWorld = getSleepableWorld(world);
		if (sleepableWorld == null)
			return;
		if (sleepableWorld.isDayTime())
			return;
		if (!sleepableWorld.isDaylightCycleEnabled())
			return;
		if (!canSleep(event.getPlayer()))
			return;

		State state = sleepableWorld.getState();
		if (State.LOCKED.equals(state))
			return;

		if (!State.SKIPPING.equals(state))
			sleepableWorld.setState(State.SLEEPING);
	}

	@EventHandler
	public void onBedLeave(PlayerBedLeaveEvent event) {
		Tasks.wait(1, () -> {
			World world = event.getPlayer().getWorld();

			SleepableWorld sleepableWorld = getSleepableWorld(world);
			if (sleepableWorld == null)
				return;

			if (sleepableWorld.getState() != null)
				return;

			if (getSleeping(world).size() == 0)
				sleepableWorld.setState(null);
		});
	}
}
