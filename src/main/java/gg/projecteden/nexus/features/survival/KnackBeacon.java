package gg.projecteden.nexus.features.survival;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import gg.projecteden.nexus.features.listeners.events.WorldGroupChangedEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import gg.projecteden.utils.TimeUtils.TickTime;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Chest;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

// TODO 1.19 remove
public class KnackBeacon extends Feature implements Listener {
	private static final double GOAL = 5904;
	private final List<Location> chests = List.of(
		new Location(Bukkit.getWorld("survival"), -2145.5, 50.5, 1797.5),
		new Location(Bukkit.getWorld("survival"), -2145.5, 51.5, 1797.5)
	);
	private KeyedBossBar bossBar;

	@Override
	public void onStart() {
		bossBar = Bukkit.getBossBar(NamespacedKey.minecraft("beacon_tracker"));
		if (bossBar == null) {
			Dev.LEXI.send("Beacon tracker bossbar could not be found; probably time for you to delete this feature :)");
			return;
		}

		Tasks.repeat(1, TickTime.SECOND.x(5), () -> {
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
					int multiplier = switch (item.getType()) {
						case NETHERITE_SCRAP -> 1;
						case NETHERITE_INGOT -> 4;
						case NETHERITE_BLOCK -> 9*4;
						default -> 0;
					};
					obtained += (item.getAmount() * multiplier);
				}
			}
			// update boss bar
			double progress = obtained / GOAL;
			bossBar.setProgress(Math.min(1, progress));
			Component title = new JsonBuilder("&cNetherite Beacon Progress &7(%.2f%%)".formatted(progress * 100)).build();
			bossBar.setTitle(LegacyComponentSerializer.legacySection().serialize(title));
		});
	}

	private void update(Player player) {
		if (WorldGroup.of(player) == WorldGroup.SURVIVAL)
			bossBar.addPlayer(player);
		else
			bossBar.removePlayer(player);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		update(event.getPlayer());
	}

	@EventHandler
	public void onWorldChange(WorldGroupChangedEvent event) {
		update(event.getPlayer());
	}
}
