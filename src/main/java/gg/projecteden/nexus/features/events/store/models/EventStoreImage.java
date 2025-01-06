package gg.projecteden.nexus.features.events.store.models;

import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.WorldEditUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class EventStoreImage {
	private String id;
	private ItemStack splatterMap;

	public static final Map<String, EventStoreImage> IMAGES = new HashMap<>();

	public static EventStoreImage of(String title) {
		return IMAGES.get(title);
	}

	public static void reload() {
		try {
			IMAGES.clear();

			final World server = Bukkit.getWorld("server");
			if (server == null)
				return;

			WorldEditUtils worldedit = new WorldEditUtils(server);

			// TODO Change to chunks/getTiteEntities
			for (Block block : worldedit.getBlocks(worldedit.worldguard().getRegion("images"))) {
				try {
					if (!MaterialTag.SIGNS.isTagged(block.getType()))
						continue;
					if (!(block.getState() instanceof Sign sign))
						continue;

					String id = (sign.getLine(0).trim() + " " + sign.getLine(1).trim()).trim();
					if (Nullables.isNullOrEmpty(id))
						continue;

					if (!(block.getBlockData() instanceof WallSign wallSign))
						continue;

					Block chest = block.getRelative(wallSign.getFacing().getOppositeFace());
					if (!(chest.getState() instanceof Chest inventory))
						continue;

					ItemStack map = inventory.getBlockInventory().getContents()[0];

					if (Nullables.isNullOrAir(map))
						continue;

					map = map.clone();

					if (map.getItemMeta().hasLore()) {
						String[] split = map.getItemMeta().getDisplayName().split("-");
						String name = String.join("-", Arrays.copyOfRange(split, 1, split.length));
						new ItemBuilder(map, true)
							.name("&6" + id + " &8-" + name)
							.loreRemove(1)
							.build();
					}

					IMAGES.put(id, new EventStoreImage(id, map));
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

}
