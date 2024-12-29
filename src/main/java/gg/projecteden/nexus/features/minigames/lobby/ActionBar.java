package gg.projecteden.nexus.features.minigames.lobby;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.commands.MinigamesCommand.MinigamePodiumPosition;
import gg.projecteden.nexus.features.minigames.utils.MinigameNight;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionBar {
	private static final long DELAY = TickTime.SECOND.x(5);

	private List<String> messages = new ArrayList<>() {{
		add("&3You are currently in the &eMinigame Lobby&3!");
		add("&3Click a gamemode to join a game");
		add("&3Join us at &e{local_mgn_time} &3on &e{local_mgn_day} &3for &eMinigame Night!");
		add("&3Make sure to join &c/discord &3on &eMinigame Night");
		add("&3Use &c/discord link &3to connect your Discord and MC accounts");
//		add("&3Use &c/discord mgnr &3to set up Minigame Night &ereminders");
		add("&3Please use the &eMinigame Channel &3with &c/ch m");
		add("&bPodium Players of the Week - {podiums_left}");
		add("&bPodium Players of the Week - {podiums_middle}");
		add("&bPodium Players of the Week - {podiums_right}");
	}};

	public ActionBar() {
		Tasks.repeat(5, messages.size() * DELAY, () -> {
			AtomicLong wait = new AtomicLong(0);
			messages.iterator().forEachRemaining(message ->
					Tasks.wait(wait.getAndAdd(DELAY), () ->
							Minigames.getWorld().getPlayers().forEach(player -> {
								if (Minigames.isInMinigameLobby(player) && !CitizensUtils.isNPC(player))
									ActionBarUtils.sendActionBar(player, interpolate(message, player), DELAY);
							})));
		});
	}

	public String interpolate(String message, Player player) {
		Matcher matcher = Pattern.compile("\\{podiums_.*}").matcher(message);
		while (matcher.find()) {
			String group = matcher.group();
			String position = group.substring("{podiums_".length(), group.length() - 1);
			NPC npc = MinigamePodiumPosition.get(position).getNPC();
			if (npc != null) {
				final List<String> hologramLines = npc.getOrAddTrait(HologramTrait.class).getLines();
				message = message.replace(group, hologramLines.get(1) + " &7- " + Nerd.of(hologramLines.get(0)).getColoredName());
			}
		}

		MinigameNight mgn = new MinigameNight(player);
		message = message.replace("{local_mgn_time}", mgn.getTimeFormatted());
		message = message.replace("{local_mgn_day}", StringUtils.camelCase(mgn.getNext().getDayOfWeek().name()));
		return message;
	}

}
