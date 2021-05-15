package me.pugabyte.nexus.features.events.y2021.bearfair21.quests;

import me.pugabyte.nexus.features.events.models.BearFairTalker;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import org.bukkit.entity.Player;

public class BearFair21Talker extends BearFairTalker {
	public static void startScript(BearFair21User user, int id) {
		Player player = user.getPlayer();
		if (player == null)
			return;

		TalkingNPC talker = getTalkingNPC(player, id);
		if (talker == null)
			return;

		sendScript(player, talker);
	}
}
