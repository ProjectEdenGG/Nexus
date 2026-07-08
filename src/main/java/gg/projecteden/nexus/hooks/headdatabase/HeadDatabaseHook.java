package gg.projecteden.nexus.hooks.headdatabase;

import gg.projecteden.nexus.hooks.IHook;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class HeadDatabaseHook extends IHook<HeadDatabaseHook> {

	@Override
	protected @NotNull String getPluginName() {
		return "HeadDatabase";
	}

	public ItemStack getItemHead(String id) {
		return null;
	}

	public ItemStack getItemHead(Block block) {
		return null;
	}

	public String getItemID(ItemStack itemStack) {
		return null;
	}

	@Getter
	@SuppressWarnings("unused")
	public static class HeadDatabasePlayerClickHeadEvent extends Event implements Cancellable {
		private static final HandlerList handlers = new HandlerList();
		private final Player player;
		private final String categoryEnum;
		private final ItemStack head;
		private double price = 0.0D;
		private String headID = "";
		private boolean economy = false;
		private String economyEnum = "CURRENCY";
		@Setter
		private boolean cancelled = false;

		public HeadDatabasePlayerClickHeadEvent(
				Player player,
				double price,
				String headID,
				String economyEnum,
				ItemStack head,
				String categoryEnum
		) {
			this.player = player;
			this.price = price;
			this.headID = headID;
			this.economy = true;
			this.economyEnum = economyEnum;
			this.head = head;
			this.categoryEnum = categoryEnum;
		}

		public HeadDatabasePlayerClickHeadEvent(Player player, String headID, ItemStack head, String categoryEnum) {
			this.player = player;
			this.headID = headID;
			this.head = head;
			this.categoryEnum = categoryEnum;
		}

		public static HandlerList getHandlerList() {
			return handlers;
		}

		public ItemStack getHead() {
			return null;
		}

		public @NonNull HandlerList getHandlers() {
			return handlers;
		}
	}

}
