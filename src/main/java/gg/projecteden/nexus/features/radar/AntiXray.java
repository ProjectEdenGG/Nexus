package gg.projecteden.nexus.features.radar;

import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.models.warps.Warps.Warp;
import gg.projecteden.nexus.models.warps.WarpsService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//TODO: Prevent false positives with silktouch/fortune somehow
public class AntiXray implements Listener {
	private final static int minuteThreshold = 5;
	private static final Map<UUID, List<XRayLog>> XRayLogs = new HashMap<>();
	private final static Map<Material, Integer> countMap = new HashMap<>() {{
		// @formatter:off
		//										// Amount per chunk is not guaranteed, but is max
		put(Material.ANCIENT_DEBRIS,	3);		// vein of 1-3, 	2 per chunk
		put(Material.DIAMOND_ORE,		20);	// vein of 1-10, 	1 per chunk
		put(Material.EMERALD_ORE,		5);		// vein of 1, 		11 per chunk
		put(Material.IRON_ORE,			50);	// vein of 1-14,	20 per chunk
		put(Material.GOLD_ORE,			20);	// vein of 1-9, 	2 per chunk
		put(Material.NETHER_GOLD_ORE,	30);	// vein of 1-10, 	10 per chunk
		put(Material.GILDED_BLACKSTONE,	20);	// generates in bastian remnants
		// @formatter:on;
	}};

	@EventHandler
	public void onBreakOre(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (WorldGroup.of(player) != WorldGroup.SURVIVAL) return;
		if (!player.getGameMode().equals(GameMode.SURVIVAL)) return;
		if (Rank.of(player).isStaff()) return;

		Material ore = event.getBlock().getType();
		if (!countMap.containsKey(ore)) return;

		if (!ore.equals(Material.GOLD_ORE))
			if (event.getBlock().getY() > 62) return;

		LocalDateTime now = LocalDateTime.now();
		Location loc = player.getLocation();
		XRayLog log = new XRayLog(ore, now, loc);

		UUID uuid = player.getUniqueId();
		XRayLogs.putIfAbsent(uuid, new ArrayList<>());
		XRayLogs.get(uuid).add(log);

		checkLogs(player, ore);
	}

	private void checkLogs(Player player, Material ore) {
		UUID uuid = player.getUniqueId();
		List<XRayLog> logs = XRayLogs.get(uuid);
		LocalDateTime now = LocalDateTime.now();
		int count = 0;
		for (XRayLog log : new ArrayList<>(logs)) {
			if (log.getTimestamp().isAfter(now.minusMinutes(minuteThreshold)))
				++count;
			if (count > countMap.get(ore)) {
				new WarpsService().edit0(warps -> warps.add(new Warp(player.getName(), WarpType.XRAY, player.getLocation())));
				logs.clear();

				String name = player.getName();
				final JsonBuilder message = new JsonBuilder
						("&7&l[&cRadar&7&l] &a" + name + "&f is possibly xraying. ")
						.next("&e[Click to Teleport]")
						.command("mcmd vanish on ;; xraywarp " + name)
						.hover("This will automatically vanish you");

				Broadcast.staffIngame().message(message).send();
				Broadcast.staffDiscord().prefix("Radar").message(name + " is possibly xraying. `/xraywarp " + name + "`").send();

				break;
			}
		}
	}

	@Getter
	@AllArgsConstructor
	private static class XRayLog {
		private final Material material;
		private final LocalDateTime timestamp;
		private final Location location;
	}


}
