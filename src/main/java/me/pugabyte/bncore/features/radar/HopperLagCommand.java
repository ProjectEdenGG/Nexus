package me.pugabyte.bncore.features.radar;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor
@Permission("group.staff")
public class HopperLagCommand extends CustomCommand implements Listener {
	private DecimalFormat nf = new DecimalFormat("#,###");

	public static Map<Location, Double> hopperLagMap = new HashMap<>();

	public HopperLagCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void hopperLag(@Arg("1") int amount) {
		if (amount > 10)
			amount = 10;

		LinkedHashMap<Location, Double> sortedMap = new LinkedHashMap<>();

		if (hopperLagMap.size() <= 0) error("There are currently no logs for Hopper Lag.");
		hopperLagMap.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> {
					sortedMap.put(x.getKey(), x.getValue());
				});

		if (sortedMap.size() <= 0) error("There are currently no logs for Hopper Lag.");

		for (int i = 1; i <= amount; i++) {
			if (sortedMap.keySet().size() < i) break;
			Location loc = (Location) sortedMap.keySet().toArray()[i - 1];
			double value = (double) sortedMap.values().toArray()[i - 1];
			String message = StringUtils.getLocationString(loc) + " &7- " + nf.format(value);
			StringUtils.sendJsonLocation("&3" + i + ". " + message, loc, player());
		}

		hopperLagMap.clear();
	}

	@EventHandler
	public void onInvMove(InventoryMoveItemEvent event) {
		if (!event.getInitiator().getType().equals(InventoryType.HOPPER)) return;
		Location loc = event.getInitiator().getLocation();
		if (hopperLagMap.containsKey(loc))
			hopperLagMap.put(loc, hopperLagMap.get(loc) + 1);
		else hopperLagMap.put(loc, 1.0);
	}
}
