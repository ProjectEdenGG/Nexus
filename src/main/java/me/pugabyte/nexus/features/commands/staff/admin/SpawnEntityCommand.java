package me.pugabyte.nexus.features.commands.staff.admin;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.List;

@Permission("group.admin")
@Aliases({"mob", "spawnmob"})
public class SpawnEntityCommand extends CustomCommand {

	public SpawnEntityCommand(CommandEvent event) {
		super(event);
	}

	@Path("<entityType...>")
	void spawnEntity(@Arg(type = EntityType.class) List<EntityType> entityTypes) {
		Location location = getTargetBlockRequired().getRelative(BlockFace.UP).getLocation();
		World world = location.getWorld();
		Entity entity = world.spawnEntity(location, entityTypes.remove(0));
		for (EntityType entityType : entityTypes) {
			PlayerUtils.wakka(entityType.name());
			if (entityType == EntityType.UNKNOWN)
				continue;
			Entity passenger = world.spawnEntity(location, entityType);
			entity.addPassenger(passenger);
			entity = passenger;
		}
	}
}
