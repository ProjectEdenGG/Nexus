package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Cooldown;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.World;
import org.bukkit.entity.Bat;

import java.util.ArrayList;
import java.util.List;

@Permission(Group.SENIOR_STAFF)
@Cooldown(value = TickTime.MINUTE, x = 5)
public class WhoDaresSummonMeCommand extends CustomCommand {

	public WhoDaresSummonMeCommand(CommandEvent event) {
		super(event);
	}

	@Override
	@NoLiterals
	@Description("Start a spooky unvanish animation")
	public void help() {
		runCommand("vanish off");
		World world = world();
		world.strikeLightning(location().clone().add(0, 5, 0));
		world.strikeLightningEffect(location().clone().add(5, 0, 0));
		world.strikeLightningEffect(location().clone().add(0, 0, 5));
		world.strikeLightningEffect(location().clone().subtract(5, 0, 0));
		world.strikeLightningEffect(location().clone().subtract(0, 0, 5));
		List<Bat> bats = new ArrayList<>();
		for (int i = 0; i < 10; i++)
			bats.add(world.spawn(location(), Bat.class));
		Tasks.wait(TickTime.SECOND.x(5), () -> bats.forEach(Bat::remove));
	}

}
