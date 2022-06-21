package gg.projecteden.nexus.features.commands.staff.admin;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTEntity;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.preconfigured.MustBeIngameException;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.ArrayList;
import java.util.List;

@Permission(Group.ADMIN)
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

			if (!Nullables.isNullOrEmpty(data)) {
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
			location = getTargetBlockRequired().getRelative(BlockFace.UP).getLocation().toCenterLocation();
		else if (isCommandBlock())
			location = commandBlock().getBlock().getRelative(BlockFace.UP).getLocation().toCenterLocation();
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
