package gg.projecteden.nexus.features.hub;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.hub.HubParkourCourse;
import gg.projecteden.nexus.models.hub.HubParkourCourseService;
import gg.projecteden.nexus.models.hub.HubParkourUser;
import gg.projecteden.nexus.models.hub.HubParkourUser.CourseData;
import gg.projecteden.nexus.models.hub.HubParkourUserService;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.utils.TimeUtils.Timespan;
import gg.projecteden.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

public class HubParkour implements Listener {

	public HubParkour() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		final String PREFIX = Features.get(Hub.class).getPrefix();
		if (event.getAction() != Action.PHYSICAL)
			return;

		final Block block = event.getClickedBlock();
		if (isNullOrAir(block) || block.getType() != Material.LIGHT_WEIGHTED_PRESSURE_PLATE)
			return;

		final WorldGuardUtils worldguard = new WorldGuardUtils(block);
		final Set<ProtectedRegion> regions = worldguard.getRegionsLikeAt("hub_parkour_.*", block.getLocation());

		String courseName = null;
		String checkpoint = null;
		for (ProtectedRegion region : regions) {
			try {
				final String[] split = region.getId().split("_", 4);
				courseName = split[2];
				checkpoint = split[3];
				break;
			} catch (ArrayIndexOutOfBoundsException ignore) {}
		}

		if (isNullOrEmpty(courseName) || isNullOrEmpty(checkpoint))
			return;

		final HubParkourUserService service = new HubParkourUserService();
		final HubParkourUser user = service.get(event.getPlayer());
		final HubParkourCourse course = new HubParkourCourseService().get(UUID.nameUUIDFromBytes(courseName.getBytes()));

		final CourseData run = user.get(courseName);
		switch (checkpoint) {
			case "start" -> {
				run.getCurrentRunSplits().clear();
				run.setLastCheckpoint(0);
				if (run.isLeftStartRegion() || run.getLastCheckpointTime() == null) {
					user.sendMessage(PREFIX + "Started parkour. Reach the end as fast as you can!");
					run.setLeftStartRegion(false);
				}
				run.setLastCheckpointTime(LocalDateTime.now());
			}
			case "end" -> {
				if (run.getCurrentRunSplits().size() != course.getCheckpoints().size() - 1)
					return;

				final Timespan timespan = Timespan.of(run.getLastCheckpointTime());
				run.getCurrentRunSplits().add(timespan);
				user.sendMessage(PREFIX + "Reached checkpoint &e#" + course.getCheckpoints().size() + " &3in &e" + timespan.format(FormatType.SHORT));
				user.sendMessage(PREFIX + "&eCompleted parkour &3in &e" + Timespan.ofMillis(run.getCurrentRunTime()).format(FormatType.SHORT));

				if (run.getBestRunSplits().isEmpty() || run.getCurrentRunTime() < run.getBestRunTime()) {
					run.setBestRunSplits(new ArrayList<>(run.getCurrentRunSplits()));
					user.sendMessage(PREFIX + "&6New personal best!");
				}

				run.getCurrentRunSplits().clear();
				run.setLastCheckpoint(0);
				run.setLastCheckpointTime(null);
			}
			default -> {
				try {
					final String[] split = checkpoint.split("_", 2);
					if (!"checkpoint".equals(split[0]))
						return;

					if (!Utils.isInt(split[1]))
						return;

					int number = Integer.parseInt(split[1]);

					if (run.getLastCheckpointTime() == null)
						return;

					if (run.getLastCheckpoint() >= number)
						return;

					if (run.getCurrentRunSplits().size() != number - 1)
						return;

					final Timespan timespan = Timespan.of(run.getLastCheckpointTime());
					run.getCurrentRunSplits().add(timespan);
					run.setLastCheckpoint(number);
					run.setLastCheckpointTime(LocalDateTime.now());
					user.sendMessage(PREFIX + "Reached checkpoint &e#" + number + " &3in &e" + timespan.format(FormatType.SHORT));
				} catch (IndexOutOfBoundsException ignore) {}
			}
		}

		service.save(user);
	}

	@EventHandler
	public void on(PlayerEnteringRegionEvent event) {
		final String PREFIX = Features.get(Hub.class).getPrefix();
		if (!event.getRegion().getId().startsWith("hub_parkour_"))
			return;

		try {
			final String[] split = event.getRegion().getId().split("_", 4);
			if (!split[3].matches("kill_\\d+"))
				return;

			final String courseName = split[2];

			final HubParkourCourse course = new HubParkourCourseService().get(UUID.nameUUIDFromBytes(courseName.getBytes()));
			final HubParkourUserService userService = new HubParkourUserService();
			final HubParkourUser user = userService.get(event.getPlayer());
			final int checkpoint = user.get(courseName).getLastCheckpoint();
			user.get(course).setLeftStartRegion(false);
			userService.save(user);
			event.getPlayer().teleportAsync(course.getCheckpoints().get(checkpoint));
			user.sendMessage(PREFIX + "Teleported to " + (checkpoint > 0 ? "checkpoint #" + checkpoint : "start"));
		} catch (IndexOutOfBoundsException ignore) {}
	}

	@EventHandler
	public void on(PlayerLeftRegionEvent event) {
		if (!event.getRegion().getId().startsWith("hub_parkour_"))
			return;

		try {
			final String[] split = event.getRegion().getId().split("_", 4);
			if (!"start".equals(split[3]))
				return;

			final String courseName = split[2];

			new HubParkourUserService().edit(event.getPlayer(), user -> {
				if (user.get(courseName).getLastCheckpointTime() != null)
					user.get(courseName).setLeftStartRegion(true);
			});
		} catch (IndexOutOfBoundsException ignore) {}
	}

}
