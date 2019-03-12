package me.pugabyte.bncore.features.tab;

import com.keenant.tabbed.Tabbed;
import me.pugabyte.bncore.BNCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Tab implements Listener {

	Tabbed tabbed;

	public Tab() {
		tabbed = new Tabbed(BNCore.getInstance());
		new TabCommand();
	}

	public Tabbed getTabbed() {
		return tabbed;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (tabbed == null) {
			tabbed = BNCore.tab.getTabbed();
		}

		Player player = event.getPlayer();

//		SimpleTabList tab = tabbed.(player);


	}
}
