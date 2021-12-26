package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;

import static gg.projecteden.nexus.utils.ItemBuilder.removeLoreLine;
import static gg.projecteden.nexus.utils.ItemBuilder.setLoreLine;

@Permission(Group.STAFF)
public class LoreCommand extends CustomCommand {

	public LoreCommand(CommandEvent event) {
		super(event);
	}

	@Path("set <line> <text...>")
	void setLore(int line, String text) {
		setLoreLine(getToolRequired(), line, text);
		player().updateInventory();
	}

	@Path("add <text...>")
	void addLore(String text) {
		ItemBuilder.addLore(getToolRequired(), text);
		player().updateInventory();
	}

	@Path("remove <line>")
	void removeLore(int line) {
		removeLoreLine(getToolRequired(), line);
		player().updateInventory();
	}
}
