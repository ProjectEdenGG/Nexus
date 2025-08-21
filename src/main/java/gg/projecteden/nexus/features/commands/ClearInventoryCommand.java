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
import java.util.UUID;

@NoArgsConstructor
@Aliases({"ci", "clear"})
public class ClearInventoryCommand extends CustomCommand implements Listener {
	private ClearInventoryPlayerCache cache;
	private static final Map<UUID, ClearInventoryPlayerCache> CACHES = new HashMap<>();

	ClearInventoryCommand(CommandEvent event) {
		super(event);
		cache = getPlayer(player());
	}

	public static ClearInventoryPlayerCache getPlayer(Player player) {
		if (!CACHES.containsKey(player.getUniqueId()))
			CACHES.put(player.getUniqueId(), new ClearInventoryPlayerCache(player));

		return CACHES.get(player.getUniqueId());
	}

	@Path
	@Description("Discard all items your inventory")
	void clear() {
		inventory().setContents(cache.store());
		send(PREFIX + "Inventory cleared. Undo with &c/ci undo");
	}

	@Path("undo")
	@Description("Restore your recently cleared inventory")
	void undo() {
		cache.restoreCache();
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		ClearInventoryPlayerCache cache = getPlayer(event.getEntity());
		cache.clear();
	}

	public static class ClearInventoryPlayerCache {
		private final Player player;
		private final Map<String, ItemStack[]> cache = new HashMap<>();

		public ClearInventoryPlayerCache(Player player) {
			this.player = player;
		}

		String getKey() {
			return player.getWorld().getName().toLowerCase() + "-" + player.getGameMode().name().toLowerCase();
		}

		public ItemStack[] store() {
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

		public void clear() {
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
				clear();
				PlayerUtils.send(player, PREFIX + "Inventory restored");
			} else {
				PlayerUtils.send(player, PREFIX + "There's nothing to undo!");
			}
		}

	}

}
