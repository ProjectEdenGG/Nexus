package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

@Permission(Group.SENIOR_STAFF)
public class WeeeCommand extends CustomCommand {

	public WeeeCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	@Description("Force a player to fly around in random directions")
	void weee(@Arg("self") Nerd nerd) {
		if (Minigamer.of(nerd).isPlaying())
			error("Cannot weee " + nerd.getNickname() + ", they are in minigames");

		List<Vector> vectors = Arrays.asList(new Vector(3, 0, 1), new Vector(-3, 0, 1), new Vector(1, 0, 3), new Vector(1, 0, -3), new Vector(0, 2.5, 0));

		Tasks.Countdown.builder()
			.duration(TickTime.SECOND.x(10))
			.doZero(true)
			.onTick(i -> {
				if (i % 5 == 0)
					nerd.getOnlinePlayer().setVelocity(RandomUtils.randomElement(vectors));
			})
				.start();
	}

}
