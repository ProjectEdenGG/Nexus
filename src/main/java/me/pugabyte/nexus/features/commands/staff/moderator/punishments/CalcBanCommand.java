package me.pugabyte.nexus.features.commands.staff.moderator.punishments;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

import java.text.DecimalFormat;

@Permission("group.moderator")
public class CalcBanCommand extends CustomCommand {

	public CalcBanCommand(CommandEvent event) {
		super(event);
	}

	@Path("<pastbans> <blocksgriefed>")
	void run(int bans, int blocksGriefed) {
		double hours = 0;
		if (++bans < 2)
			hours = bans * Math.sqrt(blocksGriefed) + (bans * 10) - 3;
		else if (bans < 3)
			hours = bans * Math.sqrt(blocksGriefed * 15) + (bans * 10);

		send();
		if (hours == 0)
			send("&eRecommended ban time: &cPermanent. Talk to an Operator.");
		else {
			if (hours > 24)
				send("&eRecommended ban time: &c" + new DecimalFormat("#.00").format(hours / 24) + " days");
			else
				send("&eRecommended ban time: &c" + Math.round(hours) + " hours");
			send("&3Please adjust for circumstances like the purpose of grief (gathering vs malicious), the player's overall attitude in the community, etc.");
		}

	}

}
