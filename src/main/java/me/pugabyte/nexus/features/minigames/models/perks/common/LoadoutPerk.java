package me.pugabyte.nexus.features.minigames.models.perks.common;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.utils.MaterialTag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public abstract class LoadoutPerk extends TickablePerk {
	public abstract Map<EnumWrappers.ItemSlot, ItemStack> getLoadout();

	@Override
	public void tick(Minigamer minigamer) {
		// idk if this needs to be every tick (it needs to be on (re)spawn, atleast) and on inventory event probably
		getLoadout().forEach((itemSlot, itemStack) -> sendPackets(minigamer, itemStack, itemSlot));
	}

	private static void sendPackets(Minigamer minigamer, ItemStack item, EnumWrappers.ItemSlot slot) {
		Player player = minigamer.getPlayer();
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

		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
		packet.getItemSlots().write(0, slot);
		packet.getEntityModifier(player.getWorld()).write(0, player);
		packet.getItemModifier().write(0, item);

		minigamer.getMatch().getMinigamers().stream().map(Minigamer::getPlayer).filter(_player -> player.getWorld() == _player.getWorld()).forEach(_player -> {
//			boolean self = player.getUniqueId() == _player.getUniqueId();

			try {
				PacketContainer clone = packet.deepClone();

				// Causes a weird sound effect
//				if (self && invisibleArmour.showSelf(slot) && invisibleArmour.show(slot))
//					return;

				ProtocolLibrary.getProtocolManager().sendServerPacket(_player, clone);
			} catch (InvocationTargetException ex) {
				Nexus.log("Error trying to send invisible armour packets from " + player.getName() + " to " + _player.getName());
				ex.printStackTrace();
			}
		});
	}
}
