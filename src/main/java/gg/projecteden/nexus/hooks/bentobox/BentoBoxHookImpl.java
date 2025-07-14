package gg.projecteden.nexus.hooks.bentobox;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import world.bentobox.aoneblock.AOneBlock;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.IslandsManager;

public class BentoBoxHookImpl extends BentoBoxHook {

	@Override
	public int getIslandRange(World world, Player player) {
		return getIsland(world, player).getProtectionRange();
	}

	@Override
	public void setIslandRange(World world, Player player, int range) {
		getIsland(world, player).setProtectionRange(range);
	}

	public int getOneBlockCount(World world, Player player) {
		return oneBlock().getOneBlocksIsland(getIsland(world, player)).getBlockNumber();
	}

	private @NotNull Island getIsland(World world, Player player) {
		Island island = getIslands().getIsland(world, player.getUniqueId());
		if (island == null)
			throw new InvalidInputException("Island not found %s %s".formatted(world.getName(), player.getName()));
		return island;
	}

	private IslandsManager getIslands() {
		return api().getIslands();
	}

	private BentoBox api() {
		return BentoBox.getInstance();
	}

	private AOneBlock oneBlock() {
		return BentoBox.getInstance().getAddonsManager().<AOneBlock>getAddonByName("AOneBlock").orElseThrow(() -> new InvalidInputException("AOneBlock not loaded"));
	}

}
