package gg.projecteden.nexus.features.customblocks.customblockbreaking;

import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BrokenBlocksManager {
	@Getter
	private static final Map<Location, BrokenBlock> brokenBlocks = new ConcurrentHashMap<>();
	private static final double expirationSeconds = 1.0;

	public BrokenBlocksManager() {
		janitor();
	}

	public void createBrokenBlock(Block block) {
		if (isTracking(block.getLocation()))
			return;

		BrokenBlock brokenBlock = new BrokenBlock(block);
		brokenBlocks.put(block.getLocation(), brokenBlock);
	}


	public void removeBrokenBlock(BrokenBlock brokenBlock) {
		removeBrokenBlock(brokenBlock.getBlock().getLocation());
	}

	public void removeBrokenBlock(Location location) {
		brokenBlocks.remove(location);
	}

	public BrokenBlock getBrokenBlock(Location location) {
		createBrokenBlock(location.getBlock());
		return brokenBlocks.get(location);
	}

	public boolean isTracking(Location location) {
		return brokenBlocks.containsKey(location);
	}

	private void janitor() {
		Tasks.repeat(0, TickTime.TICK.x(5), () -> {
			Map<Location, BrokenBlock> blocks = new HashMap<>(getBrokenBlocks());
			for (Location location : blocks.keySet()) {
				BrokenBlock damagedBlock = blocks.get(location);
				if (damagedBlock.getLastDamage() == null)
					continue;

				if (damagedBlock.isBroken() || !damagedBlock.isDamaged()) {
					damagedBlock.resetBreakPacket();
					removeBrokenBlock(damagedBlock);
					continue;
				}

				long seconds = damagedBlock.getLastDamage().until(LocalDateTime.now(), ChronoUnit.SECONDS);
//				Dev.WAKKA.send("seconds = " + seconds + " (" + damagedBlock.getDamage() + "/" + damagedBlock.getHardness() + ")");

				if (seconds > expirationSeconds) {
					damagedBlock.decrementDamage();
				}
			}
		});
	}
}
