package gg.projecteden.nexus.features.events.y2025.pugmas25.features;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25Config;
import gg.projecteden.nexus.models.pugmas25.Pugmas25ConfigService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Pugmas25Nutcrackers implements Listener {

	public Pugmas25Nutcrackers() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(DecorationInteractEvent event) {
		Player player = event.getPlayer();
		if (!Pugmas25.get().isAtEvent(player))
			return;

		if (event.getDecorationType() != DecorationType.NUTCRACKER_SHORT)
			return;

		if (PlayerUtils.isWGEdit(player))
			return;

		event.setCancelled(true);

		Location location = event.getClickedBlock().getLocation();
		Pugmas25Config config = new Pugmas25ConfigService().get0();
		if (!config.getNutCrackerLocations().contains(location))
			return;

		Pugmas25UserService userService = new Pugmas25UserService();
		Pugmas25User user = userService.get(player);
		if (user.getFoundNutCrackers().contains(location)) {
			PlayerUtils.send(player, Pugmas25.PREFIX + "&cYou already found this mini nutcracker!");
			return;
		}

		user.getFoundNutCrackers().add(location);
		userService.save(user);

		new ParticleBuilder(Particle.END_ROD).receivers(player)
			.location(location.clone().toCenterLocation()).offset(0.25, 0.25, 0.25)
			.count(15)
			.extra(0.01)
			.spawn();

		if (user.getFoundNutCrackers().size() < config.getNutCrackerLocations().size()) {
			new SoundBuilder(Sound.ENTITY_PLAYER_LEVELUP).receiver(player).volume(0.5).pitch(2.0).play();
			return;
		}

		new SoundBuilder(Sound.UI_TOAST_CHALLENGE_COMPLETE).receiver(player).volume(0.5).play();
		PlayerUtils.send(player, Pugmas25.PREFIX + "You found all the mini nutcrackers!");
		PlayerUtils.giveItem(player, Pugmas25QuestItem.SLOT_MACHINE_TOKEN.get());
		new EventUserService().edit(player, eventUser -> eventUser.giveTokens(250));
	}
}
