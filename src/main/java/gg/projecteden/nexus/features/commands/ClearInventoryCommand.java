package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Aliases({"ci", "clear"})
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
	@Description("Discard all items your inventory")
	void clear() {
		inventory().setContents(ciPlayer.addCache());
		send(PREFIX + "Inventory cleared. Undo with &c/ci undo");
	}

	@Path("undo")
	@Description("Restore your recently cleared inventory")
	void undo() {
		ciPlayer.restoreCache();
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		ClearInventoryPlayer ciPlayer = getPlayer(event.getEntity());
		ciPlayer.removeCache();
	}

	public static class ClearInventoryPlayer {
		private final Player player;
		private final Map<String, ItemStack[]> cache = new HashMap<>();

		public ClearInventoryPlayer(Player player) {
			this.player = player;
		}

		String getKey() {
			return player.getWorld().getName().toLowerCase() + "-" + player.getGameMode().name().toLowerCase();
		}

		public ItemStack[] addCache() {
			ItemStack[] contents = player.getInventory().getContents();
			ItemStack[] untrashable = new ItemStack[contents.length];

			for (int i = 0; i < contents.length; i++) {
				ItemStack content = contents[i];
				if (Nullables.isNullOrAir(content))
					continue;

				if (new ItemBuilder(content).is(ItemSetting.TRASHABLE))
					continue;

				untrashable[i] = content;
				contents[i] = null;
			}

			cache.put(getKey(), contents);
			return untrashable;
		}

		public void removeCache() {
			cache.remove(getKey());
		}

		public void restoreCache() {
			String PREFIX = StringUtils.getPrefix("ClearInventory");
			if (cache.containsKey(getKey())) {
				for (ItemStack itemStack : player.getInventory().getContents()) {
					if (!Nullables.isNullOrAir(itemStack)) {
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
