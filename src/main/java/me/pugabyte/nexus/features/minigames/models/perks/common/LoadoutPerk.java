package me.pugabyte.nexus.features.minigames.models.perks.common;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.mojang.datafixers.util.Pair;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.utils.MaterialTag;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
		return MaterialTag.COLORABLE.isTagged(item.getType());
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
		if (currentStack != null && (
				currentStack.getType() == Material.ZOMBIE_HEAD ||
				MaterialTag.ALL_BANNERS.isTagged(currentStack.getType()) ||
						(MaterialTag.COLORABLE.isTagged(currentStack.getType()) && !overrideColorables)
						))
			return;

		// self packet avoids playing the armor equip sound effect
		PacketContainer selfPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SET_SLOT);
		selfPacket.getIntegers().write(0, 0); // inventory ID (0 = player)
		selfPacket.getIntegers().write(1, 9-slot.getSlotFlag()); // dumb hack to get the slot ID of the armor sets
		selfPacket.getItemModifier().write(0, item);

		// other packet is sent to all other players to show the armor piece
		List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> equipmentList = new ArrayList<>();
		equipmentList.add(new Pair<>(slot, CraftItemStack.asNMSCopy(item)));
		PacketPlayOutEntityEquipment rawPacket = new PacketPlayOutEntityEquipment(player.getEntityId(), equipmentList);
		PacketContainer otherPacket = PacketContainer.fromPacket(rawPacket);

		players.stream().filter(_player -> player.getWorld() == _player.getWorld()).forEach(_player -> {
			PacketContainer packet = _player.equals(player) ? selfPacket : otherPacket;
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(_player, packet);
			} catch (InvocationTargetException ex) {
				Nexus.log("Error trying to send MGM collectible armour packets from " + player.getName() + " to " + _player.getName());
				ex.printStackTrace();
			}
		});
	}

	/**
	 * Thrown when a team loadout perk using the default {@link #getLoadout()} has neglected to override {@link #getMaterial()}
	 */
	public static class IncompleteLoadout extends RuntimeException {}
}
