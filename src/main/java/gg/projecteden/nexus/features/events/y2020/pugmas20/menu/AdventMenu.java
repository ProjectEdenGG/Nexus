package gg.projecteden.nexus.features.events.y2020.pugmas20.menu;

import gg.projecteden.nexus.features.events.y2020.pugmas20.Pugmas20;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class AdventMenu {
	private static final Location adventHeadsLoc = Pugmas20.location(870, 44, 573);
	public static final Block origin = adventHeadsLoc.getBlock().getRelative(BlockFace.UP);
	@Getter
	private static final LinkedHashMap<SlotPos, ItemBuilder> adventHeadMap = new LinkedHashMap<>();
	public static ItemBuilder lockedHead;
	public static ItemBuilder missedHead;
	public static ItemBuilder toFindHead;

	public static void loadHeads() {
		// Specific Heads
		origin.getRelative(0, 0, 2).getDrops().stream().findFirst().ifPresent(skull -> lockedHead = new ItemBuilder(skull));
		origin.getRelative(0, 0, 3).getDrops().stream().findFirst().ifPresent(skull -> missedHead = new ItemBuilder(skull));
		origin.getRelative(0, 0, 4).getDrops().stream().findFirst().ifPresent(skull -> toFindHead = new ItemBuilder(skull));

		// Advent menu
		String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturaday"};

		for (int z = 0; z <= 4; z++) {        // 0-4 col
			for (int x = 1; x <= 7; x++) {    // 1-7 row
				Block block = origin.getRelative(x, 0, z);
				if (!Nullables.isNullOrAir(block)) {
					ItemStack drop = block.getDrops().stream().findFirst().orElse(null);
					if (!Nullables.isNullOrAir(drop)) {
						ItemBuilder skull = new ItemBuilder(drop);
						int size = adventHeadMap.size();
						if (size <= 6)
							skull.name(days[size]);

						adventHeadMap.put(new SlotPos(z, x), skull);
					}
				}
			}
		}
	}

}
