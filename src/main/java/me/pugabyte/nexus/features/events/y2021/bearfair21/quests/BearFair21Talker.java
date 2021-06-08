package me.pugabyte.nexus.features.events.y2021.bearfair21.quests;

import me.pugabyte.nexus.features.events.models.BearFairTalker;
import me.pugabyte.nexus.features.events.models.Talker;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class BearFair21Talker extends BearFairTalker {
	public static int sendScript(BearFair21User user, int id) {
		Player player = user.getPlayer();
		if (player == null)
			return 0;

		TalkingNPC talker = getTalkingNPC(player, id);
		if (talker == null)
			return 0;

		return sendScript(player, talker);
	}

	public static CompletableFuture<Boolean> runScript(BearFair21User user, int id) {
		CompletableFuture<Boolean> future = new CompletableFuture<>();
		Player player = user.getPlayer();
		if (player == null) {
			future.complete(true);
			return future;
		}

		TalkingNPC talker = getTalkingNPC(player, id);
		if (talker == null) {
			future.complete(true);
			return future;
		}

		return Talker.runScript(player, talker, talker.getScript(player));
	}
}
