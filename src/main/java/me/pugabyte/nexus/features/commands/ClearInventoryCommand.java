package me.pugabyte.nexus.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

@NoArgsConstructor
@Aliases("ci")
@Description("Discard of all items your inventory")
public class ClearInventoryCommand extends CustomCommand implements Listener {
	private ClearInventoryPlayer ciPlayer;
	private static Map<Player, ClearInventoryPlayer> players = new HashMap<>();

	ClearInventoryCommand(CommandEvent event) {
		super(event);
		ciPlayer = getPlayer(player());
	}

	public static ClearInventoryPlayer getPlayer(Player player) {
		if (!players.containsKey(player))
			players.put(player, new ClearInventoryPlayer(player));

		return players.get(player);
	}

	@Path
	void clear() {
		ciPlayer.addCache();
		inventory().setContents(new ItemStack[0]);
		send(PREFIX + "Inventory cleared. Undo with &c/ci undo");
	}

	@Path("undo")
	void undo() {
		ciPlayer.restoreCache();
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		ClearInventoryPlayer ciPlayer = getPlayer(event.getEntity());
		ciPlayer.removeCache();
	}

	public static class ClearInventoryPlayer {
		private Player player;
		private Map<String, ItemStack[]> cache = new HashMap<>();

		public ClearInventoryPlayer(Player player) {
			this.player = player;
		}

		String getKey() {
			return player.getWorld().getName().toLowerCase() + "-" + player.getGameMode().name().toLowerCase();
		}

		public void addCache() {
			cache.put(getKey(), player.getInventory().getContents());
		}

		public void removeCache() {
			cache.remove(getKey());
		}

		public void restoreCache() {
			String PREFIX = StringUtils.getPrefix("ClearInventory");
			if (cache.containsKey(getKey())) {
				for (ItemStack itemStack : player.getInventory().getContents()) {
					if (!isNullOrAir(itemStack)) {
						PlayerUtils.send(player, PREFIX + "Your inventory must be empty to restore an undo");
						return;
					}
				}
				player.getInventory().setContents(cache.get(getKey()));
				removeCache();
				PlayerUtils.send(player, PREFIX + "Inventory restored");
			} else {
				PlayerUtils.send(player, PREFIX + "There's nothing to undo!");
			}
		}

	}

}
