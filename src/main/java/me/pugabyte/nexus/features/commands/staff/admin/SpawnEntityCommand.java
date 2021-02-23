package me.pugabyte.nexus.features.commands.staff.admin;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.preconfigured.MustBeIngameException;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

@Permission("group.admin")
@Aliases({"mob", "spawnmob"})
public class SpawnEntityCommand extends CustomCommand {

	public SpawnEntityCommand(CommandEvent event) {
		super(event);
	}

	@Path("<entityType> [amount]")
	void spawnEntity(@Arg(type = EntityData.class, tabCompleter = LivingEntity.class) List<EntityData> entities, @Arg(value = "1", min = 1) int amount) {
		for (int i = 0; i < amount; i++) {
			List<EntityData> copy = new ArrayList<>(entities);
			Entity entity = spawn(copy.remove(0));

			for (EntityData data : copy) {
				Entity passenger = spawn(data);

				entity.addPassenger(passenger);
				entity = passenger;
			}
		}
	}

	private Entity spawn(EntityData entityData) {
		Entity entity = entityData.getLocation().getWorld().spawnEntity(entityData.getLocation(), entityData.getType());

		if (!isNullOrEmpty(entityData.getData())) {
			Nexus.log("Data: " + entityData.getData());
			if ("baby".equalsIgnoreCase(entityData.getData()) && entity instanceof Ageable)
				((Ageable) entity).setBaby();


			else if (StringUtils.isValidJson(entityData.getData())) {
				Nexus.log("Adding json");
				NBTEntity nbtEntity = new NBTEntity(entity);
				nbtEntity.mergeCompound(new NBTContainer(entityData.getData()));
			}
		}

		return entity;
	}

	@Data
	@AllArgsConstructor
	@RequiredArgsConstructor
	private class EntityData {
		@NonNull
		private final EntityType type;
		@NonNull
		private final Location location;
		private String data;
	}

	@ConverterFor(EntityData.class)
	EntityData convertToEntityData(String value) {
		Location location;
		if (isPlayer())
			location = getTargetBlockRequired().getRelative(BlockFace.UP).getLocation();
		else if (isCommandBlock())
			location = commandBlock().getBlock().getRelative(BlockFace.UP).getLocation();
		else
			throw new MustBeIngameException();

		EntityData entityData;
		if (value.contains(":")) {
			String[] split = value.split(":", 2);
			entityData = new EntityData((EntityType) convertToEnum(split[0], EntityType.class), location, split[1]);
		} else
			entityData = new EntityData((EntityType) convertToEnum(value, EntityType.class), location);

		return entityData;
	}

}
