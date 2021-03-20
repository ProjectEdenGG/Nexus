package me.pugabyte.nexus.features.minigames.models.perks.common;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.mojang.datafixers.util.Pair;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.utils.MaterialTag;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class LoadoutPerk extends TickablePerk {
	public abstract Map<EnumItemSlot, ItemStack> getLoadout();

	@Override
	public void tick(Minigamer minigamer) {
		// idk if this needs to be every tick (it needs to be on (re)spawn, atleast) and on inventory event probably
		getLoadout().forEach((itemSlot, itemStack) -> sendPackets(minigamer.getPlayer(), minigamer.getMatch().getPlayers(), itemStack, itemSlot));
	}

	@Override
	public void tick(Player player) {
		getLoadout().forEach(((itemSlot, itemStack) -> sendPackets(player, player.getWorld().getPlayers(), itemStack, itemSlot)));
	}

	public static void sendPackets(Player player, List<Player> players, ItemStack item, EnumItemSlot slot) {
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

		// don't overwrite team-identifying armor
		if (currentStack != null && MaterialTag.COLORABLE.isTagged(currentStack.getType()))
			return;

		List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> equipmentList = new ArrayList<>();
		equipmentList.add(new Pair<>(slot, CraftItemStack.asNMSCopy(item)));
		PacketPlayOutEntityEquipment rawPacket = new PacketPlayOutEntityEquipment(player.getEntityId(), equipmentList);
		PacketContainer packet = PacketContainer.fromPacket(rawPacket);

		players.stream().filter(_player -> player.getWorld() == _player.getWorld()).forEach(_player -> {
			try {
				PacketContainer clone = packet.deepClone();
				ProtocolLibrary.getProtocolManager().sendServerPacket(_player, clone);
			} catch (InvocationTargetException ex) {
				Nexus.log("Error trying to send invisible armour packets from " + player.getName() + " to " + _player.getName());
				ex.printStackTrace();
			}
		});
	}
}
