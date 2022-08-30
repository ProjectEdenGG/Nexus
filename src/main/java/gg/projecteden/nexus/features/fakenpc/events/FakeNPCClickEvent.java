package gg.projecteden.nexus.features.fakenpc.events;

import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

public abstract class FakeNPCClickEvent extends FakeNPCEvent implements Cancellable {
	private boolean cancelled = false;
	@Getter
	Player clicker;

	public FakeNPCClickEvent(FakeNPC npc, Player clicker) {
		super(npc);
		this.clicker = clicker;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	public ItemStack getItemInMainHand() {
		return clicker.getInventory().getItemInMainHand();
	}

	public ItemStack getItemInOffHand() {
		return clicker.getInventory().getItemInOffHand();
	}
}
