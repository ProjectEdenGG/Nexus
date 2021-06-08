package me.pugabyte.nexus.features.events.models;

import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class Talker {
	/**
	 * Sends a script to a player from a talking NPC. Uses the NPC's default script(s).
	 * @param player player to send to
	 * @param talker NPC to send from
	 * @return ticks until the script ends
	 */
	public static int sendScript(Player player, TalkingNPC talker) {
		return sendScript(player, talker, talker.getScript(player));
	}

	/**
	 * Sends a script to a player from a talking NPC.
	 * @param player player to send to
	 * @param talker NPC to send from
	 * @param script script to send
	 * @return ticks until the script ends
	 */
	public static int sendScript(Player player, TalkingNPC talker, List<String> script) {
		if (script == null || script.isEmpty())
			return 0;
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
		return wait.get();
	}

	/**
	 * Sends a script to a player from a talking NPC.
	 *
	 * @param player player to send to
	 * @param talker NPC to send from
	 * @param script script to send
	 * @return CompletableFuture
	 */
	public static CompletableFuture<Boolean> runScript(Player player, TalkingNPC talker, List<String> script) {
		CompletableFuture<Boolean> future = new CompletableFuture<>();
		if (script == null || script.isEmpty()) {
			future.complete(true);
			return future;
		}

		final String playerName = Nickname.of(player);
		AtomicInteger wait = new AtomicInteger(0);

		Iterator<String> iterator = script.iterator();
		while (iterator.hasNext()) {
			String line = iterator.next();
			if (line.toLowerCase().contains("<exit>")) {
				Tasks.wait(wait.get(), () -> future.complete(false));
			} else if (line.toLowerCase().matches("^wait \\d+$")) {
				wait.getAndAdd(Integer.parseInt(line.toLowerCase().replace("wait ", "")));
				if (!iterator.hasNext())
					Tasks.wait(wait.get(), () -> future.complete(true));
			} else {
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

				if (!iterator.hasNext())
					Tasks.wait(wait.get(), () -> future.complete(true));
			}
		}

		return future;
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
