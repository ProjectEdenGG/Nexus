package gg.projecteden.nexus.features.customboundingboxes;

import de.tr7zw.nbtapi.NBTEntity;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.features.resourcepack.commands.CustomModelConverterCommand;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntity;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntityService;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.Listener;
import org.bukkit.util.BoundingBox;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@Permission(Group.ADMIN)
public class CustomBoundingBoxCommand extends CustomCommand implements Listener {
	private static final CustomBoundingBoxEntityService service = new CustomBoundingBoxEntityService();
	private CustomBoundingBoxEntity targetEntity;

	public CustomBoundingBoxCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private CustomBoundingBoxEntity getEntity(String id) {
		return getEntity(id, false);
	}

	private CustomBoundingBoxEntity getEntity(String id, boolean nearest) {
		if (nearest)
			targetEntity = service.get(getNearestEntityRequired());
		else if (!Nullables.isNullOrEmpty(id))
			targetEntity = service.getById(id);
		else
			targetEntity = service.get(getTargetEntityRequired());
		return targetEntity;
	}

	@Path("create [id] [--nearest]")
	@Description("Create a custom bounding box entity")
	void create(String id, @Switch boolean nearest) {
		getEntity(null, nearest);

		if (targetEntity.hasCustomBoundingBox())
			error("That " + camelCase(targetEntity.getEntityType()) + " already has a custom bounding box");

		try {
			service.getById(id);
			error("A custom bounding box with id &e" + id + " &calready exists");
		} catch (Exception ignore) {}

		targetEntity.setId(id);
		targetEntity.createBoundingBox();
		service.save(targetEntity);
		service.cache(targetEntity);
		send(PREFIX + "Created bounding box");

		if (targetEntity.getLoadedEntity() instanceof Display)
			modify(targetEntity.getId(), 0, 0, 0, 0, 0, 0, 0, 0, 0, .5);

		targetEntity.draw();
	}

	@Path("list [--radius]")
	void list(
		@Switch Integer radius
	) {
		send(PREFIX + "Custom bounding boxes:");
		for (CustomBoundingBoxEntity entity : service.getAll()) {
			if (!entity.isLoaded())
				continue;

			if (radius != null)
				if (distance(entity.getLocation()).gt(radius))
					continue;

			send("&e" + entity.getId());
		}
	}

	@Path("convert armorStand to itemDisplay [--id]")
	@Description("Automatically convert an unmodified large armor stand with an item on its head to an item display entity")
	void convert_armorStand_to_itemDisplay(@Switch String id) {
		getEntity(id);

		if (!(targetEntity.getEntity() instanceof ArmorStand armorStand))
			throw new InvalidInputException("Custom bounding box entity &e" + id + " &cis not an Armor Stand but is "
				+ StringUtils.an(camelCase(targetEntity.getLoadedEntity().getType()), "&e"));

		updateUuid(CustomModelConverterCommand.armorStandToItemDisplay(armorStand));
	}

	@Path("convert itemDisplay to armorStand [--id]")
	@Description("Automatically convert a converted item display back to an armor stand")
	void convert_itemDisplay_to_armorStand(@Switch String id) {
		getEntity(id);

		if (!(targetEntity.getEntity() instanceof ItemDisplay itemDisplay))
			throw new InvalidInputException("Custom bounding box entity &e" + id + " &cis not an Item Display but is "
				+ StringUtils.an(camelCase(targetEntity.getLoadedEntity().getType()), "&e"));

		updateUuid(CustomModelConverterCommand.itemDisplayToArmorStand(itemDisplay));
	}

	private void updateUuid(Entity entity) {
		Tasks.async(() -> {
			service.deleteSync(targetEntity);
			targetEntity.setUuid(entity.getUniqueId());
			service.saveSync(targetEntity);
			service.cache(targetEntity);
			send(PREFIX + "Entity successfully converted to " + StringUtils.an(camelCase(entity.getType()), "&e"));
		});
	}

	@Path("associated create clone [associationId] [--id]")
	@Description("Create a clone of the entity and tag it with an association id")
	void associated_create_clone(String associationId, @Switch String id) {
		getEntity(id);

		Entity associated = null;
		try {
			associated = EntityUtils.cloneEntity(targetEntity.getLoadedEntity());

			final String tag = targetEntity.getId() + "-" + associationId;
			new NBTEntity(associated).getStringList("Tags").add(tag);

			targetEntity.getAssociated().put(associationId, associated.getUniqueId());
			service.save(targetEntity);
			send(PREFIX + "Created clone entity and tagged it with &e" + tag);
		} catch (Exception ex) {
			if (associated != null)
				associated.remove();
			throw ex;
		}
	}

	@Path("delete [--id] [--nearest]")
	@Description("Delete a custom bounding box")
	void delete(@Switch String id, @Switch boolean nearest) {
		getEntity(id, nearest);

		targetEntity.stopDrawing();
		service.delete(targetEntity);
		send(PREFIX + "Deleted custom bounding box");
	}

	@Path("set id <id> [--id]")
	@Description("Update a custom bounding box entity's ID")
	void set_id(String newId, @Switch String id) {
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
	void modify(
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
		final Location to = entity.getLocation().add(x, y, z);
		entity.teleport(to);

		for (UUID associated : targetEntity.getAssociated().values()) {
			final Entity associatedEntity = targetEntity.getWorld().getEntity(associated);
			if (associatedEntity != null)
				associatedEntity.teleport(to);
		}

		targetEntity.modifyBoundingBox(box -> box.shift(x, y, z));

		service.save(targetEntity);
		send(PREFIX + "Shifted entities & bounding box");
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
