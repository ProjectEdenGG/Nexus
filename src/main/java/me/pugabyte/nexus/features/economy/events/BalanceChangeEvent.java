package me.pugabyte.nexus.features.economy.events;

import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@Getter
public class BalanceChangeEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final OfflinePlayer player;
	private final BigDecimal oldBalance;
	private final BigDecimal newBalance;
	private final BigDecimal changeAmount;
	private boolean cancelled;

	public BalanceChangeEvent(@NotNull OfflinePlayer player, BigDecimal oldBalance, BigDecimal newBalance) {
		this.player = player;
		this.oldBalance = oldBalance;
		this.newBalance = newBalance;
		this.changeAmount = newBalance.subtract(oldBalance);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
