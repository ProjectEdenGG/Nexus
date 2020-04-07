package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;

@Fallback("lwc")
public class LWCCommand extends CustomCommand {

	public LWCCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Permission("group.seniorstaff")
	@Path("admin (purge|update|report|convert|clear) [radius]")
	void admin(@Arg("20") int radius) {
		if (arg(2).equalsIgnoreCase("purge")) {
			if (radius > 100)
				error("Max radius is 100");

			runCommand("rg remove lwcpurge");
			runCommand("/here " + radius);
			Tasks.wait(10, () -> {
				runCommand("rg define lwcpurge");
				Tasks.wait(10, () -> {
					runCommand("cadmin purgeregion lwcpurge " + player().getWorld().getName());
					runCommand("rg remove lwcpurge");
					runCommand("/desel");
				});
			});
		} else
			fallback();
	}

}
