package me.pugabyte.nexus.features.menus.itemeditor;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.seniorstaff")
public class ItemEditorCommand extends CustomCommand {

	public ItemEditorCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void itemEditor() {
		ItemEditorMenu.openItemEditor(player(), ItemEditMenu.MAIN);
	}

}