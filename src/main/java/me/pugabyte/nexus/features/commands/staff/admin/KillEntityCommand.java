package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.nexus.framework.annotations.Disabled;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.preconfigured.MustBeIngameException;
import me.pugabyte.nexus.utils.EntityUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;

import java.util.ArrayList;
import java.util.List;

@Permission("group.admin")
@Aliases("killall")
@Disabled
public class KillEntityCommand extends CustomCommand {

	public KillEntityCommand(CommandEvent event) {
		super(event);
	}

	@Getter
	public enum KillableEntityType {
		LIVING(LivingEntity.class),
		HOSTILE(Monster.class, EntityUtils.getExtraHostileMobs());

		private final Class<? extends Entity> entityClass;
		private final List<EntityType> included;

		KillableEntityType(Class<? extends Entity> entityClass) {
			this(entityClass, null);
		}

		KillableEntityType(@NonNull Class<? extends Entity> entityClass, @NonNull List<EntityType> included) {
			this.entityClass = entityClass;
			this.included = included;
		}
	}

	@Path("<entityType...>")
	void spawnEntity(@Arg(type = EntityType.class, tabCompleter = LivingEntity.class) List<EntityType> entityTypes) {
		Location location;
		if (isPlayer())
			location = getTargetBlockRequired().getRelative(BlockFace.UP).getLocation();
		else if (isCommandBlock())
			location = commandBlock().getBlock().getRelative(BlockFace.UP).getLocation();
		else
			throw new MustBeIngameException();

		World world = location.getWorld();
		Entity entity = world.spawnEntity(location, entityTypes.remove(0));
		for (EntityType entityType : entityTypes) {
			Entity passenger = world.spawnEntity(location, entityType);
			entity.addPassenger(passenger);
			entity = passenger;
		}
	}

	@TabCompleterFor(LivingEntity.class)
	List<String> tabCompleteLivingEntity(String value) {
		List<String> completions = new ArrayList<>();
		for (EntityType entityType : EntityType.values()) {
			Class<? extends Entity> entityClass = entityType.getEntityClass();
			if (entityClass != null && entityClass.isAssignableFrom(LivingEntity.class))
				completions.add(entityType.name().toLowerCase());
		}

		return completions;
	}
}
