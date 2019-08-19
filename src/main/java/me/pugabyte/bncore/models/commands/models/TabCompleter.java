package me.pugabyte.bncore.models.commands.models;

import me.pugabyte.bncore.models.commands.models.annotations.TabCompleterFor;
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
