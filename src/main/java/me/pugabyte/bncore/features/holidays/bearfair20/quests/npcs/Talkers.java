package me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs;

import lombok.Getter;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.MinigameNight;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.send;
import static me.pugabyte.bncore.features.holidays.bearfair20.islands.SummerDownUnder.*;


public class Talkers {

	public static boolean startScript(Player player, int id) {
		BFTalker bfTalker = BFTalker.getFromId(id);
		if (bfTalker == null)
			return false;
		sendScript(player, bfTalker);
		return true;
	}

	private static void sendScript(Player player, BFTalker talker) {
		List<String> script = talker.getScript(player);
		String npcName = talker.npcName;

		AtomicInteger wait = new AtomicInteger(0);
		script.forEach(line -> {
			if (line.toLowerCase().matches("^wait \\d+$"))
				wait.getAndAdd(Integer.parseInt(line.toLowerCase().replace("wait ", "")));
			else {
				line = line.replaceAll("<player>", player.getName());
				String message = npcName + "> " + line;
				Tasks.wait(wait.get(), () -> {
					send(message, player);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 1F);
				});
			}
		});
	}

	public enum BFTalker {
		// Minigame Island
		MGN_AXEL(2755, "Axel") {
			@Override
			public List<String> getScript(Player player) {
				int ran = Utils.randomInt(1, 3);
				switch (ran) {
					case 1:
						return MinigameNight.SCRIPT_AXEL_BEFORE;
					case 2:
						return MinigameNight.SCRIPT_AXEL_DURING;
					default:
						return MinigameNight.SCRIPT_AXEL_AFTER;
				}
			}
		},

		// Summer Down Under
		// Quest NPCs

		// Clutter NPCs
		SDU_DYLAN(2915, "Dylan") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_DYLAN;
			}
		},
		SDU_MATT(2916, "Matt") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_MATT;
			}
		},
		SDU_MAX(2917, "Max") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_MAX;
			}
		},
		SDU_TALITHA(2933, "Talitha") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_TALITHA;
			}
		},
		SDU_DECLAN(2922, "Declan") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_DECLAN;
			}
		},
		SDU_CAMERON(2921, "Cameron") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_CAMERON;
			}
		},
		SDU_JOSH(2918, "Josh") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_JOSH;
			}
		},
		SDU_NIKKI(2944, "Nikki") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_NIKKI;
			}
		},
		SDU_NICOLE(2945, "Nicole") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_NICOLE;
			}
		},
		SDU_GRIFFIN(2931, "Griffin") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_GRIFFIN;
			}
		},
		SDU_TRINITY(2932, "Trinity") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_TRINITY;
			}
		},
		SDU_RYAN(2923, "Ryan") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_RYAN;
			}
		},
		SDU_FOREMAN(2927, "Foreman") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_FOREMAN;
			}
		},
		SDU_DRIVER(2930, "Driver") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_DRIVER;
			}
		},
		SDU_TALISHA(2939, "Talisha") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_TALISHA;
			}
		},
		SDU_TAYLOR(2940, "Taylor") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_TAYLOR;
			}
		},
		SDU_LUCY(2941, "Lucy") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_LUCY;
			}
		},
		SDU_CHRIS(2942, "Chris") {
			@Override
			public List<String> getScript(Player player) {
				return SCRIPT_CHRIS;
			}
		};

		@Getter
		private final int npcId;

		@Getter
		private final String npcName;

		BFTalker(int npcId, String npcName) {
			this.npcId = npcId;
			this.npcName = npcName;
		}

		public static BFTalker getFromId(int id) {
			for (BFTalker talker : values()) {
				if (talker.getNpcId() == id)
					return talker;
			}

			return null;
		}

		public abstract List<String> getScript(Player player);
	}
}
