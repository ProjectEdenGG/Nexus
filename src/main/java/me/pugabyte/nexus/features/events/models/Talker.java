package me.pugabyte.nexus.features.events.models;

import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static eden.utils.StringUtils.camelCase;

public class Talker {
	public static void sendScript(Player player, TalkingNPC talker) {
		sendScript(player, talker, talker.getScript());
	}

	public static void sendScript(Player player, TalkingNPC talker, List<String> script) {
		if (script == null) return;
		AtomicReference<String> npcName = new AtomicReference<>("");

		AtomicInteger wait = new AtomicInteger(0);
		script.forEach(line -> {
			npcName.set(camelCase(talker.name().replaceAll("_", " ")));
			npcName.set(npcName.get().replaceAll("[0-9]+", ""));
			if (line.toLowerCase().matches("^wait \\d+$"))
				wait.getAndAdd(Integer.parseInt(line.toLowerCase().replace("wait ", "")));
			else {
				line = line.replaceAll("<player>", player.getName());
				if (line.contains("<self>")) {
					npcName.set("&b&lYOU&f");
					line = line.replaceAll("<self> ", "");
				}
				String message = "&3" + npcName.get() + " &7> &f" + line;
				Tasks.wait(wait.get(), () -> {
					PlayerUtils.send(message, player);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 1F);
				});
			}
		});
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
}
