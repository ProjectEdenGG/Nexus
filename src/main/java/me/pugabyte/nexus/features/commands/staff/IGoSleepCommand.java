package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;

@Permission("group.staff")
public class IGoSleepCommand extends CustomCommand {

	public IGoSleepCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[hours]")
	void run(@Arg("4") int hours) {
		send("Kicking you in " + hours + " hours");
		Tasks.wait(Time.HOUR.x(hours), () -> player().kickPlayer("Goodnight"));
	}

}
