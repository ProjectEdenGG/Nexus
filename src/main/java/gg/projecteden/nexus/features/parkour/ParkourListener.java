package gg.projecteden.nexus.features.parkour;

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
import gg.projecteden.nexus.features.minigames.models.events.lobby.MinigamerUseGadgetEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeavingRegionEvent;
import gg.projecteden.nexus.models.parkour.LobbyParkourCourse;
import gg.projecteden.nexus.models.parkour.LobbyParkourCourseService;
import gg.projecteden.nexus.models.parkour.LobbyParkourUser;
import gg.projecteden.nexus.models.parkour.LobbyParkourUser.CourseData;
import gg.projecteden.nexus.models.parkour.LobbyParkourUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class ParkourListener implements Listener {
	final String PREFIX = StringUtils.getPrefix("Parkour");
	public static final ItemStack RESET_ITEM = new ItemBuilder(Material.POISONOUS_POTATO).name("Reset Parkour").build();

	public ParkourListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onClickPotato(PlayerInteractEvent event) {
		if (!ActionGroup.LEFT_CLICK.applies(event))
			return;

		if (!EquipmentSlot.HAND.equals(event.getHand()))
			return;

		final Player player = event.getPlayer();
		ItemStack tool = ItemUtils.getTool(player);
		if (Nullables.isNullOrAir(tool))
			return;

		if (!tool.equals(RESET_ITEM))
			return;

		PlayerUtils.runCommandAsOp(player, "parkour reset");
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (event.getAction() != Action.PHYSICAL)
			return;

		final Player player = event.getPlayer();

		final Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block) || block.getType() != Material.LIGHT_WEIGHTED_PRESSURE_PLATE)
			return;

		final Set<ProtectedRegion> regions = new WorldGuardUtils(player).getRegionsLikeAt("lobby_parkour_.*", block.getLocation());

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

		if (Nullables.isNullOrEmpty(courseName) || Nullables.isNullOrEmpty(checkpoint))
			return;

		final LobbyParkourUserService service = new LobbyParkourUserService();
		final LobbyParkourUser user = service.get(player);
		final LobbyParkourCourse course = new LobbyParkourCourseService().get(UUID.nameUUIDFromBytes(courseName.getBytes()));

		final CourseData run = user.get(courseName);

		switch (checkpoint) {
			case "start" -> {
				run.getCurrentRunSplits().clear();
				run.setLastCheckpoint(0);
				if (run.isLeftStartRegion() || run.getLastCheckpointTime() == null) {
					user.sendMessage(PREFIX + "Started parkour. Reach the end as fast as you can!");
					run.setLeftStartRegion(false);

					if (!PlayerUtils.playerHas(user, RESET_ITEM))
						PlayerUtils.giveItem(user, RESET_ITEM);
				}
				FlyCommand.off(player, ParkourListener.class);
				SpeedCommand.resetSpeed(player, "Parkour#start");
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
		for (LobbyParkourCourse course : new LobbyParkourCourseService().getAll())
			course.updateHologram();
	}

	@EventHandler
	public void on(PlayerEnteringRegionEvent event) {
		if (!event.getRegion().getId().startsWith("lobby_parkour_"))
			return;

		try {
			final String[] split = event.getRegion().getId().split("_", 4);
			if (!split[3].matches("kill_\\d+"))
				return;

			final String courseName = split[2];

			final LobbyParkourCourse course = new LobbyParkourCourseService().get(UUID.nameUUIDFromBytes(courseName.getBytes()));
			final LobbyParkourUserService userService = new LobbyParkourUserService();
			final LobbyParkourUser user = userService.get(event.getPlayer());
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
		if (!event.getRegion().getId().startsWith("lobby_parkour_"))
			return;

		try {
			final String[] split = event.getRegion().getId().split("_", 4);
			final String courseName = split[2];

			if (split.length == 3) {
				new LobbyParkourUserService().edit(event.getPlayer(), user -> {
					final CourseData run = user.get(courseName);
					if (run.isPlaying()) {
						run.quit();
						user.sendMessage(PREFIX + "&cParkour quit, you left the parkour area");
						user.getOnlinePlayer().getInventory().remove(RESET_ITEM);
					}
				});
				return;
			}

			if ("start".equals(split[3])) {
				new LobbyParkourUserService().edit(event.getPlayer(), user -> {
					if (user.get(courseName).getLastCheckpointTime() != null)
						user.get(courseName).setLeftStartRegion(true);
				});
			}
		} catch (IndexOutOfBoundsException ignore) {}
	}

	@EventHandler
	public void on(PlayerToggleFlightEvent event) {
		if (!event.isFlying())
			return;

		final LobbyParkourUserService service = new LobbyParkourUserService();
		final LobbyParkourUser user = service.get(event.getPlayer());

		if (!user.quitAll(CourseData::reset))
			return;

		PlayerUtils.send(event.getPlayer(), PREFIX + "&cParkour quit, flying is not allowed");
		service.save(user);
	}

	@EventHandler
	public void on(PlayerTeleportEvent event) {
		if (event.getCause() == TeleportCause.PLUGIN)
			return;

		final LobbyParkourUserService service = new LobbyParkourUserService();
		final LobbyParkourUser user = service.get(event.getPlayer());

		if (!user.quitAll(CourseData::quit))
			return;

		PlayerUtils.send(event.getPlayer(), PREFIX + "&cParkour quit, teleporting is not allowed");
		service.save(user);
	}

	@EventHandler
	public void on(PlayerGameModeChangeEvent event) {
		if (event.getNewGameMode() != GameMode.SPECTATOR)
			return;

		final LobbyParkourUserService service = new LobbyParkourUserService();
		final LobbyParkourUser user = service.get(event.getPlayer());

		if (!user.quitAll(CourseData::reset))
			return;

		PlayerUtils.send(event.getPlayer(), PREFIX + "&cParkour quit, spectator mode is not allowed");
		service.save(user);
	}

	@EventHandler
	public void on(SpeedChangeEvent event) {
		if (event.getNewSpeed() == 1)
			return;

		final LobbyParkourUserService service = new LobbyParkourUserService();
		final LobbyParkourUser user = service.get(event.getPlayer());

		if (!user.quitAll(CourseData::quit))
			return;

		PlayerUtils.send(event.getPlayer(), PREFIX + "&cParkour quit, changing speed is not allowed");
		service.save(user);
	}

	@EventHandler
	public void on(EntityToggleGlideEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		if (!event.isGliding())
			return;

		final LobbyParkourUserService service = new LobbyParkourUserService();
		final LobbyParkourUser user = service.get(player);

		if (!user.quitAll(CourseData::quit))
			return;

		PlayerUtils.send(player, PREFIX + "&cParkour quit, gliding is not allowed");
		service.save(user);
	}

	@EventHandler
	public void on(MinigamerUseGadgetEvent event) {
		final Player player = event.getMinigamer().getOnlinePlayer();

		final LobbyParkourUserService service = new LobbyParkourUserService();
		final LobbyParkourUser user = service.get(player);

		if (!user.quitAll(CourseData::quit))
			return;

		PlayerUtils.send(player, PREFIX + "&cParkour quit, gadgets are not allowed");
		service.save(user);
	}

}
