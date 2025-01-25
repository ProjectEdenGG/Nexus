package gg.projecteden.nexus.hooks.bentobox;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.hooks.IHook;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class BentoBoxHook extends IHook<BentoBoxHook> {

	public World getOneBlockWorld() {
		return Bukkit.getWorld("oneblock_world");
	}

	public int getIslandRange(World world, Player player) {
		throw new InvalidInputException("BentoBox not loaded");
	}

	public void setIslandRange(World world, Player player, int range) {
		throw new InvalidInputException("BentoBox not loaded");
	}

}
