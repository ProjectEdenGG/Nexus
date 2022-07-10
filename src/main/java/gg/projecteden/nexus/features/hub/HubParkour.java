package gg.projecteden.nexus.features.hub;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.FlyCommand;
import gg.projecteden.nexus.features.commands.SpeedCommand;
import gg.projecteden.nexus.features.commands.SpeedCommand.SpeedChangeEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeavingRegionEvent;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.hub.HubParkourCourse;
import gg.projecteden.nexus.models.hub.HubParkourCourseService;
import gg.projecteden.nexus.models.hub.HubParkourUser;
import gg.projecteden.nexus.models.hub.HubParkourUser.CourseData;
import gg.projecteden.nexus.models.hub.HubParkourUserService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;

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
		if (event.getAction() != Action.PHYSICAL)
			return;

		final Player player = event.getPlayer();
		if (Hub.isNotAtHub(player))
			return;

		final Block block = event.getClickedBlock();
		if (isNullOrAir(block) || block.getType() != Material.LIGHT_WEIGHTED_PRESSURE_PLATE)
			return;

		final Set<ProtectedRegion> regions = Hub.getWorldguard().getRegionsLikeAt(Hub.getBaseRegion() + "_parkour_.*", block.getLocation());

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

		final String PREFIX = Features.get(Hub.class).getPrefix();
		final HubParkourUserService service = new HubParkourUserService();
		final HubParkourUser user = service.get(player);
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
				FlyCommand.off(player);
				SpeedCommand.resetSpeed(player);
				run.setLastCheckpointTime(LocalDateTime.now());
				run.setPlaying(true);
			}
			case "end" -> {
				if (!run.isPlaying())
					return;

				if (run.getCurrentRunSplits().size() != course.getCheckpoints().size() - 1)
					return;

				final Timespan checkpointTimespan = TimespanBuilder.of(run.getLastCheckpointTime()).displayMillis().build();
				run.getCurrentRunSplits().add(checkpointTimespan);
				user.sendMessage(PREFIX + "Reached checkpoint &e#" + course.getCheckpoints().size() + " &3in &e" + checkpointTimespan.format(FormatType.SHORT));

				final Timespan completedTimespan = TimespanBuilder.ofMillis(run.getCurrentRunTime()).displayMillis().build();
				user.sendMessage(PREFIX + "&eCompleted parkour &3in &e" + completedTimespan.format(FormatType.SHORT));

				if (run.getBestRunSplits().isEmpty() || run.getCurrentRunTime() < run.getBestRunTime()) {
					run.setBestRunSplits(new ArrayList<>(run.getCurrentRunSplits()));
					user.sendMessage(PREFIX + "&6New personal best!");
					Tasks.wait(TickTime.SECOND, course::updateHologram);
				}

				run.quit();
			}
			default -> {
				try {
					if (!run.isPlaying())
						return;

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

					final Timespan timespan = TimespanBuilder.of(run.getLastCheckpointTime()).displayMillis().build();
					run.getCurrentRunSplits().add(timespan);
					run.setLastCheckpoint(number);
					run.setLastCheckpointTime(LocalDateTime.now());
					user.sendMessage(PREFIX + "Reached checkpoint &e#" + number + " &3in &e" + timespan.format(FormatType.SHORT));
				} catch (IndexOutOfBoundsException ignore) {}
			}
		}

		service.save(user);
	}

	static {
		for (HubParkourCourse course : new HubParkourCourseService().getAll())
			course.updateHologram();
	}

	@EventHandler
	public void on(PlayerEnteringRegionEvent event) {
		if (Hub.isNotAtHub(event.getPlayer()))
			return;

		final String PREFIX = Features.get(Hub.class).getPrefix();
		if (!event.getRegion().getId().startsWith(Hub.getBaseRegion() + "_parkour_"))
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
			final CourseData run = user.get(course);

			if (!run.isPlaying())
				return;

			run.setLeftStartRegion(false);
			userService.save(user);
			// No /back
			event.getPlayer().teleportAsync(course.getCheckpoints().get(checkpoint));
			user.sendMessage(PREFIX + "Teleported to " + (checkpoint > 0 ? "checkpoint #" + checkpoint : "start"));
		} catch (IndexOutOfBoundsException ignore) {}
	}

	@EventHandler
	public void on(PlayerLeavingRegionEvent event) {
		if (Hub.isNotAtHub(event.getPlayer()))
			return;

		final String PREFIX = Features.get(Hub.class).getPrefix();
		if (!event.getRegion().getId().startsWith(Hub.getBaseRegion() + "_parkour_"))
			return;

		try {
			final String[] split = event.getRegion().getId().split("_", 4);
			final String courseName = split[2];

			if (split.length == 3) {
				new HubParkourUserService().edit(event.getPlayer(), user -> {
					final CourseData run = user.get(courseName);
					if (run.isPlaying()) {
						run.quit();
						user.sendMessage(PREFIX + "&cParkour quit, you left the parkour area");
					}
				});
				return;
			}

			switch (split[3]) {
				case "start" -> new HubParkourUserService().edit(event.getPlayer(), user -> {
					if (user.get(courseName).getLastCheckpointTime() != null)
						user.get(courseName).setLeftStartRegion(true);
				});
			}
		} catch (IndexOutOfBoundsException ignore) {}
	}

	@EventHandler
	public void on(PlayerToggleFlightEvent event) {
		if (Hub.isNotAtHub(event.getPlayer()))
			return;

		if (!event.isFlying())
			return;

		final HubParkourUserService service = new HubParkourUserService();
		final HubParkourUser user = service.get(event.getPlayer());

		if (!user.quitAll(CourseData::reset))
			return;

		PlayerUtils.send(event.getPlayer(), Features.get(Hub.class).getPrefix() + "&cParkour quit, flying is not allowed");
		service.save(user);
	}

	@EventHandler
	public void on(PlayerTeleportEvent event) {
		if (Hub.isNotAtHub(event.getPlayer()))
			return;

		if (event.getCause() == TeleportCause.PLUGIN)
			return;

		final HubParkourUserService service = new HubParkourUserService();
		final HubParkourUser user = service.get(event.getPlayer());

		if (!user.quitAll(CourseData::quit))
			return;

		PlayerUtils.send(event.getPlayer(), Features.get(Hub.class).getPrefix() + "&cParkour quit, teleporting is not allowed");
		service.save(user);
	}

	@EventHandler
	public void on(PlayerGameModeChangeEvent event) {
		if (Hub.isNotAtHub(event.getPlayer()))
			return;

		if (event.getNewGameMode() != GameMode.SPECTATOR)
			return;

		final HubParkourUserService service = new HubParkourUserService();
		final HubParkourUser user = service.get(event.getPlayer());

		if (!user.quitAll(CourseData::reset))
			return;

		PlayerUtils.send(event.getPlayer(), Features.get(Hub.class).getPrefix() + "&cParkour quit, spectator mode is not allowed");
		service.save(user);
	}

	@EventHandler
	public void on(SpeedChangeEvent event) {
		if (Hub.isNotAtHub(event.getPlayer()))
			return;

		if (event.getNewSpeed() == 1)
			return;

		final HubParkourUserService service = new HubParkourUserService();
		final HubParkourUser user = service.get(event.getPlayer());

		if (!user.quitAll(CourseData::quit))
			return;

		PlayerUtils.send(event.getPlayer(), Features.get(Hub.class).getPrefix() + "&cParkour quit, changing speed is not allowed");
		service.save(user);
	}

}
