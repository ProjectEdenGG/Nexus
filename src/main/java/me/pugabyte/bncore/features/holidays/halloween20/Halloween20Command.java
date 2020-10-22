package me.pugabyte.bncore.features.holidays.halloween20;

import me.pugabyte.bncore.features.holidays.halloween20.quest.menus.Halloween20Menus;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Permission("group.staff")
public class Halloween20Command extends CustomCommand {

	public Halloween20Command(CommandEvent event) {
		super(event);
	}

	@Path("picture")
	void picture() {
		Halloween20Menus.openPicturePuzzle(player());
	}

	@Path("flashCard")
	void flash() {
		Halloween20Menus.openFlashCardPuzzle(player());
	}

}
