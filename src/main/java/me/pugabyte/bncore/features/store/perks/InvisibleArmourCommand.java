package me.pugabyte.bncore.features.store.perks;

import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.invisiblearmour.InvisibleArmour;
import me.pugabyte.bncore.models.invisiblearmour.InvisibleArmourService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
@Aliases({"ia", "invisarmor", "invisarmour", "invisiblearmor"})
public class InvisibleArmourCommand extends CustomCommand implements Listener {
	InvisibleArmourService service = new InvisibleArmourService();

	public InvisibleArmourCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeat(Time.SECOND, Time.SECOND.x(5), InvisibleArmourCommand::sendPackets);
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		InvisibleArmour invisibleArmour = new InvisibleArmourService().get(event.getPlayer());
		if (!invisibleArmour.isEnabled())
			return;

		sendPackets();
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		InvisibleArmour invisibleArmour = new InvisibleArmourService().get(event.getPlayer());
		if (!invisibleArmour.isEnabled())
			return;

		sendPackets();
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
		InvisibleArmour invisibleArmour = new InvisibleArmourService().get((Player) event.getWhoClicked());
		if (!invisibleArmour.isEnabled())
			return;

		if (event.getSlotType() == SlotType.ARMOR) {
			event.setCancelled(true);
			sendPackets();
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		Tasks.wait(1, InvisibleArmourCommand::sendPackets);
	}

	static {
		List<ItemSlot> armourSlots = Arrays.asList(ItemSlot.HEAD, ItemSlot.CHEST, ItemSlot.LEGS, ItemSlot.FEET);

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(BNCore.getInstance(), Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				InvisibleArmour invisibleArmour = new InvisibleArmourService().get(event.getPlayer());
				if (!invisibleArmour.isEnabled())
					return;

				for (ItemSlot value : event.getPacket().getItemSlots().getValues())
					if (armourSlots.contains(value)) {
						event.setCancelled(true);
						return;
					}
			}

			// Not sure why this is needed, it kept erroring without it
			@Override
			public void onPacketSending(PacketEvent event) {}
		});

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(BNCore.getInstance(), Client.CLOSE_WINDOW) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				InvisibleArmour invisibleArmour = new InvisibleArmourService().get(event.getPlayer());
				if (!invisibleArmour.isEnabled())
					return;

				Tasks.wait(1, InvisibleArmourCommand::sendPackets);
			}
		});
	}

	private static void sendPackets() {
		AtomicInteger wait = new AtomicInteger(0);
		Bukkit.getOnlinePlayers().forEach(player -> Tasks.wait(wait.getAndIncrement(), () -> {
			InvisibleArmourService service = new InvisibleArmourService();
			InvisibleArmour invisibleArmour = service.get(player);
			if (!invisibleArmour.isEnabled())
				return;

			if (WorldGroup.get(player.getWorld()) == WorldGroup.MINIGAMES) {
				invisibleArmour.setEnabled(false);
				service.save(invisibleArmour);
				return;
			}

			if (invisibleArmour.helmet())
				sendPackets(invisibleArmour, ItemSlot.HEAD);
			if (invisibleArmour.chestplate())
				sendPackets(invisibleArmour, ItemSlot.CHEST);
			if (invisibleArmour.leggings())
				sendPackets(invisibleArmour, ItemSlot.LEGS);
			if (invisibleArmour.boots())
				sendPackets(invisibleArmour, ItemSlot.FEET);
		}));
	}

	private static void sendPackets(InvisibleArmour invisibleArmour, ItemSlot slot) {
		sendPackets(invisibleArmour, slot, new ItemStack(Material.AIR));
	}

	private static void sendPackets(InvisibleArmour invisibleArmour, ItemSlot slot, ItemStack item) {
		Player player = Utils.getPlayer(invisibleArmour.getUuid()).getPlayer();
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(Server.ENTITY_EQUIPMENT);
		packet.getItemSlots().write(0, slot);
		packet.getItemModifier().write(0, item);
		packet.getEntityModifier(player.getWorld()).write(0, player);

		Bukkit.getOnlinePlayers().stream().filter(_player -> player.getWorld() == _player.getWorld()).forEach(_player -> {
			try {
				if (player.getUniqueId() == _player.getUniqueId()) {
					if (!invisibleArmour.showSelf(slot))
						ProtocolLibrary.getProtocolManager().sendServerPacket(_player, packet);
				} else
					ProtocolLibrary.getProtocolManager().sendServerPacket(_player, packet);
			} catch (InvocationTargetException ex) {
				BNCore.log("Error trying to send invisible armour packets from " + player.getName() + " to " + _player.getName());
				ex.printStackTrace();
			}
		});
	}

}
