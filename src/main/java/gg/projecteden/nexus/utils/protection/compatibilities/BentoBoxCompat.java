package gg.projecteden.nexus.utils.protection.compatibilities;

import gg.projecteden.nexus.utils.protection.ProtectionCompatibility;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.lists.Flags;

public class BentoBoxCompat extends ProtectionCompatibility {

	BentoBox instance = BentoBox.getInstance();

	public BentoBoxCompat(JavaPlugin mainPlugin, Plugin plugin) {
		super(mainPlugin, plugin);
	}

	/**
	 * @param player Player looking to place a block
	 * @param target Place where the player seeks to place a block
	 * @return true if he can put the block
	 */
	@Override
	public boolean canBuild(Player player, Location target) {
		return canDo(player, target, Flags.PLACE_BLOCKS);
	}

	private boolean canDo(Player player, Location target, Flag flag) {
		// Check if in world
		if (!instance.getIWM().inWorld(target)) {
			return true;
		}
		return instance.getIslands().getIslandAt(target).map(island -> island.isAllowed(User.getInstance(player), flag)).orElse(!flag.isSetForWorld(target.getWorld()));

	}

	/**
	 * @param player Player looking to break a block
	 * @param target Place where the player seeks to break a block
	 * @return true if he can break the block
	 */
	@Override
	public boolean canBreak(Player player, Location target) {
		return canDo(player, target, Flags.BREAK_BLOCKS);
	}

	/**
	 * @param player Player looking to interact with a block
	 * @param target Place where the player seeks to interact with a block
	 * @return true if he can interact with the block
	 */
	@Override
	public boolean canInteract(Player player, Location target) {
		// Check if in world
		if (!instance.getIWM().inWorld(target)) {
			return true;
		}
		// No single interact flag, so just check if player is on their own island
		return instance.getIslands().locationIsOnIsland(player, target);
	}

	/**
	 * @param player Player looking to use an item
	 * @param target Place where the player seeks to use an item at a location
	 * @return true if he can use the item at the location
	 */
	public boolean canUse(Player player, Location target) {
		// Check if in world
		if (!instance.getIWM().inWorld(target)) {
			return true;
		}
		// No single use flag, so just check if player is on their own island
		return instance.getIslands().locationIsOnIsland(player, target);
	}
}
