package gg.projecteden.nexus.features.customblocks.customblockbreaking;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BrokenBlocksManager {
	@Getter
	private static final Map<Location, BrokenBlock> brokenBlocks = new ConcurrentHashMap<>();
	private static final int expirationTicks = 6;

	public BrokenBlocksManager() {
		janitor();
	}

	public void createBrokenBlock(Player player, Block block, ItemStack itemStack) {
		if (isTracking(block))
			return;

		float blockHardness = BlockUtils.getBlockHardness(block);
		if (blockHardness == -1 || blockHardness > 50) // unbreakable
			return;

		BrokenBlock brokenBlock = new BrokenBlock(block.getLocation(), block, player, itemStack, Bukkit.getCurrentTick());
		brokenBlocks.put(block.getLocation(), brokenBlock);
	}


	public void removeBrokenBlock(BrokenBlock brokenBlock) {
		removeBrokenBlock(brokenBlock.getLocation());
	}

	public void removeBrokenBlock(Location location) {
		brokenBlocks.remove(location);
	}

	public BrokenBlock getBrokenBlock(Location location) {
		return brokenBlocks.get(location);
	}

	public boolean isTracking(Block block) {
		return isTracking(block.getLocation());
	}


	public boolean isTracking(Location location) {
		return brokenBlocks.containsKey(location);
	}

	private void janitor() {
		Tasks.repeat(0, TickTime.TICK.x(2), () -> {
			Map<Location, BrokenBlock> blocks = new HashMap<>(getBrokenBlocks());
			int currentTick = Bukkit.getCurrentTick();
			for (Location location : blocks.keySet()) {
				BrokenBlock damagedBlock = blocks.get(location);
				int tickDiff = currentTick - damagedBlock.getLastDamageTick();
				if (tickDiff < expirationTicks)
					continue;

				if (damagedBlock.isBroken() || !damagedBlock.isDamaged()) {
					damagedBlock.resetDamagePacket();
					damagedBlock.remove();
				} else
					damagedBlock.decrementDamage(currentTick);
			}
		});
	}
}
