package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.ItemBuilder;

import static me.pugabyte.nexus.utils.ItemBuilder.removeLoreLine;
import static me.pugabyte.nexus.utils.ItemBuilder.setLoreLine;

@Permission("group.staff")
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
