package gg.projecteden.nexus.features.commands.staff.moderator;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission(Group.MODERATOR)
public class EnzlesCommand extends CustomCommand {

	public static boolean displayMOTD = false;
	public static String MOTD = "&dHappy birthday &bEnzles16&d!";

	public EnzlesCommand(CommandEvent event) {
		super(event);
	}

	@Path("motd [enable]")
	@Description("Toggle the MOTD for Enzles Birthday")
	void motd(Boolean enable) {
		if (enable == null)
			enable = !displayMOTD;

		displayMOTD = enable;

		send(PREFIX + "Custom MOTD " + (enable ? "&aEnabled" : "&cDisabled"));
	}


}
