package gg.projecteden.nexus.features.events.store;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.WorldGroup;
import me.arcaniax.hdb.api.PlayerClickHeadEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

import static gg.projecteden.nexus.features.events.Events.STORE_PREFIX;

public class EventStoreListener implements Listener {

	public EventStoreListener() {
		Nexus.registerListener(this);
	}

	private static final List<WorldGroup> hdb_allowedWorldGroups = Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK, WorldGroup.ONEBLOCK);
	private static final List<WorldGroup> hdb_bypassWorldGroups = Arrays.asList(WorldGroup.CREATIVE, WorldGroup.STAFF);

	@EventHandler
	public void onPlayerClickHeadDatabase(PlayerClickHeadEvent event) {
		final Player player = event.getPlayer();
		final WorldGroup worldGroup = WorldGroup.of(player);
		final Rank rank = Rank.of(player);

		final EventUserService service = new EventUserService();
		final EventUser user = service.get(player);

		int price = EventStoreItem.HEADS.getPrice();

		if (rank.isAdmin())
			return;

		if (rank.isStaff() && player.getGameMode() == GameMode.CREATIVE)
			return;

		if (hdb_bypassWorldGroups.contains(worldGroup))
			return;

		if (!hdb_allowedWorldGroups.contains(worldGroup)) {
			event.setCancelled(true);
			user.sendMessage(STORE_PREFIX + "You can not purchase heads here");
			return;
		}

		if (!user.hasTokens(price)) {
			event.setCancelled(true);
			user.sendMessage(STORE_PREFIX + "&cYou do not have enough tokens to purchase a head");
			return;
		}

		user.takeTokens(price);
		service.save(user);
	}

}
