package gg.projecteden.nexus.features.menus.api.content;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.common.TemporaryListener;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.resourcepack.models.font.InventoryTexture;
import gg.projecteden.nexus.features.titan.serverbound.Scroll.PlayerScrollMenuEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class ScrollableInventoryProvider extends InventoryProvider implements TemporaryListener {

	private static final String MOUSETWEAKS_FEATURE_CONTROL = "mousetweaks:feature_control";
	private static final byte[] DISABLE_PACKET;
	private static final byte[] ENABLE_PACKET;

	@Override
	public String getTitle(int page) {
		return InventoryTexture.getScrollTitle(getPages(), page);
	}

	@Override
	public void init() {
		final int page = contents.pagination().getPage();
		if (page > 0)
			contents.set(8, ClickableItem.of(new ItemBuilder(ItemModelType.INVISIBLE).name("&eScroll Up").build(), e -> open(viewer, page - 1)));
		else
			contents.set(8, ClickableItem.AIR);

		if (page < (getPages() - 1))
			contents.set(53, ClickableItem.of(new ItemBuilder(ItemModelType.INVISIBLE).name("&eScroll Down").build(), e -> open(viewer, page + 1)));
		else
			contents.set(53, ClickableItem.AIR);

		getViewer().sendPluginMessage(Nexus.getInstance(), MOUSETWEAKS_FEATURE_CONTROL, DISABLE_PACKET);
		Nexus.registerTemporaryListener(this);
	}

	@Override
	public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
		getViewer().sendPluginMessage(Nexus.getInstance(), MOUSETWEAKS_FEATURE_CONTROL, ENABLE_PACKET);
		Nexus.unregisterTemporaryListener(this);
	}

	@Override
	public Player getPlayer() {
		return getViewer();
	}

	@EventHandler
	public void onScroll(PlayerScrollMenuEvent event) {
		if (!event.getPlayer().equals(getViewer())) return;
		final int page = contents.pagination().getPage();
		if (page > 0 && !event.isUp())
			open(viewer, page - 1);
		else if (page < (getPages() - 1) && event.isUp())
			open(viewer, page + 1);
	}

	abstract public int getPages();

	static {
		Bukkit.getMessenger().registerOutgoingPluginChannel(Nexus.getInstance(), MOUSETWEAKS_FEATURE_CONTROL);
		DISABLE_PACKET = "!wheel".getBytes(StandardCharsets.UTF_8);
		ENABLE_PACKET = "wheel".getBytes(StandardCharsets.UTF_8);
	}

}
