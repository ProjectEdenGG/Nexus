package me.pugabyte.nexus.features.events.models;

import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Talker {
	public static void sendScript(Player player, TalkingNPC talker) {
		sendScript(player, talker, talker.getScript(player));
	}

	public static void sendScript(Player player, TalkingNPC talker, List<String> script) {
		if (script == null || script.isEmpty())
			return;

		AtomicReference<String> npcName = new AtomicReference<>(talker.getName());
		AtomicInteger wait = new AtomicInteger(0);
		script.forEach(line -> {
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
					PlayerUtils.send(player, message);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 1F);
				});
			}
		});
	}

	public interface TalkingNPC {
		String getName();

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
