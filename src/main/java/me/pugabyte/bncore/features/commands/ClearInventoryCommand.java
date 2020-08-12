package me.pugabyte.bncore.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static me.pugabyte.bncore.utils.Utils.isNullOrAir;

@NoArgsConstructor
@Aliases({"clean", "clear", "ci", "clearinvent", "eclean", "eclear", "eci", "eclearinvent", "eclearinventory"})
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
		player().getInventory().setContents(new ItemStack[0]);
		send(PREFIX + "Inventory cleared. Undo with &c/clear undo");
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
						Utils.send(player, PREFIX + "Your inventory must be empty to restore an undo");
						return;
					}
				}
				player.getInventory().setContents(cache.get(getKey()));
				removeCache();
				Utils.send(player, PREFIX + "Inventory restored");
			} else {
				Utils.send(player, PREFIX + "There's nothing to undo!");
			}
		}

	}

}
