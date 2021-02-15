package me.pugabyte.nexus.features.commands.staff.admin;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.preconfigured.MustBeIngameException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
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

	@Path("<entityType...> [amount]")
	void spawnEntity(@Arg(type = EntityType.class, tabCompleter = LivingEntity.class) List<EntityType> entityTypes, @Arg(min = 1) int amount) {
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
