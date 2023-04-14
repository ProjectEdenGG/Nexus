package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.framework.commandsv2.annotations.command.Redirects.Redirect;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;

@HideFromWiki // TODO feature/locks
@Permission(Group.SENIOR_STAFF)
@Redirect(from = "/lwc admin purge", to = "/lwcpurge")
public class LWCPurgeCommand extends CustomCommand {

	public LWCPurgeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Purge LWC protections in a radius")
	void run(@Optional("20") int radius) {
		if (radius > 100) {
			send(PREFIX + "&cMax radius is 100, limiting");
			radius = 100;
		}

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
	}

}
