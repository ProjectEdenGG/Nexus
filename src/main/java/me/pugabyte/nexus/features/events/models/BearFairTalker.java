package me.pugabyte.nexus.features.events.models;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.features.events.models.Talker.TalkingNPC;
import me.pugabyte.nexus.features.events.y2020.bearfair20.BearFair20;
import me.pugabyte.nexus.features.events.y2020.bearfair20.islands.MainIsland.MainNPCs;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class BearFairTalker {

	public static void startScript(Player player, int id) {
		BearFairIsland island = getIslandType(player);
		if (island == null)
			return;

		TalkingNPC talker = island.getNPC(id);
		if (talker == null)
			return;

		Talker.sendScript(player, talker);
	}

	public static void sendScript(Player player, MainNPCs npc, List<String> script) {
		Talker.sendScript(player, npc, script);
	}


	private static BearFairIsland getIslandType(Player player) {
		WorldGuardUtils WGUtils = new WorldGuardUtils(player);
		Set<ProtectedRegion> regions = WGUtils.getRegionsLikeAt("bearfair.*", player.getLocation());
		for (ProtectedRegion region : regions) {
			if (region.getId().equals(BearFair20.getRegion()))
				return me.pugabyte.nexus.features.events.y2020.bearfair20.islands.IslandType.getFromLocation(player.getLocation());
			if (region.getId().equals(BearFair21.getRegion()))
				return me.pugabyte.nexus.features.events.y2021.bearfair21.islands.IslandType.getFromLocation(player.getLocation());
		}
		return null;
	}
}
