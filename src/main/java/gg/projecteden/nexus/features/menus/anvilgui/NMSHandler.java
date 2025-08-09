package gg.projecteden.nexus.features.menus.anvilgui;

import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public class NMSHandler {

	private int getRealNextContainerId(Player player) {
		return this.toNMS(player).nextContainerCounter();
	}

	private ServerPlayer toNMS(Player player) {
		return ((CraftPlayer) player).getHandle();
	}

	public int getNextContainerId(Player player, AnvilContainer container) {
		return container.getContainerId();
	}

	public void handleInventoryCloseEvent(Player player) {
		CraftEventFactory.handleInventoryCloseEvent(this.toNMS(player), Reason.PLAYER);
	}

	public void sendPacketOpenWindow(Player player, AnvilContainer container, int containerId, String inventoryTitle) {
		this.toNMS(player).connection.send(new ClientboundOpenScreenPacket(container.containerId, container.getType(), container.getTitle()));
//		this.toNMS(player).b.a(new PacketPlayOutOpenWindow(containerId, Containers.h, IChatBaseComponent.a(inventoryTitle)));
	}

	public void sendPacketCloseWindow(Player player, int containerId) {
		this.toNMS(player).connection.send(new ClientboundContainerClosePacket(containerId));
//		this.toNMS(player).b.a(new PacketPlayOutCloseWindow(containerId));
	}

	public void setActiveContainerDefault(Player player) {
		this.toNMS(player).containerMenu = this.toNMS(player).inventoryMenu;
//		this.toNMS(player).bU = this.toNMS(player).bT;
	}

	public void setActiveContainer(Player player, AnvilContainer container) {
		this.toNMS(player).containerMenu = container;
//		this.toNMS(player).bU = (Container)container;
	}

	public void setActiveContainerId(AnvilContainer container, int containerId) {

	}

	public void addActiveContainerSlotListener(AnvilContainer container, Player player) {
		this.toNMS(player).initMenu(container);
//		this.toNMS(player).a((Container)container);
	}

	public Inventory toBukkitInventory(Object container) {
		return ((AnvilMenu) container).getBukkitView().getTopInventory();
	}

	public AnvilContainer newContainerAnvil(Player player, String title) {
		return new AnvilContainer(player, this.getRealNextContainerId(player), title);
	}

	public static class AnvilContainer extends net.minecraft.world.inventory.AnvilMenu {
		public AnvilContainer(Player player, int containerId, String guiTitle) {
			super(containerId, ((CraftPlayer) player).getHandle().getInventory(), ContainerLevelAccess.create(((CraftWorld) player.getWorld()).getHandle(), new BlockPos(0, 0, 0)));
			this.checkReachable = false;
			this.setTitle(Component.literal(guiTitle));
		}

		//		public void l() {
//			super.l();
//			this.w.a(0);
//		}
		@Override
		public void createResult() {
			super.createResult();
			this.cost.set(0);
		}

		//		public void b(EntityHuman player) {}
		@Override
		public void removed(net.minecraft.world.entity.player.@NotNull Player player) {}

		//		protected void a(EntityHuman player, IInventory container) {}
		@Override
		protected void clearContainer(net.minecraft.world.entity.player.@NotNull Player player, @NotNull Container inventory) {}

		public int getContainerId() {
			return this.containerId;
			// return this.j;
		}
	}
}
