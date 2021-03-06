package me.pugabyte.nexus.features.menus.mutemenu;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.menus.mutemenu.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class MuteMenuCommand extends CustomCommand {

	public MuteMenuCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void muteMenu() {
		send(PREFIX + "&3The mute menu is down for &oMaintenance. &cSorry for the inconvenience.");
		if (isStaff()) {
			SmartInventory.builder()
					.title("&3Mute Menu")
					.size(MenuUtils.getRows(MuteMenuItem.values().length, 2, 7), 9)
					.provider(new MuteMenuProvider())
					.build().open(player());
		}
	}
}
