package gg.projecteden.nexus.features.menus.itemeditor;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

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