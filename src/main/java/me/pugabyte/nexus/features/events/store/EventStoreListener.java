package me.pugabyte.nexus.features.events.store;

import me.arcaniax.hdb.api.PlayerClickHeadEvent;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.eventuser.EventUser;
import me.pugabyte.nexus.models.eventuser.EventUserService;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

public class EventStoreListener implements Listener {
	private static final String PREFIX = StringUtils.getPrefix("Event Store");

	public EventStoreListener() {
		Nexus.registerListener(this);
	}

	private static final List<WorldGroup> hdb_allowedWorldGroups = Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK, WorldGroup.ONEBLOCK);
	private static final List<WorldGroup> hdb_bypassWorldGroups = Arrays.asList(WorldGroup.CREATIVE, WorldGroup.STAFF);

	@EventHandler
	public void onPlayerClickHeadDatabase(PlayerClickHeadEvent event) {
		Player player = event.getPlayer();
		WorldGroup worldGroup = WorldGroup.of(player);
		EventUserService service = new EventUserService();
		EventUser user = service.get(player);
		int price = Purchasable.HEADS.getPrice();

		if (Rank.of(player).isAdmin())
			return;

		if (hdb_bypassWorldGroups.contains(worldGroup))
			return;

		if (!hdb_allowedWorldGroups.contains(worldGroup)) {
			event.setCancelled(true);
			user.sendMessage(PREFIX + "You can not purchase heads here");
			return;
		}

		if (!user.hasTokens(price)) {
			event.setCancelled(true);
			user.sendMessage(PREFIX + "&cYou do not have enough tokens to purchase a head");
			return;
		}

		user.takeTokens(price);
		service.save(user);
	}

}
