package gg.projecteden.nexus.features.menus.itemeditor;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission(Group.SENIOR_STAFF)
public class ItemEditorCommand extends CustomCommand {

	public ItemEditorCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void itemEditor() {
		ItemEditorMenu.openItemEditor(player(), ItemEditMenu.MAIN);
	}

}
