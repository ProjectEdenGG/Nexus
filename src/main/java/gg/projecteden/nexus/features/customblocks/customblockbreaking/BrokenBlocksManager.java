package gg.projecteden.nexus.features.customblocks.customblockbreaking;

import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BrokenBlocksManager {
	@Getter
	private static final Map<Location, BrokenBlock> brokenBlocks = new HashMap<>();
	private static final double expirationSeconds = 1.0;

	public BrokenBlocksManager() {
		janitor();
	}

	public void createBrokenBlock(Block block) {
		createBrokenBlock(block, -1);
	}

	public void createBrokenBlock(Block block, int time) {
		if (isTracking(block.getLocation()))
			return;

		BrokenBlock brokenBlock;
		if (time == -1)
			brokenBlock = new BrokenBlock(block, 0);
		else
			brokenBlock = new BrokenBlock(block, time);

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
		Tasks.repeat(0, TickTime.TICK.x(4), () -> {
			Map<Location, BrokenBlock> blocks = getBrokenBlocks();
			Date currentDate = new Date();
			blocks.values().forEach(damagedBlock -> {
				if (damagedBlock.getLastDamage() == null)
					return;

				if ((currentDate.getTime() - damagedBlock.getLastDamage().getTime()) / 1000.0 > expirationSeconds) {
					damagedBlock.decrementDamage(1);
				}
			});
		});
	}
}
