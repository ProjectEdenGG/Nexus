package gg.projecteden.nexus.features.resourcepack.customblocks.customblockbreaking;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocksLang;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.StringUtils;
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

	public void createBrokenBlock(Block block, Player player, ItemStack itemStack) {
		Location location = block.getLocation();
		if (isTracking(location)) {
			CustomBlocksLang.debug(player, "<-- already tracking");
			return;
		}

		float blockHardness = BlockUtils.getBlockHardness(block);

		boolean isCustomBlock = false;
		CustomBlock customBlock = CustomBlock.from(block);
		if (customBlock != null) {
			blockHardness = (float) customBlock.get().getBlockHardness();
			isCustomBlock = true;
		}

		if (blockHardness == -1 || blockHardness > 50) { // unbreakable
			CustomBlocksLang.debug(player, "<-- block is unbreakable");
			return;
		}

		CustomBlocksLang.debug(player, "now tracking...");
		BrokenBlock brokenBlock = new BrokenBlock(block, isCustomBlock, player, itemStack, Bukkit.getCurrentTick());
		brokenBlocks.put(location, brokenBlock);
	}


	public void removeBrokenBlock(BrokenBlock brokenBlock) {
		removeBrokenBlock(brokenBlock.getLocation());
	}

	public void removeBrokenBlock(Block block) {
		removeBrokenBlock(block.getLocation());
	}

	public void removeBrokenBlock(Location location) {
		//CustomBlocksLang.debug("Removing block...");
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
