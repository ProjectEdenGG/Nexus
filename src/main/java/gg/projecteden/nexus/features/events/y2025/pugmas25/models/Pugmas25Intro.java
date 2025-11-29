package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Pugmas25Intro implements Listener {

	private static final Pugmas25UserService userService = new Pugmas25UserService();
	private static final Pugmas25 PUGMAS = Pugmas25.get();
	private static final Location TRANSITION_LOC = PUGMAS.location(-641.5, 98, -3246.5, -90, 0);
	private static final Location INTRO_LOC = PUGMAS.location(-641.5, 98, -3236.5, -90, 0);
	public static final String TRANSITION_REGION_REGEX = "hub_pugmas25_train_[0-9]+";

	public Pugmas25Intro() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();

		if (!Rank.of(player).isStaff() && !PUGMAS.isEventActive())
			return;

		if (!event.getRegion().getId().matches(TRANSITION_REGION_REGEX))
			return;

		if (!PlayerUtils.playerHas(player, Pugmas25QuestItem.TRAIN_TICKET.get()))
			return;

		PlayerUtils.removeItem(player, Pugmas25QuestItem.TRAIN_TICKET.get());
		play(player);
	}

	public static void play(Player player) {
		Pugmas25User user = userService.get(player);

		new Cutscene()
			.fade(0, "Boarding train...", 70)
			.next(TickTime.SECOND.x(2), _player -> _player.teleport(TRANSITION_LOC))
			.next(TickTime.SECOND.x(2), _player -> _player.teleport(INTRO_LOC))
			.fade(TickTime.SECOND.x(20), "Deboarding train...", 60)
			.next(TickTime.SECOND, _player -> {
				_player.teleport(PUGMAS.warp);
				new SoundBuilder(Sound.BLOCK_FIRE_EXTINGUISH).receiver(_player).volume(0.75).pitch(0.1).play();
				if (!user.isVisited()) {
					_player.setHealth(20);
					_player.setFoodLevel(20);
					_player.getInventory().clear();
				}
			})
			.next(TickTime.SECOND.x(2), _player -> {
				PUGMAS.send(_player, "You've unlocked the warp to &e" + Pugmas25.EVENT_NAME);
				new SoundBuilder(Sound.ENTITY_PLAYER_LEVELUP).pitch(2).receiver(_player).play();

				user.setVisited(true);
				userService.save(user);
			})
			.next(TickTime.SECOND.x(3), _player -> PUGMAS.send(player, "Talk with the &eTicket Master &3to get started."))
			.start(player);
	}
}
