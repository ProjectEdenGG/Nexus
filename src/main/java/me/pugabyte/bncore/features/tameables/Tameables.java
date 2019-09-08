package me.pugabyte.bncore.features.tameables;

import me.pugabyte.bncore.Utils;
import me.pugabyte.bncore.features.tameables.models.TameablesAction;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Tameables {
	final static String PREFIX = Utils.getPrefix("Tameables");
	private Map<Player, TameablesAction> actions = new HashMap<>();

	public Tameables() {
		new TameablesTabCompleter();
		new TameablesListener();
	}

	Map<Player, TameablesAction> getPendingActions() {
		return actions;
	}

	void addPendingAction(Player player, TameablesAction action) {
		actions.put(player, action);
	}

	void removePendingAction(Player player) {
		actions.remove(player);
	}

}
