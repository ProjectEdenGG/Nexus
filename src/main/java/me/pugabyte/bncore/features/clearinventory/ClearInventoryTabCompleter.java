package me.pugabyte.bncore.features.clearinventory;

import me.pugabyte.bncore.BNCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class ClearInventoryTabCompleter implements TabCompleter {

	public ClearInventoryTabCompleter() {
		BNCore.registerTabCompleter("clearinventory", this);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.singletonList("undo");
	}

}
