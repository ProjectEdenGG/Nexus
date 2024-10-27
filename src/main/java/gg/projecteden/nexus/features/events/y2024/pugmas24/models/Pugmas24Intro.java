package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.pugmas24.Pugmas24User;
import gg.projecteden.nexus.models.pugmas24.Pugmas24UserService;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Pugmas24Intro implements Listener {

	private static final Pugmas24UserService userService = new Pugmas24UserService();
	private static final Pugmas24 PUGMAS = Pugmas24.get();
	private static final Location TRANSITION_LOC = PUGMAS.location(-641.5, 98, -3246.5, -90, 0);
	private static final Location INTRO_LOC = PUGMAS.location(-641.5, 98, -3236.5, -90, 0);
	private static final String TRANSITION_REGION_REGEX = "hub_pugmas24_train_[0-9]+";

	public Pugmas24Intro() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();

		if (!event.getRegion().getId().matches(TRANSITION_REGION_REGEX))
			return;

		if (!Rank.of(player).isStaff() && PUGMAS.isBeforeEvent())
			return;

		play(player);
	}

	public static void play(Player player) {
		Pugmas24User user = userService.get(player);

		PUGMAS.fadeToBlack(player, "Boarding train...", 90);

		Tasks.wait(TickTime.SECOND, () -> {
			player.teleport(TRANSITION_LOC);

			Tasks.wait(TickTime.SECOND.x(2), () -> {
				player.teleport(INTRO_LOC);

				Tasks.wait(TickTime.SECOND.x(30), () -> {
					PUGMAS.fadeToBlack(player, "Deboarding train...", 80);

					Tasks.wait(TickTime.SECOND, () -> {
						player.teleport(PUGMAS.warp);

						//user.setVisited(true); // TODO: RELEASE
						userService.save(user);
					});
				});
			});
		});
	}
}
