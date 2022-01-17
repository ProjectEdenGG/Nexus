package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NonNull;

@Permission(Group.STAFF)
public class IGoSleepCommand extends CustomCommand {

	public IGoSleepCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[hours]")
	void run(@Arg("4") int hours) {
		send("Kicking you in " + hours + " hours");
		Tasks.wait(TickTime.HOUR.x(hours), () -> player().kickPlayer("Goodnight"));
	}

}
