package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Permission("group.staff")
public class LoreCommand extends CustomCommand {

	public LoreCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		send("&4Correct Usage:");
		send("&c/lore set <line> <text>");
		send("&c/lore add <text>");
		send("&c/lore remove <line>");
	}

	@Path("set <line> <text>")
	void setLore(int line, String text) {
		ItemStack tool = getTool(player());
		ItemMeta meta = tool.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null)
			lore = new ArrayList<>();
		while (lore.size() < line)
			lore.add("");

		lore.set(line - 1, text);
		meta.setLore(lore);
		tool.setItemMeta(meta);
		player().updateInventory();
	}

	@Path("add <text>")
	void addLore(String text) {
		ItemStack tool = getTool(player());
		ItemMeta meta = tool.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null)
			lore = new ArrayList<>();
		lore.add(text);
		meta.setLore(lore);
		tool.setItemMeta(meta);
		player().updateInventory();
	}

	@Path("remove <line>")
	void removeLore(int line) {
		ItemStack tool = getTool(player());
		ItemMeta meta = tool.getItemMeta();
		List<String> lore = meta.getLore();

		if (lore == null) error("Item does not have lore");
		if (line - 1 > lore.size()) error("Line " + line + " does not exist");

		lore.remove(line - 1);
		meta.setLore(lore);
		tool.setItemMeta(meta);
		player().updateInventory();
	}

	ItemStack getTool(Player player) {
		return player.getInventory().getItemInMainHand();
	}
}
