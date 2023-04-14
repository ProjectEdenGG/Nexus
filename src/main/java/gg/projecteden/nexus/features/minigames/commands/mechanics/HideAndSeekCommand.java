package gg.projecteden.nexus.features.minigames.commands.mechanics;

import gg.projecteden.nexus.features.minigames.mechanics.HideAndSeek;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;

@Permission(Group.MODERATOR)
public class HideAndSeekCommand extends InfectionCommand {

	public HideAndSeekCommand(CommandEvent event) {
		super(event);
	}

	@Path("kit")
	@Description("Receive the Hide and Seek kit")
	void kit() {
		giveItem(HideAndSeek.RADAR);
		giveItem(HideAndSeek.SELECTOR_ITEM);
		giveItem(HideAndSeek.STUN_GRENADE);
		send(PREFIX + "Giving Hide and Seek kit");
	}

}
