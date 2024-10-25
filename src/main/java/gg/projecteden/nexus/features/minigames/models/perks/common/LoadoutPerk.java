package gg.projecteden.nexus.features.minigames.models.perks.common;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Map;

/**
 * A perk that gives a user fake armor items
 * @see HatPerk
 */
public interface LoadoutPerk extends TickablePerk {
	Map<EquipmentSlot, ItemStack> getLoadout();

	@Override
	default void tick(Minigamer minigamer) {
		getLoadout().forEach((itemSlot, itemStack) -> sendColorablePackets(minigamer.getOnlinePlayer(), minigamer.getMatch().getOnlinePlayers(), itemStack, itemSlot));
	}

	@Override
	default void tick(Player player) {
		getLoadout().forEach(((itemSlot, itemStack) -> sendColorablePackets(player, player.getWorld().getPlayers(), itemStack, itemSlot)));
	}

	default boolean isColorable(ItemStack item) {
		return false;
	}

	/**
	 * Same as {@link #sendPackets(Player, List, ItemStack, EquipmentSlot)} but uses {@link #isColorable(ItemStack)} to
	 * allow overriding
	 */
	default void sendColorablePackets(Player player, List<Player> players, ItemStack item, EquipmentSlot slot) {
		sendPackets(player, players, item, slot, isColorable(item));
	}

	static void sendPackets(Player player, List<Player> players, ItemStack item, EquipmentSlot slot) {
		sendPackets(player, players, item, slot, MaterialTag.COLORABLE.isTagged(item.getType()));
	}

	static void sendPackets(Player player, List<Player> players, ItemStack item, EquipmentSlot slot, boolean overrideColorables) {
		PlayerInventory inventory = player.getInventory();

		ItemStack currentStack = switch (slot) {
			case HEAD -> inventory.getHelmet();
			case CHEST -> inventory.getChestplate();
			case LEGS -> inventory.getLeggings();
			case FEET -> inventory.getBoots();
			default -> null;
		};

		if (currentStack != null) {
			// don't overwrite banners and don't overwrite colored armor (if the current item isn't colorable)
			Material type = currentStack.getType();
			boolean isZombieHead = type == Material.ZOMBIE_HEAD;
			boolean isBanner = MaterialTag.ALL_BANNERS.isTagged(type);
			boolean isColorable = MaterialTag.COLORABLE.isTagged(type);

			if (isZombieHead || isBanner || (isColorable && !overrideColorables))
				return;
		}

		PacketUtils.sendFakeItem(player, players, item, slot);
	}
}
