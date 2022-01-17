package gg.projecteden.nexus.features.achievements.listeners;

import gg.projecteden.nexus.models.achievement.Achievement;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class EconomyListener implements Listener {
	public void checkBalance(Player player) {
		if (player.getWorld().getName().startsWith("world")) {
			Achievement.COMMONWEALTH.check(player);
			Achievement.UPPER_CLASS.check(player);
			Achievement.BOUGHT_AND_PAID_FOR.check(player);
		}
	}

/*
// TODO New econ handlers
	@EventHandler
	public void onBalanceUpdated(UserBalanceUpdateEvent event) {
		checkBalance(event.getPlayer());
	}

	@EventHandler
	public void onEssSignInteract(EssSignInteractEvent event) {
		checkBalance(event.getPlayer());
	}

	@EventHandler
	public void onPay(PayEvent event) {
		Player target = event.getPlayer();
		checkBalance(target);
		Achievement.STEADY_INCOME.check(target);

		Player player = event.getPlayer();
		Achievement.PERFECT_TRADE.check(target, (int) event.getAmount());
		Achievement.HIRED_HELP.check(player);
	}

	@EventHandler
	public void onTradeSignPurchase(TradeSignPurchaseEvent event) {
		Player buyer = event.getBuyer().getPlayer();

		Achievement.BIG_SPENDER.check(buyer, (int) event.getAmount());
		Achievement.SPREADING_THE_WEALTH.check(buyer);
	}

	@EventHandler
	public void onOutOfStock(OutOfStockEvent event) {
		Achievement.IN_DEMAND.check(event.getOwner().getPlayer());
	}

	@EventHandler
	public void onBuyFromMarket(BuySignInteractEvent event) {
		Player player = event.getPlayer();
		int price = Integer.parseInt(((Sign) event.getBlock().getState()).getLine(3).replaceAll("\\$", ""));

		Essentials ess = (Essentials) Achievements.getInstance().getServer().getPluginManager().getPlugin("Essentials");
		User user = ess.getUser(player);
		int balance = user.getMoney().intValue();

		boolean canAfford = false;
		if (balance >= price) {
			canAfford = true;
		} else if (player.hasPermission("essentials.eco.loan") && balance >= (price - 5000)) {
			canAfford = true;
		}

		if (canAfford) {
			Achievement.SPENDER.check(player);
			Achievement.SHOPAHOLIC.check(player);
		}

	}

*/

}
