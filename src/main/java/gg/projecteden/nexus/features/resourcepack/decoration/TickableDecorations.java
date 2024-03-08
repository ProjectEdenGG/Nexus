package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.TickableDecoration;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationDestroyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPlacedEvent;
import gg.projecteden.nexus.models.decoration.DecorationUser;
import gg.projecteden.nexus.models.decoration.DecorationUserService;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TickableDecorations implements Listener {
	List<WorldGroup> activeWorldGroups = List.of(WorldGroup.SURVIVAL, WorldGroup.STAFF, WorldGroup.CREATIVE);
	DecorationUserService userService = new DecorationUserService();

	public TickableDecorations() {
		Nexus.registerListener(this);

		Tasks.repeat(0, TimeUtils.TickTime.TICK.x(4), () -> {
			for (DecorationUser user : userService.getAll()) {
				for (DecorationUser.Tickable tickable : user.getTickableDecorations()) {
					if (!tickable.getLocation().isWorldLoaded())
						continue;

					if (!tickable.getLocation().isChunkLoaded())
						continue;

					DecorationConfig config = DecorationConfig.of(tickable.getConfigId());
					if (!(config instanceof TickableDecoration _tickable))
						continue;

					if (_tickable.shouldTick())
						_tickable.tick(tickable.getLocation());
				}
			}
		});

		// Janitor
		Tasks.repeat(0, TimeUtils.TickTime.SECOND.x(30), () -> {
			for (DecorationUser user : userService.getAll()) {
				Iterator<DecorationUser.Tickable> tickables = user.getTickableDecorations().iterator();
				while (tickables.hasNext()) {
					DecorationUser.Tickable tickable = tickables.next();

					if (!tickable.getLocation().isWorldLoaded())
						continue;

					if (!tickable.getLocation().isChunkLoaded())
						continue;

					DecorationConfig config = DecorationConfig.of(tickable.getConfigId());
					if (!(config instanceof TickableDecoration))
						continue;

					AtomicBoolean found = new AtomicBoolean(false);
					tickable.getLocation().getNearbyEntitiesByType(ItemFrame.class, 1).forEach(itemFrame -> {
						if (!itemFrame.getUniqueId().equals(tickable.getUuid()))
							return;

						found.set(true);
					});

					if (!found.get()) {
						tickables.remove();
					}
				}

				userService.save(user);
			}
		});
	}

	@EventHandler
	public void onPlaceTickable(DecorationPlacedEvent event) {
		if (!activeWorldGroups.contains(WorldGroup.of(event.getLocation())))
			return;

		Decoration decoration = event.getDecoration();
		DecorationConfig config = decoration.getConfig();
		if (!(config instanceof TickableDecoration)) {
			return;
		}

		DecorationUser user = userService.get(event.getPlayer());
		user.addTickable(decoration.getItemFrame(), config.getId());
		userService.save(user);
	}

	@EventHandler
	public void onBreakTickable(DecorationDestroyEvent event) {
		Decoration decoration = event.getDecoration();
		if (!(decoration.getConfig() instanceof TickableDecoration))
			return;

		DecorationUser user = userService.get(decoration.getOwner(event.getPlayer()));
		user.removeTickable(decoration.getItemFrame().getUniqueId());
		userService.save(user);
	}
}
