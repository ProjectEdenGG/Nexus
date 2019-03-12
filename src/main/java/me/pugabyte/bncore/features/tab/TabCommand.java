package me.pugabyte.bncore.features.tab;

import com.keenant.tabbed.Tabbed;
import me.pugabyte.bncore.BNCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class TabCommand implements CommandExecutor, Listener {
	Tabbed tabbed;

	public TabCommand() {
		BNCore.registerCommand("tab", this);
		BNCore.registerListener(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;

		if (args[0].equalsIgnoreCase("destroy")) {
			tabbed.destroyTabList(player);
		} else if (args[0].equalsIgnoreCase("simple")) {

		}
		return true;
	}

}
