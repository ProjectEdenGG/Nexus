package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

@Permission(Group.STAFF)
public class WeeeCommand extends CustomCommand {

	public WeeeCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void weee(@Arg("self") Player player) {
		List<Vector> vectors = Arrays.asList(new Vector(3, 0, 1), new Vector(-3, 0, 1), new Vector(1, 0, 3), new Vector(1, 0, -3), new Vector(0, 2.5, 0));

		Tasks.Countdown.builder()
				.duration(TickTime.SECOND.x(10))
				.doZero(true)
				.onTick(i -> {
					if (i % 5 == 0)
						player.setVelocity(RandomUtils.randomElement(vectors));
				})
				.start();
	}


}
