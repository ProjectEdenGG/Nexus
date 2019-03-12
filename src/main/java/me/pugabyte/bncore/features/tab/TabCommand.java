package me.pugabyte.bncore.features.tab;

import com.keenant.tabbed.Tabbed;
import com.keenant.tabbed.item.PlayerTabItem;
import com.keenant.tabbed.item.TabItem;
import com.keenant.tabbed.tablist.SimpleTabList;
import me.pugabyte.bncore.BNCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TabCommand implements CommandExecutor {

	public TabCommand() {
		BNCore.registerCommand("tab", this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;

		Tabbed tabbed = new Tabbed(BNCore.getInstance());
		tabbed.destroyTabList(player);
		SimpleTabList tab = tabbed.newSimpleTabList(player);

		for (Player _player : Bukkit.getOnlinePlayers()) {
			tab.add(new PlayerTabItem(_player, new PlayerTabItem.PlayerProvider<String>() {
				@Override
				public String get(Player player) {
					return BNCore.colorize("&8[&b&oMod&8] &b&o" + _player.getName());
				}
			}));
		}

		return true;
	}
}
