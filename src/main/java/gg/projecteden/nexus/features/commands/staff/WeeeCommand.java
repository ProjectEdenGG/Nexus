package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

@Permission(Group.STAFF)
public class WeeeCommand extends CustomCommand {

	public WeeeCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Force a player to fly around in random directions")
	void run(@Optional("self") Nerd nerd) {
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
