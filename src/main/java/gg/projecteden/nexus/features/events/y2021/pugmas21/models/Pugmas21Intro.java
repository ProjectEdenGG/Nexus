package gg.projecteden.nexus.features.events.y2021.pugmas21.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.pugmas21.Pugmas21User;
import gg.projecteden.nexus.models.pugmas21.Pugmas21UserService;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

public class Pugmas21Intro implements Listener {
	Pugmas21UserService userService = new Pugmas21UserService();
	private static final Location transitionLoc = new Location(Bukkit.getWorld("legacy2"), 0.5, 126.5, -195.5, 0F, 0);
	private static final String TRANSITION_REGION_REGEX = "spawn_pugmas_train_[0-9]+";
	private static final Location introLoc = Pugmas21.location(-7.5, 12.5, -69.5, -90, 0);

	public Pugmas21Intro() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onRegionEnter(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();

		if (WorldGroup.of(player) != WorldGroup.SURVIVAL)
			return;

		if (!Rank.of(player).isStaff() && Pugmas21.isBeforePugmas())
			return;

		if (event.getRegion().getId().matches(TRANSITION_REGION_REGEX)) {
			play(player);
		}
	}

	public void play(Player player) {
		Pugmas21User user = userService.get(player);

		player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS).duration(90).amplifier(250).build());

		Tasks.wait(TickTime.SECOND, () -> {
			player.teleport(transitionLoc);

			Tasks.wait(TickTime.SECOND.x(2), () -> {
				player.teleport(introLoc);
				Pugmas21TrainBackground.start();
				Pugmas21TrainBackground.getChugs().add(player.getUniqueId());

				Tasks.wait(TickTime.SECOND.x(15), () -> {
					Pugmas21TrainBackground.getChugs().remove(player.getUniqueId());
					player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS).duration(80).amplifier(250).build());

					Tasks.wait(TickTime.SECOND, () -> {
						player.teleport(Pugmas21.warp);

						user.setFirstVisit(true);
						userService.save(user);
					});
				});
			});
		});
	}

}
