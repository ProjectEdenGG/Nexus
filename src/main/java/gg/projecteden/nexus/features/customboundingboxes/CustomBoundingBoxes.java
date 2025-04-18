package gg.projecteden.nexus.features.customboundingboxes;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.nexus.features.customboundingboxes.events.CustomBoundingBoxEntityInteractEvent;
import gg.projecteden.nexus.features.customboundingboxes.events.CustomBoundingBoxEntityTargetEndEvent;
import gg.projecteden.nexus.features.customboundingboxes.events.CustomBoundingBoxEntityTargetStartEvent;
import gg.projecteden.nexus.features.customboundingboxes.events.CustomBoundingBoxEntityTargetTickEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntity;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntityService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
public class CustomBoundingBoxes extends Feature implements Listener {
	private static final CustomBoundingBoxEntityService service = new CustomBoundingBoxEntityService();
	private static boolean CACHE_LOADED = false;
	private static final Map<UUID, List<UUID>> ENTITIES_TO_PROCESS = new HashMap<>();

	@Override
	public void onStart() {
		service.cacheAll();
		CACHE_LOADED = true;
		ENTITIES_TO_PROCESS.forEach((worldUuid, entities) -> {
			var world = Bukkit.getWorld(worldUuid);
			if (world == null)
				return;

			entities.forEach(entityUuid -> {
				var entity = world.getEntity(entityUuid);
				if (entity == null || !entity.isValid())
					return;

				onLoad(entityUuid);
			});
		});


		for (World world : Bukkit.getWorlds())
			for (Entity entity : world.getEntities())
				onLoad(entity.getUniqueId());

		Map<UUID, UUID> targetEntities = new HashMap<>();

		Tasks.repeat(0, 1, () -> {
			for (Player player : OnlinePlayers.getAll()) {
				final CustomBoundingBoxEntity targetEntity = service.getTargetEntity(player);
				if (targetEntity == null || !targetEntity.hasCustomBoundingBox()) {
					var previous = targetEntities.remove(player.getUniqueId());

					if (previous != null)
						new CustomBoundingBoxEntityTargetEndEvent(player, service.get(previous)).callEvent();

					continue;
				}

				boolean changed = false;
				if (!targetEntities.containsKey(player.getUniqueId()))
					changed = true;
				else if (!targetEntities.get(player.getUniqueId()).equals(targetEntity.getUuid()))
					changed = true;

				var previous = targetEntities.put(player.getUniqueId(), targetEntity.getUuid());

				if (changed) {
					if (previous != null)
						new CustomBoundingBoxEntityTargetEndEvent(player, service.get(previous)).callEvent();

					new CustomBoundingBoxEntityTargetStartEvent(player, targetEntity).callEvent();
				}

				new CustomBoundingBoxEntityTargetTickEvent(player, targetEntity).callEvent();
			}
		});
	}

	@EventHandler
	public void onEntityAddToWorld(EntityAddToWorldEvent event) {
		onLoad(event.getWorld(), event.getEntity().getUniqueId());
	}

	private static void onLoad(World world, UUID uuid) {
		Tasks.async(() -> {
			if (!CACHE_LOADED) {
				ENTITIES_TO_PROCESS.computeIfAbsent(world.getUID(), $ -> new ArrayList<>()).add(uuid);
				return;
			}

			onLoad(uuid);
		});
	}

	private static void onLoad(UUID uuid) {
		final CustomBoundingBoxEntity entity = service.getCache().get(uuid);
		if (entity == null)
			return;

		if (entity.hasCustomBoundingBox())
			Tasks.sync(entity::updateBoundingBox);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final CustomBoundingBoxEntity entity = service.getTargetEntity(player);

		if (entity == null)
			return;

		if (!new CustomBoundingBoxEntityInteractEvent(player, entity, event).callEvent())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerInteractAtEntityEvent event) {
		final Player player = event.getPlayer();
		final CustomBoundingBoxEntity entity = service.getTargetEntity(player);

		if (entity == null)
			return;

		if (!new CustomBoundingBoxEntityInteractEvent(player, entity, event).callEvent())
			event.setCancelled(true);
	}

}
