package gg.projecteden.nexus.features.events.models;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.events.y2020.bearfair20.BearFair20;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.features.events.y2021.bearfair21.islands.BearFair21IslandType;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public class BearFairTalker extends Talker {

	public static TalkingNPC getTalkingNPC(Player player, int id) {
		BearFairIsland island = getIslandType(player);
		if (island == null)
			return null;

		return island.getNPC(id);
	}

	public static void startScript(Player player, int id) {
		TalkingNPC talker = getTalkingNPC(player, id);
		if (talker == null)
			return;

		sendScript(player, talker);
	}

	private static BearFairIsland getIslandType(Player player) {
		WorldGuardUtils worldguard = new WorldGuardUtils(player);
		Location location = player.getLocation();
		Set<ProtectedRegion> regions = worldguard.getRegionsLikeAt("bearfair.*", location);
		for (ProtectedRegion region : regions) {
			if (region.getId().equals(BearFair20.getRegion()))
				return gg.projecteden.nexus.features.events.y2020.bearfair20.islands.IslandType.getFromLocation(location);
			if (region.getId().equals(BearFair21.getRegion()))
				return BearFair21IslandType.get(location);
		}
		return null;
	}
}
