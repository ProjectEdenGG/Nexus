package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class Pugmas25Intro implements Listener {

	private static final Pugmas25UserService userService = new Pugmas25UserService();
	private static final Pugmas25 PUGMAS = Pugmas25.get();
	private static final Location TRANSITION_LOC = PUGMAS.location(-641.5, 98, -3246.5, -90, 0);
	private static final Location INTRO_LOC = PUGMAS.location(-641.5, 98, -3236.5, -90, 0);
	private static final String TRANSITION_REGION_REGEX = "hub_pugmas25_train_[0-9]+";

	@Getter
	private static final ItemStack ticketItem = new ItemBuilder(ItemModelType.VOUCHER)
		.dyeColor(Color.WHITE)
		.name("&3&oTrain Ticket")
		.lore("&3Destination: &e" + Pugmas25.EVENT_NAME)
		.itemFlags(ItemFlags.HIDE_ALL)
		.build();

	public Pugmas25Intro() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();

		if (!event.getRegion().getId().matches(TRANSITION_REGION_REGEX))
			return;

		if (!PlayerUtils.playerHas(player, ticketItem))
			return;

		if (!Rank.of(player).isStaff() && PUGMAS.isBeforeEvent())
			return;

		PlayerUtils.removeItem(player, ticketItem);
		play(player);
	}

	public static void play(Player player) {
		Pugmas25User user = userService.get(player);

		new Cutscene()
			.fade(0, "Boarding train...", 70)
			.next(TickTime.SECOND, _player -> _player.teleport(TRANSITION_LOC))
			.next(TickTime.SECOND.x(2), _player -> _player.teleport(INTRO_LOC))
			.fade(TickTime.SECOND.x(20), "Deboarding train...", 60)
			.next(TickTime.SECOND, _player -> {
				_player.teleport(PUGMAS.warp);
				new SoundBuilder(Sound.BLOCK_FIRE_EXTINGUISH).receiver(_player).pitch(0.1).play();
			})
			.next(TickTime.SECOND.x(2), _player -> {
				PUGMAS.send(_player, "You've unlocked the warp to " + Pugmas25.EVENT_NAME);
				new SoundBuilder(Sound.ENTITY_PLAYER_LEVELUP).pitch(2).receiver(_player).play();

				//user.setVisited(true); // TODO: RELEASE
				userService.save(user);
			})
			.next(TickTime.SECOND.x(3), _player -> PUGMAS.send(player, "Talk with the Ticket Master to get started."))
			.start(player);
	}
}
