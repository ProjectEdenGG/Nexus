package me.pugabyte.nexus.features.menus.itemeditor;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class ItemEditorCommand extends CustomCommand {

	public ItemEditorCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Permission("item.editor")
	void itemEditor() {
		ItemEditorMenu.openItemEditor(player(), ItemEditMenu.MAIN);
	}

}