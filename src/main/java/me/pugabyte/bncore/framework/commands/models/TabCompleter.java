package me.pugabyte.bncore.framework.commands.models;

import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

abstract class TabCompleter {

	@TabCompleterFor("player")
	List<String> playerTabComplete(String filter) {
		List<String> names = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getName().toLowerCase().startsWith(filter.toLowerCase()))
				names.add(player.getName());
		return names;
	}

}
