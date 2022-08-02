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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.atomic.AtomicBoolean;

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
		Tasks.repeatAsync(TickTime.SECOND, TickTime.SECOND, () -> {
			final String id = "ClientSideEntities Radius Task";
			new Timer(id, () -> {
				ClientSideConfig.getEntities().forEach((world, entities) -> {
					new Timer(id + " - " + world.getName(), () -> {
						// TODO Remove .region("spawn")
						OnlinePlayers.where().world(world).region("spawn").forEach(player -> {
							new Timer(id + " - " + world.getName() + " - " + player.getName(), () -> {
								final var user = ClientSideUser.of(player);
								for (var entity : entities) {
									String subId = id + " - " + world.getName() + " - " + player.getName() + " - " + entity.getUuid();
									new Timer(subId, () -> {
										if (entity.location() == null)
											return;

										final AtomicBoolean isInRadius = new AtomicBoolean(false);

										new Timer(subId + " - Distance Check", () -> {
											isInRadius.set(distance(entity, player).lte(user.getRadius()));
										});

										if (isInRadius.get())
											new Timer(subId + " - Show", () -> {
												user.show(entity);
											});
										else
											new Timer(subId + " - Hide", () -> {
												user.hide(entity);
											});
									});
								}
							});
						});
					});
				});
			});
		});
	}

}
