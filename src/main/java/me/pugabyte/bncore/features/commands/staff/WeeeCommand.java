package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Permission("group.staff")
public class WeeeCommand extends CustomCommand {

	public WeeeCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void weee(Player player) {
		AtomicInteger i = new AtomicInteger(1);
		AtomicInteger id = new AtomicInteger(0);
		id.set(Tasks.repeat(0, 10, () -> {
			if (i.getAndIncrement() > 20) Tasks.cancel(id.get());
			List<Vector> vectors = Arrays.asList(new Vector(1, 1, 0), new Vector(-1, 1, 0), new Vector(0, 1, 1), new Vector(0, 1, -1));
			Vector vector = Utils.getRandomElement(vectors);
			player.setVelocity(vector);
		}));
	}


}
