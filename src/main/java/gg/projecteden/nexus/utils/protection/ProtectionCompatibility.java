package gg.projecteden.nexus.utils.protection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@RequiredArgsConstructor
public abstract class ProtectionCompatibility {

	private final JavaPlugin mainPlugin;
	private final Plugin plugin;

	/**
	 * @param player Player looking to place a block
	 * @param target Place where the player seeks to place a block
	 * @return true if he can place the block
	 */
	public abstract boolean canBuild(Player player, Location target);

	/**
	 * @param player Player looking to break a block
	 * @param target Place where the player seeks to break a block
	 * @return true if he can break the block
	 */
	public abstract boolean canBreak(Player player, Location target);

	/**
	 * @param player Player looking to interact with a block
	 * @param target Place where the player seeks to interact with a block
	 * @return true if he can interact with the block
	 */
	public abstract boolean canInteract(Player player, Location target);

	/**
	 * @param player Player looking to use an item
	 * @param target Place where the player seeks to use an item at a location
	 * @return true if he can use an item at the location
	 */
	public abstract boolean canUse(Player player, Location target);

}
