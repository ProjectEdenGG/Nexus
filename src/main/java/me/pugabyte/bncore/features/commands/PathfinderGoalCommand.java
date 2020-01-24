package me.pugabyte.bncore.features.commands;

import com.github.ysl3000.bukkit.pathfinding.PathfinderGoalAPI;
import com.github.ysl3000.bukkit.pathfinding.entity.Insentient;
import com.github.ysl3000.bukkit.pathfinding.goals.PathfinderGoalMoveToLocation;
import com.github.ysl3000.bukkit.pathfinding.pathfinding.PathfinderManager;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.entity.Zombie;

@Permission("permission")
public class PathfinderGoalCommand extends CustomCommand {

	public PathfinderGoalCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		PathfinderManager pathApi = PathfinderGoalAPI.INSTANCE.getAPI();
		if (pathApi == null)
			error("PathfinderAPI is null?");

		Zombie zombie = player().getWorld().spawn(player().getLocation(), Zombie.class);
		zombie.setSilent(true);
		Insentient insentient = pathApi.getPathfinderGoalEntity(zombie);

		insentient.clearPathfinderGoals();
		PathfinderGoalMoveToLocation goal = new PathfinderGoalMoveToLocation(insentient, player().getLocation().add(20, 0, 13), 1.1, 1);
		insentient.addPathfinderGoal(0, goal);

		Tasks.wait(10 * 20, () -> {
			send("Is done: " + insentient.getNavigation().isDoneNavigating());
			send("Has goal: " + insentient.hasPathfinderGoal(goal));
		});
		Tasks.wait(30 * 20, () -> {
			send("Is done: " + insentient.getNavigation().isDoneNavigating());
			send("Has goal: " + insentient.hasPathfinderGoal(goal));
		});
	}
}
