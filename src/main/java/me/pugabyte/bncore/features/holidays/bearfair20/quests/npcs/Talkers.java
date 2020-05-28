package me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs;

import lombok.Getter;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class Talkers {

	public static boolean startScript(Player player, int id) {
		BFTalker bfTalker = BFTalker.getFromId(id);
		if (bfTalker == null)
			return false;
		sendScript(player, bfTalker.getScript());
		return true;
	}

	private enum BFTalker {
		AXEL(2755) {
			@Override
			List<String> getScript() {
				return new ArrayList<String>() {{
					add("This is some text, please wait 4...");
					add("wait 80");
					add("this");
					add("wait 10");
					add("is");
					add("wait 10");
					add("some");
					add("wait 10");
					add("text");
					add("wait 40");
					add("Nerd");
				}};
			}
		};

		@Getter
		private final int npcId;

		BFTalker(int npcId) {
			this.npcId = npcId;
		}

		public static BFTalker getFromId(int id) {
			for (BFTalker talker : values()) {
				if (talker.getNpcId() == id)
					return talker;
			}

			return null;
		}

		abstract List<String> getScript();
	}

	private static void sendScript(Player player, List<String> script) {
		AtomicInteger wait = new AtomicInteger(0);
		script.forEach(line -> {
			if (line.toLowerCase().matches("^wait \\d+$"))
				wait.getAndAdd(Integer.parseInt(line.toLowerCase().replace("wait ", "")));
			else {
				line = line.replaceAll("<player>", player.getName());
				String finalLine = line;
				Tasks.wait(wait.get(), () -> {
					player.sendMessage(colorize(finalLine));
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 1F);
				});
			}
		});
	}
}
