package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@Permission(Group.MODERATOR)
public class PunishmentsCommand extends _JusticeCommand {

	public PunishmentsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("General guideline for punishments")
	void run() {
		send(PREFIX);
		line();
		send("&eGriefing");
		send("    &cFirst offense: &3Recommend &c/warn&3, max of &c1 day");
		send("    &cSecond offense: &31 day if first was warning, max of 3 days");
		send("    &cThird offense: &3Permanent");
		line();
		send("&eChat");
		send("    &3Mute initially, short ban if they continue");
		line();
		send("&eHacks / death traps / obscene structures/skins");
		send("    &3Max of 3 days, then permanent");
		line();
		send("&eSlurs / hate speech / racism / etc of any kind");
		send("    &3Permanent");
		line();
		send("&eBan evasions");
		send("    &3Permanently ban alt, add time to main account if it was malicious");
		line();
		send("&eOther assholery");
		send("    &3Generally 1 day ban, max of 3 days for first ban");
		line();
		send("Only Operators+ can authorize punishments that go beyond the guidelines");
		line();
	}

}
