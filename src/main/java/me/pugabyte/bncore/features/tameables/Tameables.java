package me.pugabyte.bncore.features.tameables;

import me.pugabyte.bncore.features.tameables.models.TameablesAction;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Tameables {
	final static String PREFIX = Utils.getPrefix("Tameables");
	private static Map<Player, TameablesAction> actions = new HashMap<>();

	public Tameables() {
		new TameablesListener();
	}

	static Map<Player, TameablesAction> getPendingActions() {
		return actions;
	}

	static void addPendingAction(Player player, TameablesAction action) {
		actions.put(player, action);
	}

	static void removePendingAction(Player player) {
		actions.remove(player);
	}

}
