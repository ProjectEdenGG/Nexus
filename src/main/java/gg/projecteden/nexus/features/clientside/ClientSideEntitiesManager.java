package gg.projecteden.nexus.features.clientside;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.events.PlayerChangingWorldEvent;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.models.clientside.ClientSideUserService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static gg.projecteden.nexus.utils.Distance.distance;

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
		Tasks.repeat(TickTime.SECOND, TickTime.SECOND, () -> {
			ClientSideConfig.getEntitiesByWorld().forEach((world, entities) -> {
				OnlinePlayers.where().world(world).forEach(player -> {
					final var user = ClientSideUser.of(player);
					for (var entity : entities) {
						if (entity.location() == null)
							continue;

						if (distance(entity, player).lte(user.getRadius()))
							user.show(entity);
						else
							user.hide(entity);
					}
				});
			});
		});
	}

}
