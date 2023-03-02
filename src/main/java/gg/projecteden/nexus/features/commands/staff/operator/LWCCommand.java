package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Fallback;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;

@Fallback("lwc")
public class LWCCommand extends CustomCommand {

	public LWCCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Permission(Group.SENIOR_STAFF)
	@Path("admin (purge|update|report|convert|clear) [radius]")
	@Description("Learn about the administrative LWC commands")
	void admin(@Arg("20") int radius) {
		if (arg(2).equalsIgnoreCase("purge")) {
			if (radius > 100)
				error("Max radius is 100");

			runCommand("rg remove lwcpurge");
			runCommand("/here " + radius);
			Tasks.wait(10, () -> {
				runCommand("rg define lwcpurge");
				Tasks.wait(10, () -> {
					runCommand("cadmin purgeregion lwcpurge " + world().getName());
					runCommand("rg remove lwcpurge");
					runCommand("/desel");
				});
			});
		} else
			fallback();
	}

}
