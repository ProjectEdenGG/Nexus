package gg.projecteden.nexus.features.commands.staff.admin;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.customhitbox.CustomBoundingBoxEntity;
import gg.projecteden.nexus.models.customhitbox.CustomBoundingBoxEntityService;
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
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@NoArgsConstructor
public class CustomBoundingBoxCommand extends CustomCommand implements Listener {
	private static final CustomBoundingBoxEntityService service = new CustomBoundingBoxEntityService();
	private CustomBoundingBoxEntity targetEntity;

	public CustomBoundingBoxCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			targetEntity = getTarget();
	}

	private CustomBoundingBoxEntity getTarget() {
		return service.get(getTargetEntityRequired());
	}

	@Path("init")
	void init() {
		if (targetEntity.hasCustomHitbox())
			error("That " + camelCase(targetEntity.getEntityType()) + " already has a custom bounding box");

		targetEntity.createBoundingBox();
		service.save(targetEntity);
		send(PREFIX + "Created bounding box");
	}

	@Path("update")
	void update() {
		if (!targetEntity.hasCustomHitbox())
			error("That " + camelCase(targetEntity.getEntityType()) + " does not have a custom bounding box");

		targetEntity.updateBoundingBox();
		send(PREFIX + "Updated bounding box");
	}

	@Path("modify [--x] [--y] [--z] [--posX] [--posY] [--posZ] [--negX] [--negY] [--negZ] [--all]")
	void boundingBox_modify(
		@Switch double x, @Switch double y, @Switch double z,
		@Switch double negX, @Switch double negY, @Switch double negZ,
		@Switch double posX, @Switch double posY, @Switch double posZ,
		@Switch double all
	) {
		BoundingBox box = targetEntity.getBoundingBox();
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

	@Path("shift [--x] [--y] [--z]")
	void shift(@Switch double x, @Switch double y, @Switch double z) {
		final Entity entity = targetEntity.getLoadedEntity();
		entity.teleport(entity.getLocation().add(x, y, z));

		targetEntity.modifyBoundingBox(box -> box.shift(x, y, z));

		service.save(targetEntity);
		send(PREFIX + "Shifted entity & bounding box");
	}

	@Path("draw [--stop]")
	void draw(@Switch boolean stop) {
		if (stop) {
			if (!targetEntity.isDrawing())
				error("No particle task for that entity running");

			targetEntity.stopDrawing();
			send(PREFIX + "Particle task cancelled");
			return;
		}

		targetEntity.draw();
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final CustomBoundingBoxEntity entity = service.getTargetEntity(player);

		if (entity == null)
			return;

		if (!new CustomBoundingBoxEntityInteractEvent(player, entity, event).callEvent())
			event.setCancelled(true);
	}

	@EventHandler
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
		if (!entity.hasCustomHitbox())
			return;

		entity.updateBoundingBox();
	}

}
