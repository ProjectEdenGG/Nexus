package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.ItemBuilder;

import static me.pugabyte.bncore.utils.ItemBuilder.removeLoreLine;
import static me.pugabyte.bncore.utils.ItemBuilder.setLoreLine;

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
