package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.features.minigames.utils.MinigameNight.NextMGN;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class MGNCommand extends CustomCommand {

	public MGNCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		NextMGN mgn = isPlayer() ? new NextMGN(player()) : new NextMGN();

		line();
		if (mgn.isNow())
			send("&3Minigame night is happening right now! Join with &e/gl");
		else
			send("&3The next &eMinigame Night &3will be hosted on &e" + mgn.getDateFormatted() + "&3 at &e"
				+ mgn.getTimeFormatted() + "&3. That is in &e" + mgn.getUntil());
	}

}
