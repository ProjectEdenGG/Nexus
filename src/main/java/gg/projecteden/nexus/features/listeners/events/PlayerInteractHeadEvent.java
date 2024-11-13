package gg.projecteden.nexus.features.listeners.events;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class PlayerInteractHeadEvent extends PlayerEvent {

	@Getter
	private static final HandlerList handlerList = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

	Block block;
	ItemStack droppedItem;

	public PlayerInteractHeadEvent(@NotNull Player who, @NotNull Block block) {
		super(who);

		this.block = block;
		this.droppedItem = ItemUtils.getItem(block);
	}

	public @Nullable String getHeadDatabaseId() {
		return Nexus.getHeadAPI().getItemID(this.droppedItem);
	}
}
