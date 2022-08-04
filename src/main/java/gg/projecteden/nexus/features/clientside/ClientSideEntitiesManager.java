package gg.projecteden.nexus.features.clientside;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.events.PlayerChangingWorldEvent;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.models.clientside.ClientSideUserService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Timer;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.BoundingBox;

public class ClientSideEntitiesManager implements Listener {
	private static final ClientSideUserService userService = new ClientSideUserService();

	public ClientSideEntitiesManager() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		userService.get(event.getPlayer()).hideAll();

		Tasks.wait(5, () -> userService.get(event.getPlayer()).showAll());
	}

	@EventHandler
	public void on(PlayerChangedWorldEvent event) {
		userService.get(event.getPlayer()).showAll();
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		userService.get(event.getPlayer()).hideAll();
	}

	@EventHandler
	public void on(PlayerChangingWorldEvent event) {
		userService.get(event.getPlayer()).hideAll();
	}

	static {
		Tasks.repeatAsync(TickTime.SECOND, TickTime.SECOND, () -> {
			final String id = "ClientSideEntities Radius Task";
			new Timer(id, () -> {
				ClientSideConfig.getEntities().forEach((world, entities) -> {
					if (entities.isEmpty())
						return;

					OnlinePlayers.where().world(world).forEach(player -> {
						new Timer(id + " - " + world.getName() + " - " + player.getName(), () -> {
							final var user = ClientSideUser.of(player);
							final int radius = user.getRadius();
							final BoundingBox box = BoundingBox.of(player.getLocation(), radius, radius, radius);
							for (var entity : entities) {
								final Location location = entity.location();
								if (location == null)
									continue;

								if (box.contains(location.toVector()))
									user.show(entity);
								else if (user.canAlreadySee(entity))
									user.hide(entity);
							}
						});
					});
				});
			});
		});
	}

}
