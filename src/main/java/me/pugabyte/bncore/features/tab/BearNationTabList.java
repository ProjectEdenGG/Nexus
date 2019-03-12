package me.pugabyte.bncore.features.tab;

import com.keenant.tabbed.Tabbed;
import com.keenant.tabbed.item.PlayerTabItem;
import com.keenant.tabbed.item.TabItem;
import com.keenant.tabbed.tablist.SimpleTabList;
import me.pugabyte.bncore.BNCore;
import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

// https://github.com/DungeonRealms/tabbed/blob/master/core/src/main/java/com/keenant/tabbed/tablist/DefaultTabList.java

public class BearNationTabList extends SimpleTabList implements Listener {
	private Map<Player, String> players = new HashMap<>();
	private int taskId;

	public BearNationTabList(Tabbed tabbed, Player player) {
		super(tabbed, player, -1, -1, -1);
	}

	@Override
	public BearNationTabList enable() {
		super.enable();
		BNCore.registerListener(this);

		int taskId = BNCore.scheduleSyncRepeatingTask(() -> {
			for (Player _player : Bukkit.getOnlinePlayers()) {
				if (!players.containsKey(_player))
					continue;

				if (BNCore.isVanished(_player) && !player.hasPermission("vanish.see")) {
					remove(_player);
					continue;
				}

				players.put(_player, getDisplay(player));
			}
		}, 0, 5);

		return this;
	}

	@Override
	public BearNationTabList disable() {
		super.disable();
		HandlerList.unregisterAll(this);
		this.tabbed.getPlugin().getServer().getScheduler().cancelTask(this.taskId);
		return this;
	}

	// sorting by rank
	private void add(Player player) {
		add(getInsertLocation(player), new PlayerTabItem(player));
		this.players.put(player, getDisplay(player));
	}

	private void remove(Player player) {

	}

	public String getDisplay(Player player) {
		String display = BNCore.getRankDisplay(player) + player.getName();
		if (BNCore.isAfk(player)) {
			if (BNCore.isVanished(player)) {
				display += " &7[AFK] [V]";
			} else {
				display += " &7[AFK]";
			}
		} else if (BNCore.isVanished(player)) {
			if (player.hasPermission("vanish.see")) {
				display += " &7[V]";
			}
		}
		return display;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		BNCore.runTaskLater(() -> {
			Player _player = event.getPlayer();
			if (BNCore.isVanished(_player)) {
				if (!player.hasPermission("vanish.see")) {
					add(_player);
				}
			} else {
				add(_player);
			}
		}, 2);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		remove(event.getPlayer());
	}

	@EventHandler
	public void onVanishToggle(VanishStatusChangeEvent event) {
		boolean vanished = event.getValue();
		Player _player = event.getAffected().getBase();
		if (vanished) {
			if (!player.hasPermission("vanish.see")) {
				remove(_player);
			}
		} else {
			add(_player);
		}
	}

	private int getTabItemIndex(Player player) {
		for (Map.Entry<Integer, TabItem> item : this.items.entrySet()) {
			// items will always be players in this case, cast is safe
			PlayerTabItem tabItem = (PlayerTabItem) item.getValue();
			if (tabItem.getPlayer().equals(player))
				return item.getKey();
		}
		return -1;
	}

	private int getInsertLocation(Player player) {
		for (Map.Entry<Integer, TabItem> item : this.items.entrySet()) {
			// items will always be players in this case, cast is safe
			PlayerTabItem tabItem = (PlayerTabItem) item.getValue();

			if (player.getName().compareTo(tabItem.getPlayer().getName()) < 0)
				return item.getKey();
		}
		return getNextIndex();
	}

}
