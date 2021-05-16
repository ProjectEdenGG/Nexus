package me.pugabyte.nexus.features.events.models;

import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Talker {
	public static void sendScript(Player player, TalkingNPC talker) {
		sendScript(player, talker, talker.getScript(player));
	}

	public static void sendScript(Player player, TalkingNPC talker, List<String> script) {
		if (script == null || script.isEmpty())
			return;
		final String playerName = Nickname.of(player);

		AtomicInteger wait = new AtomicInteger(0);
		script.forEach(line -> {
			if (line.toLowerCase().matches("^wait \\d+$"))
				wait.getAndAdd(Integer.parseInt(line.toLowerCase().replace("wait ", "")));
			else {
				line = line.replaceAll("<player>", playerName);
				final String npcName;
				if (line.contains("<self> ")) {
					npcName = "&b&lYOU&f";
					line = line.replaceAll("<self> ", "");
				} else
					npcName = talker.getName();
				String message = "&3" + npcName + " &7> &f" + line;
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
