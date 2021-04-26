package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

@Permission("group.staff")
public class WeeeCommand extends CustomCommand {

	public WeeeCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void weee(@Arg("self") Player player) {
		List<Vector> vectors = Arrays.asList(new Vector(3, 0, 1), new Vector(-3, 0, 1), new Vector(1, 0, 3), new Vector(1, 0, -3), new Vector(0, 2.5, 0));

		Tasks.Countdown.builder()
				.duration(Time.SECOND.x(10))
				.doZero(true)
				.onTick(i -> {
					if (i % 5 == 0)
						player.setVelocity(RandomUtils.randomElement(vectors));
				})
				.start();
	}


}
