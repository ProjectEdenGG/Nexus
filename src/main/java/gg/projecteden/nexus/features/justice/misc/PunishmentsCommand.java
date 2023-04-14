package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import lombok.NonNull;

@Permission(Group.MODERATOR)
public class PunishmentsCommand extends _JusticeCommand {

	public PunishmentsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("General guideline for punishments")
	void run() {
		send("&eGriefing");
		send("    &c/calcban <# of past griefing bans> <# of blocks griefed>");
		line();
		send("&eChat");
		send("    &3Try to keep it to &cmutes &3for established members of the community. Give them time to cool down. Otherwise short bans");
		line();
		send("&eHacks / death traps / obscene structures/skins");
		send("    &cMax of 3 days&3, then permanent");
		line();
		send("&eBan evasions");
		send("    &cPermanently ban alt&3, add time to main account if it was malicious");
		line();
		send("&eOther assholery");
		send("    &3Generally &c1 day&3, max of 3 days for first ban");
		line();
		send("&3If they are &enot active &3when you find reason to ban, make sure to send a &c/warning &3too/instead so they will receive the message instead of not knowing they were banned at all.");
	}

}
