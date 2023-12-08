package gg.projecteden.nexus.features.radar;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor
@Permission(Group.STAFF)
public class HopperLagCommand extends CustomCommand implements Listener {
	private static final String PREFIX = "&7&l[&cRadar&7&l]&f ";
	private static final Map<Location, Double> hopperLagMap = new HashMap<>();

	public HopperLagCommand(@NonNull CommandEvent event) {
		super(event);
		super.PREFIX = PREFIX;
	}

	@Path("[amount]")
	@Description("View hoppers that are triggering InventoryMoveEvents repeatedly")
	void hopperLag(@Arg("1") int amount) {
		if (amount > 10)
			amount = 10;

		LinkedHashMap<Location, Double> sortedMap = new LinkedHashMap<>();

		if (hopperLagMap.isEmpty())
			error("There are currently no logs for Hopper Lag");

		hopperLagMap.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

		if (sortedMap.size() <= 0)
			error("There are currently no logs for Hopper Lag");

		for (int i = 1; i <= amount; i++) {
			if (sortedMap.keySet().size() < i)
				break;

			Location location = (Location) sortedMap.keySet().toArray()[i - 1];
			double value = (double) sortedMap.values().toArray()[i - 1];
			final String color = (value > 100000) ? "&c" : (value > 10000) ? "&6" : "&e";
			String message = StringUtils.getLocationString(location) + " &7- " + color + StringUtils.getCnf().format(value);
			StringUtils.sendJsonLocation("&f" + i + ". " + message, location, player());
		}

		hopperLagMap.clear();
	}

	@EventHandler
	public void onInvMove(InventoryMoveItemEvent event) {
		if (!event.getInitiator().getType().equals(InventoryType.HOPPER)) return;
		Location location = event.getInitiator().getLocation();
		hopperLagMap.put(location, hopperLagMap.computeIfAbsent(location, $ -> 0d) + 1);
	}
}
