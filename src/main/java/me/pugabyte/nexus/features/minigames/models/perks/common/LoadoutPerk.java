package me.pugabyte.nexus.features.minigames.models.perks.common;

import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PacketUtils;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A perk that gives a user fake armor items. As this is usually used for hats, most subclasses should only need to
 * override {@link #getMaterial()} or {@link #getItem()}. More complex loadouts should override {@link #getLoadout()}
 * and {@link #getMenuItem()}.
 */
public abstract class LoadoutPerk extends TickablePerk {
	@Override
	public PerkCategory getPerkCategory() {
		return PerkCategory.HAT;
	}

	public Map<EnumItemSlot, ItemStack> getLoadout() {
		Map<EnumItemSlot, ItemStack> loadout = new HashMap<>();
		loadout.put(EnumItemSlot.HEAD, getItem());
		return loadout;
	}

	public Material getMaterial() {
		return null;
	}

	public ItemStack getItem() {
		if (getMaterial() == null || !getMaterial().isItem())
			throw new IncompleteLoadout();
		return new ItemStack(getMaterial());
	}

	@Override
	public ItemStack getMenuItem() {
		return getItem();
	}

	@Override
	public void tick(Minigamer minigamer) {
		getLoadout().forEach((itemSlot, itemStack) -> sendColorablePackets(minigamer.getPlayer(), minigamer.getMatch().getPlayers(), itemStack, itemSlot));
	}

	@Override
	public void tick(Player player) {
		getLoadout().forEach(((itemSlot, itemStack) -> sendColorablePackets(player, player.getWorld().getPlayers(), itemStack, itemSlot)));
	}

	protected boolean isColorable(ItemStack item) {
		return false;
	}

	/**
	 * Same as {@link #sendPackets(Player, List, ItemStack, EnumItemSlot)} but uses {@link #isColorable(ItemStack)} to
	 * allow overriding
	 */
	protected void sendColorablePackets(Player player, List<Player> players, ItemStack item, EnumItemSlot slot) {
		sendPackets(player, players, item, slot, isColorable(item));
	}

	public static void sendPackets(Player player, List<Player> players, ItemStack item, EnumItemSlot slot) {
		sendPackets(player, players, item, slot, MaterialTag.COLORABLE.isTagged(item.getType()));
	}

	public static void sendPackets(Player player, List<Player> players, ItemStack item, EnumItemSlot slot, boolean overrideColorables) {
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
		if (currentStack != null) {
			Material type = currentStack.getType();
			boolean isZombieHead = type == Material.ZOMBIE_HEAD;
			boolean isBanner = MaterialTag.ALL_BANNERS.isTagged(type);
			boolean isColorable = MaterialTag.COLORABLE.isTagged(type);

			if (isZombieHead || isBanner || (isColorable && !overrideColorables))
				return;
		}

		PacketUtils.setSlot(player, players, item, slot);
	}

	/**
	 * Thrown when a team loadout perk using the default {@link #getLoadout()} has neglected to override {@link #getMaterial()}
	 */
	public static class IncompleteLoadout extends NexusException {}
}
