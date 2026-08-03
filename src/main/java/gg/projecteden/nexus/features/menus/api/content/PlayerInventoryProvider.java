package gg.projecteden.nexus.features.menus.api.content;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.common.TemporaryListener;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.InventoryManager;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;

public abstract class PlayerInventoryProvider extends InventoryProvider implements TemporaryListener {

	private Map<Integer, ClickableItem> playerInvMap = new HashMap<>();

	@Override
	public Player getPlayer() {
		return viewer;
	}

	@Override
	public void open(Player viewer, int page) {
		Nexus.registerTemporaryListener(this);
		super.open(viewer, page);

		for (int i = 0; i < viewer.getInventory().getContents().length; i++)
			if (!playerInvMap.containsKey(i))
				PacketUtils.sendPacket(viewer, new ClientboundSetPlayerInventoryPacket(i, ItemStack.EMPTY));

		for (Integer key : playerInvMap.keySet())
			PacketUtils.sendPacket(viewer, new ClientboundSetPlayerInventoryPacket(key.intValue(), NMSUtils.toNMS(playerInvMap.get(key).getItem())));
	}

	@Override
	public void onClose(InventoryManager manager) {
		for (int i = 0; i < viewer.getInventory().getContents().length; i++)
			PacketUtils.sendPacket(viewer, new ClientboundSetPlayerInventoryPacket(i, NMSUtils.toNMS(viewer.getInventory().getContents()[i])));
		Nexus.unregisterTemporaryListener(this);
	}

	protected void addPlayerInvItem(int slot, ClickableItem item) {
		playerInvMap.put(slot, item);
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent event) {
		if (viewer == null) return;
		if (!isOpen()) return;
		if (event.getClickedInventory() == null) return;
		if (!event.getClickedInventory().equals(viewer.getInventory())) return;

		if (!playerInvMap.containsKey(event.getSlot())) {
			event.setCancelled(true);
			PacketUtils.sendPacket(viewer, new ClientboundSetPlayerInventoryPacket(event.getSlot(), ItemStack.EMPTY));
			return;
		}
		ClickableItem item = playerInvMap.get(event.getSlot());
		item.run(new ItemClickData(event, (Player) event.getWhoClicked(), item.getItem(), null));

		PacketUtils.sendPacket(viewer, new ClientboundSetPlayerInventoryPacket(event.getSlot(), NMSUtils.toNMS(playerInvMap.get(event.getSlot()).getItem())));
		Tasks.wait(1, () -> PacketUtils.sendPacket(viewer, new ClientboundSetCursorItemPacket(ItemStack.EMPTY)));
	}

}
