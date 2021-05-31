package me.pugabyte.nexus.features.events.y2021.pride21;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.LocalDate;

public class Pride21 implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("Pride");
	public static final String REGION = "pride21";
	private static final int SHOP_NPC_ID = 3862;

	public static boolean QUESTS_ENABLED() {
		return LocalDate.now().isBefore(LocalDate.of(2021, 7, 1));
	}

	public Pride21() {
		new Quests();
		Nexus.registerListener(this);
	}

	public static WorldGuardUtils getWGUtils() {
		return new WorldGuardUtils("events");
	}

	public static boolean isInRegion(Location location) {
		return getWGUtils().isInRegion(location, REGION);
	}

	public static boolean isInRegion(Player player) {
		return isInRegion(player.getLocation());
	}

	@EventHandler
	public void onRightClickNPC(NPCRightClickEvent event) {
		if (event.getNPC().getId() != SHOP_NPC_ID) return;
		new BuyFlagsMenu().open(event.getClicker());
	}
}
