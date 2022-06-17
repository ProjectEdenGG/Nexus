package gg.projecteden.nexus.features.survival;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import net.kyori.adventure.bossbar.BossBar.Listener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Chest;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KnackBeacon extends Feature implements Listener {
	private static final int GOAL = 5904;
	private final List<Location> chests = List.of(
		new Location(Bukkit.getWorld("survival"), -2145.5, 50.5, 1797.5),
		new Location(Bukkit.getWorld("survival"), -2145.5, 50.5, 1796.5),
		new Location(Bukkit.getWorld("survival"), -2145.5, 50.5, 1797.5),
		new Location(Bukkit.getWorld("survival"), -2145.5, 50.5, 1796.5)
	);
	private KeyedBossBar bossBar;

	@Override
	public void onStart() {
		bossBar = Bukkit.getBossBar(NamespacedKey.minecraft("beacon_tracker"));
		if (bossBar == null) {
			Dev.LEXI.send("Beacon tracker bossbar could not be found; probably time for you to delete this feature :)");
			return;
		}

		Tasks.repeat(1, TickTime.SECOND.x(10), () -> {
			int obtained = 0;
			// count blocks in beacon
			WorldGuardUtils wg = new WorldGuardUtils("survival");
			var beaconRegion = wg.getRegion("knack_netherite_beacon");
			for (BlockVector3 vector : beaconRegion) {
				BlockState block = vector.getBlock(wg.getWorldEditWorld());
				if (block != null && block.getBlockType().equals(BlockTypes.NETHERITE_BLOCK)) {
					obtained += (9 * 4);
				}
			}
			// count scrap and ingots in storage chest
			for (Location chestLocation : chests) {
				if (!(chestLocation.getBlock().getState() instanceof Chest chest)) {
					Dev.LEXI.send("Chest at " + chestLocation + " is not a chest, it is a " + chestLocation.getBlock().getState().getClass().getSimpleName() + " (" + chestLocation.getBlock().getType() + ")");
					continue;
				}
				for (ItemStack item : chest.getInventory()) {
					if (item == null) continue;
					if (item.getType().equals(Material.NETHERITE_INGOT)) {
						obtained += (item.getAmount() * 4);
					} else if (item.getType().equals(Material.NETHERITE_SCRAP)) {
						obtained += item.getAmount();
					}
				}
			}
			// update boss bar
			bossBar.setProgress((double) obtained / GOAL);
		});
	}
}
