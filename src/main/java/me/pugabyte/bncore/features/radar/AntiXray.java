package me.pugabyte.bncore.features.radar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.chat.Chat.StaticChannel;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.WorldGroup;
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
	private final static Map<Material, Integer> countMap = new HashMap<Material, Integer>() {{
		// TODO: 1.16 Netherrite
		put(Material.DIAMOND_ORE, 20);
		put(Material.EMERALD_ORE, 5);
		put(Material.IRON_ORE, 50);
		put(Material.GOLD_ORE, 20);
		// TODO: 1.16 Nether gold
		// TODO: 1.16 Gilded Blackstone
	}};
	private static Map<UUID, List<XRayLog>> XRayLogs = new HashMap<>();
	private final static int minuteThreshold = 5;

	@EventHandler
	public void onBreakOre(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (WorldGroup.get(player) != WorldGroup.SURVIVAL) return;
		if (!player.getGameMode().equals(GameMode.SURVIVAL)) return;
		Nerd nerd = new Nerd(player);
		if (nerd.getRank().isStaff()) return;

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
				Warp warp = new Warp(player.getName(), player.getLocation(), WarpType.XRAY.name());
				new WarpService().save(warp);
				logs.clear();

				String name = player.getName();
				Chat.broadcastIngame(new JsonBuilder
						("&7&l[&cRadar&7&l] &a" + name + "&f is possibly xraying. ")
						.next("&e[Click to Teleport]")
						.command("mcmd vanish on ;; xraywarp " + name)
						.hover("This will automatically vanish you"), StaticChannel.STAFF);
				Chat.broadcastDiscord("**[Radar]** " + name + " is possibly xraying. `/xraywarp " + name + "`", StaticChannel.STAFF);

				break;
			}
		}
	}

	@Getter
	@AllArgsConstructor
	class XRayLog {
		Material material;
		LocalDateTime timestamp;
		Location location;
	}


}
