package me.pugabyte.nexus.features.commands.staff.admin;

import com.google.common.base.Strings;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.preconfigured.MustBeIngameException;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.ArrayList;
import java.util.List;

@Permission("group.admin")
@Aliases({"mob", "spawnmob"})
public class SpawnEntityCommand extends CustomCommand {

	public SpawnEntityCommand(CommandEvent event) {
		super(event);
	}

	@Path("<entityType> [amount] [reason]")
	void spawnEntity(@Arg(type = EntitySpawnData.class, tabCompleter = LivingEntity.class) List<EntitySpawnData> entities,
					 @Arg(value = "1", min = 1) int amount,
					 @Arg("CUSTOM") SpawnReason reason) {
		for (int i = 0; i < amount; i++) {
			List<EntitySpawnData> copy = new ArrayList<>(entities);
			Entity entity = copy.remove(0).spawn(reason);

			for (EntitySpawnData spawnData : copy) {
				Entity passenger = spawnData.spawn();
				entity.addPassenger(passenger);
				entity = passenger;
			}
		}
	}

	@Data
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class EntitySpawnData {
		@NonNull
		private final EntityType type;
		@NonNull
		private final Location location;
		private String data;

		public Entity spawn() {
			return spawn(SpawnReason.CUSTOM);
		}

		public Entity spawn(SpawnReason spawnReason) {
			Entity entity = location.getWorld().spawnEntity(location, type, spawnReason);

			if (!Strings.isNullOrEmpty(data)) {
				if ("baby".equalsIgnoreCase(data) && entity instanceof Ageable ageable)
					ageable.setBaby();

				else if (StringUtils.isValidJson(data)) {
					NBTEntity nbtEntity = new NBTEntity(entity);
					nbtEntity.mergeCompound(new NBTContainer(data));
				}
			}

			return entity;
		}
	}

	@TabCompleterFor(EntitySpawnData.class)
	List<String> tabCompleteEntitySpawnData(String value) {
		return tabCompleteEnum(value, EntityType.class);
	}

	@ConverterFor(EntitySpawnData.class)
	EntitySpawnData convertToEntitySpawnData(String value) {
		Location location;
		if (isPlayer())
			location = getTargetBlockRequired().getRelative(BlockFace.UP).getLocation();
		else if (isCommandBlock())
			location = commandBlock().getBlock().getRelative(BlockFace.UP).getLocation();
		else
			throw new MustBeIngameException();

		EntitySpawnData spawnData;
		if (value.contains(":")) {
			String[] split = value.split(":", 2);
			spawnData = new EntitySpawnData((EntityType) convertToEnum(split[0], EntityType.class), location, split[1]);
		} else
			spawnData = new EntitySpawnData((EntityType) convertToEnum(value, EntityType.class), location);

		return spawnData;
	}

}
