package me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs;

import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.IslandType;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.send;
import static me.pugabyte.bncore.utils.StringUtils.camelCase;

public class Talkers {

	public static boolean startScript(Player player, int id) {
		Island island = IslandType.getFromLocation(player.getLocation());
		if (island == null)
			return false;

		TalkingNPC talker = island.getNPC(id);
		if (talker == null)
			return false;

		sendScript(player, talker);
		return true;
	}

	public interface TalkingNPC {
		String name();

		int getNpcId();

		List<String> getScript();

		default List<String> getScript(Player player) {
			return getScript();
		}

		static TalkingNPC[] values() {
			return null;
		}
	}

	private static void sendScript(Player player, TalkingNPC talker) {
		List<String> script = talker.getScript(player);
		AtomicReference<String> npcName = new AtomicReference<>("");

		AtomicInteger wait = new AtomicInteger(0);
		script.forEach(line -> {
			npcName.set(camelCase(talker.name().replaceAll("_", " ")));
			if (line.toLowerCase().matches("^wait \\d+$"))
				wait.getAndAdd(Integer.parseInt(line.toLowerCase().replace("wait ", "")));
			else {
				line = line.replaceAll("<player>", player.getName());
				if (line.contains("<self>")) {
					npcName.set("&lYOU&f");
					line = line.replaceAll("<self> ", "");
				}
				String message = npcName.get() + "> " + line;
				Tasks.wait(wait.get(), () -> {
					send(message, player);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 1F);
				});
			}
		});
	}
}
