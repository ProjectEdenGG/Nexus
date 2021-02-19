package me.pugabyte.nexus.features.commands.staff.admin;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
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

	@Path("<entityType> [amount]")
	void spawnEntity(@Arg(type = EntityType.class, tabCompleter = LivingEntity.class) List<EntityType> entityTypes, @Arg(value = "1", min = 1) int amount) {
		Location location;
		if (isPlayer())
			location = getTargetBlockRequired().getRelative(BlockFace.UP).getLocation();
		else if (isCommandBlock())
			location = commandBlock().getBlock().getRelative(BlockFace.UP).getLocation();
		else
			throw new MustBeIngameException();

		World world = location.getWorld();

		for (int i = 0; i < amount; i++) {
			List<EntityType> copy = new ArrayList<>(entityTypes);
			Entity entity = world.spawnEntity(location, copy.remove(0));
			for (EntityType entityType : copy) {
				Entity passenger = world.spawnEntity(location, entityType);
				entity.addPassenger(passenger);
				entity = passenger;
			}
		}
	}
}
