package gg.projecteden.nexus.features.commands.staff.admin;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntity;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntityService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

@NoArgsConstructor
@Permission(Group.ADMIN)
public class CustomBoundingBoxCommand extends CustomCommand implements Listener {
	private static final CustomBoundingBoxEntityService service = new CustomBoundingBoxEntityService();
	private CustomBoundingBoxEntity targetEntity;

	public CustomBoundingBoxCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private CustomBoundingBoxEntity getEntity(String id) {
		if (!isNullOrEmpty(id))
			targetEntity = service.getById(id);

		targetEntity = service.get(getTargetEntityRequired());
		return targetEntity;
	}

	@Path("init")
	@Description("Create a custom bounding box entity")
	void init() {
		getEntity(null);

		if (targetEntity.hasCustomBoundingBox())
			error("That " + camelCase(targetEntity.getEntityType()) + " already has a custom bounding box");

		targetEntity.createBoundingBox();
		service.save(targetEntity);
		service.cache(targetEntity);
		send(PREFIX + "Created bounding box");

		draw(null, false);
	}

	@Path("delete [--id]")
	@Description("Delete a custom bounding box")
	void delete(@Switch String id) {
		getEntity(id);

		targetEntity.stopDrawing();
		service.delete(targetEntity);
		send(PREFIX + "Deleted custom bounding box");
	}

	@Path("id <id> [--id]")
	@Description("Update a custom bounding box entity's ID")
	void id(String newId, @Switch String id) {
		getEntity(id);

		targetEntity.setId(newId);
		service.save(targetEntity);
		send(PREFIX + "Set id to &e" + newId);
	}

	@Path("update [--id]")
	@Description("Apply the custom bounding box to an entity")
	void update(@Switch String id) {
		getEntity(id);

		if (!targetEntity.hasCustomBoundingBox())
			error("That " + camelCase(targetEntity.getEntityType()) + " does not have a custom bounding box");

		targetEntity.updateBoundingBox();
		send(PREFIX + "Updated bounding box");
	}

	@Path("modify [--id] [--x] [--y] [--z] [--posX] [--posY] [--posZ] [--negX] [--negY] [--negZ] [--all]")
	@Description("Modify the bounds of a custom bounding box")
	void boundingBox_modify(
		@Switch String id,
		@Switch double x, @Switch double y, @Switch double z,
		@Switch double negX, @Switch double negY, @Switch double negZ,
		@Switch double posX, @Switch double posY, @Switch double posZ,
		@Switch double all
	) {
		BoundingBox box = getEntity(id).getBoundingBox();

		if (box == null)
			box = targetEntity.createBoundingBox();

		if (all != 0) {
			negX += all; negY += all; negZ += all;
			posX += all; posY += all; posZ += all;
		}

		if (x != 0) {
			negX += x;
			posX += x;
		}

		if (y != 0) {
			negY += y;
			posY += y;
		}

		if (z != 0) {
			negZ += z;
			posZ += z;
		}

		box.expand(negX, negY, negZ, posX, posY, posZ);
		targetEntity.updateBoundingBox();
		service.save(targetEntity);
		send(PREFIX + "Modified bounding box");
	}

	@Path("shift [--id] [--x] [--y] [--z]")
	@Description("Shift an entity and it's custom bounding box")
	void shift(@Switch String id, @Switch double x, @Switch double y, @Switch double z) {
		getEntity(id);

		final Entity entity = targetEntity.getLoadedEntity();
		entity.teleport(entity.getLocation().add(x, y, z));

		targetEntity.modifyBoundingBox(box -> box.shift(x, y, z));

		service.save(targetEntity);
		send(PREFIX + "Shifted entity & bounding box");
	}

	@Path("draw [--id] [--stop]")
	@Description("Draw an entity's custom bounding box with particles")
	void draw(@Switch String id, @Switch boolean stop) {
		getEntity(id);

		if (!targetEntity.hasCustomBoundingBox())
			error("That entity doesn't have a custom bounding box");

		if (stop) {
			if (!targetEntity.isDrawing())
				error("No particle task for that entity running");

			targetEntity.stopDrawing();
			send(PREFIX + "Particle task cancelled");
			return;
		}

		targetEntity.draw();
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

	@Getter
	@Setter
	public static class CustomBoundingBoxEntityInteractEvent extends PlayerEvent implements Cancellable {
		private static final HandlerList handlers = new HandlerList();
		private final CustomBoundingBoxEntity entity;
		private final EquipmentSlot hand;
		private final PlayerEvent originalEvent;
		private boolean cancelled;

		public CustomBoundingBoxEntityInteractEvent(@NotNull Player who, CustomBoundingBoxEntity entity, PlayerEvent originalEvent) {
			super(who);
			this.entity = entity;
			this.originalEvent = originalEvent;

			if (originalEvent instanceof PlayerInteractEvent interactEvent)
				this.hand = interactEvent.getHand();
			else if (originalEvent instanceof PlayerInteractEntityEvent interactEntityEvent)
				this.hand = interactEntityEvent.getHand();
			else
				this.hand = null;
		}

		public static HandlerList getHandlerList() {
			return handlers;
		}

		@Override
		public HandlerList getHandlers() {
			return handlers;
		}
	}

	static {
		for (World world : Bukkit.getWorlds())
			for (ArmorStand armorStand : world.getEntitiesByClass(ArmorStand.class))
				onLoad(armorStand.getUniqueId());
	}

	@EventHandler
	public void onEntityAddToWorld(EntityAddToWorldEvent event) {
		if (event.getEntity() instanceof ArmorStand armorStand)
			onLoad(armorStand.getUniqueId());
	}

	private static void onLoad(UUID uuid) {
		final CustomBoundingBoxEntity entity = service.get(uuid);
		if (!entity.hasCustomBoundingBox())
			return;

		entity.updateBoundingBox();
	}

	@TabCompleterFor(CustomBoundingBoxEntity.class)
	List<String> tabCompleteCustomBoundingBoxEntity(String filter) {
		return service.getAll().stream()
			.map(CustomBoundingBoxEntity::getId)
			.filter(id -> id.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

	@ConverterFor(CustomBoundingBoxEntity.class)
	CustomBoundingBoxEntity convertToCustomBoundingBoxEntity(String value) {
		return service.getById(value);
	}

}
