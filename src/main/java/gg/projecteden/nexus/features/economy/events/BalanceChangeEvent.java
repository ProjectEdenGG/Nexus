package gg.projecteden.nexus.features.economy.events;

import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class BalanceChangeEvent extends NexusEconomyEvent implements Cancellable {
	private final UUID uuid;
	private final BigDecimal oldBalance;
	private final BigDecimal newBalance;
	private final BigDecimal changeAmount;
	private final ShopGroup shopGroup;
	private boolean cancelled;

	public BalanceChangeEvent(@NotNull UUID uuid, BigDecimal oldBalance, BigDecimal newBalance, ShopGroup shopGroup) {
		this.uuid = uuid;
		this.oldBalance = oldBalance;
		this.newBalance = newBalance;
		this.changeAmount = newBalance.subtract(oldBalance);
		this.shopGroup = shopGroup;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
