package gg.projecteden.nexus.features.hub;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.events.PlayerInteractHeadEvent;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.hub.HubTreasureHunter;
import gg.projecteden.nexus.models.hub.HubTreasureHunterService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HubTreasureHunt implements Listener {
	private static final String HEAD_TEXTURE = "c3bdbaedd7d6444e79aa8222f8981240204cc3cc1c9655118687618db8cec";
	static final int TOTAL_TREASURE_CHESTS = 25;

	public HubTreasureHunt() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerInteractHeadEvent event) {
		final Player player = event.getPlayer();

		debug(player, "PlayerInteractHeadEvent:");

		if (Hub.isNotAtHub(player)) {
			debug(player, "<- Not at hub");
			return;
		}

		debug(player, "- At hub");

		Block block = event.getBlock();
		Skull skull = (Skull) block.getState();
		String url = "";

		try {
			url = skull.getPlayerProfile().getTextures().getSkin().toString();
		} catch (Exception ex) {}

		if (!url.contains(HEAD_TEXTURE)) {
			debug(player, "<- Head texture " + url + " !contains " + HEAD_TEXTURE);
			return;
		}

		debug(player, "- Matching Head Texture");

		final String PREFIX = Features.get(Hub.class).getPrefix();
		final HubTreasureHunterService service = new HubTreasureHunterService();
		final HubTreasureHunter hunter = service.get(player);
		Location location = block.getLocation();
		if (hunter.getFound().contains(location)) {
			PlayerUtils.send(player, PREFIX + "&cYou already found that treasure chest");
			new SoundBuilder(Sound.ENTITY_VILLAGER_NO).receiver(player).volume(0.5).play();
			return;
		}

		hunter.getFound().add(location);
		service.save(hunter);

		new ParticleBuilder(Particle.HAPPY_VILLAGER).location(location.toCenterLocation()).offset(0.25, 0.25, 0.25).count(15).spawn();

		final int found = hunter.getFound().size();
		if (found != TOTAL_TREASURE_CHESTS) {
			PlayerUtils.send(player, PREFIX + "You found a treasure chest &7(%s/%s)".formatted(found, TOTAL_TREASURE_CHESTS));
			new SoundBuilder(Sound.ENTITY_PLAYER_LEVELUP).receiver(player).volume(0.5).pitch(2.0).play();
			return;
		}

		new SoundBuilder(Sound.UI_TOAST_CHALLENGE_COMPLETE).receiver(player).volume(0.5).play();
		PlayerUtils.send(player, PREFIX + "You found all the treasure chests!");
		new EventUserService().edit(player, user -> user.giveTokens(100));
	}

	public static void debug(Player player, String debug) {
		if (!HubCommand.debuggers.contains(player))
			return;

		player.sendMessage(debug);
	}

}
