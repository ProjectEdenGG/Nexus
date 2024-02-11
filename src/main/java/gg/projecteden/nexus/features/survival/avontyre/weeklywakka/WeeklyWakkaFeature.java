package gg.projecteden.nexus.features.survival.avontyre.weeklywakka;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.survival.Survival;
import gg.projecteden.nexus.features.survival.avontyre.weeklywakka.WeeklyWakkaUtils.RadiusTier;
import gg.projecteden.nexus.features.survival.avontyre.weeklywakka.WeeklyWakkaUtils.RadiusTier.AppliesResult;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.models.warps.Warps.Warp;
import gg.projecteden.nexus.models.weeklywakka.WeeklyWakka;
import gg.projecteden.nexus.models.weeklywakka.WeeklyWakkaService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.Nullable;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class WeeklyWakkaFeature extends Feature implements Listener {
	private static final Map<Player, WeeklyWakkaData> playerMap = new HashMap<>();

	static class WeeklyWakkaData {
		int ticks = 0;
		int frame = 0;
	}

	@Override
	public void onStart() {
		final int tickIncrement = 2;
		Tasks.repeat(0, TimeUtils.TickTime.TICK.x(tickIncrement), () -> {
			for (Player player : Survival.getPlayersAtSpawn()) {
				if (!WeeklyWakkaUtils.isHoldingTrackingDevice(player))
					continue;

				WeeklyWakkaData data = new WeeklyWakkaData();
				if (playerMap.containsKey(player))
					data = playerMap.remove(player);

				for (RadiusTier tier : RadiusTier.values()) {
					if (tier == RadiusTier.CLOSE) {
						// if player is on same floor (+/- 5 blocks) as NPC
						if (Math.abs(WeeklyWakkaUtils.getNPC().getStoredLocation().getY() - player.getLocation().getY()) > 5)
							tier = RadiusTier.NEAR;
					}

					AppliesResult result = tier.applies(player, data);
					if (result == AppliesResult.CONTINUE)
						continue;

					if (result == AppliesResult.PING_PLAYER) {
						data.ticks = 0;
						tier.ping(player, data);
					}

					break;
				}

				data.ticks += tickIncrement;
				playerMap.put(player, data);
			}
		});
	}

	@EventHandler
	public void onStationaryNPCClick(NPCRightClickEvent event) {
		if (event.getNPC().getId() != WeeklyWakkaUtils.getStationaryNPCId()) return;
		PlayerUtils.runCommandAsOp(event.getClicker(), "weeklywakka info");
	}

	@EventHandler
	public void on(NPCRightClickEvent event) {
		if (event.getNPC().getId() != WeeklyWakkaUtils.getNpcId())
			return;

		WeeklyWakkaService service = new WeeklyWakkaService();
		WeeklyWakka weeklyWakka = service.get0();

		Player player = event.getClicker();
		if (weeklyWakka.getFoundPlayers().contains(player.getUniqueId())) {
			WeeklyWakkaUtils.tell(player, "Hey! You have already found me this week! Try again in " + getNextWeek());
			return;
		}

		WeeklyWakkaUtils.tell(player, "Hey, you found me! Nice job, here's your reward!");
		CrateType.WEEKLY_WAKKA.give(player);

		weeklyWakka.getFoundPlayers().add(player.getUniqueId());
		service.save(weeklyWakka);

		Tasks.wait(TickTime.SECOND.x(5), () ->
			WeeklyWakkaUtils.tell(player, WeeklyWakkaUtils.getTips().get(Integer.parseInt(weeklyWakka.getCurrentTip())).get()));

	}

	private static String getNextWeek() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime next = now.withHour(12).withMinute(0).withSecond(0).withNano(0);

		if (now.getDayOfWeek() != DayOfWeek.SUNDAY || now.getHour() >= 12)
			next = next.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));

		return TimeUtils.Timespan.of(next).format();
	}

	public static void moveNPC() {
		moveNPC(null);
	}

	public static void moveNPC(@Nullable Player debugger) {
		Location location = WeeklyWakkaUtils.getNPC().getStoredLocation().toCenterLocation();

		new SoundBuilder(Sound.ENTITY_FIREWORK_ROCKET_BLAST).location(location).play();
		location.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location, 500, 0.5, 1, 0.5, 0);
		location.getWorld().spawnParticle(Particle.FLASH, location, 10, 0, 0, 0);

		List<Warp> warps = WarpType.WEEKLY_WAKKA.getAll();
		WeeklyWakkaService service = new WeeklyWakkaService();
		WeeklyWakka weeklyWakka = service.get0();
		warps.stream().filter(warp -> warp.getName().equals(weeklyWakka.getCurrentLocation())).findFirst().ifPresent(warps::remove);
		Warp newWarp = RandomUtils.randomElement(warps);
		weeklyWakka.setCurrentLocation(newWarp.getName());

		var tips = WeeklyWakkaUtils.getTips();
		List<Supplier<JsonBuilder>> newTips = new ArrayList<>();
		for (Supplier<JsonBuilder> tip : tips) {
			if (weeklyWakka.getCurrentTip() == null) {
				newTips.addAll(tips);
				break;
			}
			if (!(tips.indexOf(tip) + "").equals(weeklyWakka.getCurrentTip()))
				newTips.add(tip);
		}
		Supplier<JsonBuilder> newTip = RandomUtils.randomElement(newTips);
		weeklyWakka.setCurrentTip(String.valueOf(tips.indexOf(newTip)));

		weeklyWakka.getFoundPlayers().clear();
		service.save(weeklyWakka);

		NPC npc = WeeklyWakkaUtils.getNPC();

		final World world = newWarp.getLocation().getWorld();
		final CompletableFuture<Chunk> oldLocation = world.getChunkAtAsync(npc.getStoredLocation());
		final CompletableFuture<Chunk> newLocation = world.getChunkAtAsync(newWarp.getLocation());
		final Runnable teleport = () -> npc.teleport(newWarp.getLocation(), TeleportCause.PLUGIN);

		CompletableFuture.allOf(oldLocation, newLocation).thenRun(teleport);

		if (debugger != null)
			PlayerUtils.send(debugger, new JsonBuilder("The Weekly Wakka NPC has moved to location #" + newWarp.getName()).command("/weeklywakka " + newWarp.getName()));
	}

}
