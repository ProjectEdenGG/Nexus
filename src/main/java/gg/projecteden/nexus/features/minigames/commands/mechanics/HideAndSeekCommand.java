package gg.projecteden.nexus.features.minigames.commands.mechanics;

import gg.projecteden.nexus.features.minigames.mechanics.HideAndSeek;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

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
