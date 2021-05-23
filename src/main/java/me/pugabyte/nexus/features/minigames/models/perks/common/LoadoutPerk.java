package me.pugabyte.nexus.features.minigames.models.perks.common;

import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PacketUtils;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Map;

/**
 * A perk that gives a user fake armor items
 * @see HatPerk
 */
public interface LoadoutPerk extends TickablePerk {
	Map<EnumItemSlot, ItemStack> getLoadout();

	@Override
	default void tick(Minigamer minigamer) {
		getLoadout().forEach((itemSlot, itemStack) -> sendColorablePackets(minigamer.getPlayer(), minigamer.getMatch().getPlayers(), itemStack, itemSlot));
	}

	@Override
	default void tick(Player player) {
		getLoadout().forEach(((itemSlot, itemStack) -> sendColorablePackets(player, player.getWorld().getPlayers(), itemStack, itemSlot)));
	}

	default boolean isColorable(ItemStack item) {
		return false;
	}

	/**
	 * Same as {@link #sendPackets(Player, List, ItemStack, EnumItemSlot)} but uses {@link #isColorable(ItemStack)} to
	 * allow overriding
	 */
	default void sendColorablePackets(Player player, List<Player> players, ItemStack item, EnumItemSlot slot) {
		sendPackets(player, players, item, slot, isColorable(item));
	}

	static void sendPackets(Player player, List<Player> players, ItemStack item, EnumItemSlot slot) {
		sendPackets(player, players, item, slot, MaterialTag.COLORABLE.isTagged(item.getType()));
	}

	static void sendPackets(Player player, List<Player> players, ItemStack item, EnumItemSlot slot, boolean overrideColorables) {
		PlayerInventory inventory = player.getInventory();

		ItemStack currentStack;
		switch (slot) {
			case HEAD:
				currentStack = inventory.getHelmet();
				break;
			case CHEST:
				currentStack = inventory.getChestplate();
				break;
			case LEGS:
				currentStack = inventory.getLeggings();
				break;
			case FEET:
				currentStack = inventory.getBoots();
				break;
			default:
				return;
		}

		// don't overwrite banners and don't overwrite colored armor (if the current item isn't colorable)
		if (currentStack != null && (
				currentStack.getType() == Material.ZOMBIE_HEAD ||
				MaterialTag.ALL_BANNERS.isTagged(currentStack.getType()) ||
						(MaterialTag.COLORABLE.isTagged(currentStack.getType()) && !overrideColorables)
						))
			return;

		PacketUtils.sendFakeItem(player, players, item, slot);
	}
}
