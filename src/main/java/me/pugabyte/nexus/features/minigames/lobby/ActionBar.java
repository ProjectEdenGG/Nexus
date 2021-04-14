package me.pugabyte.nexus.features.minigames.lobby;

import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.commands.PodiumsCommand;
import me.pugabyte.nexus.features.minigames.commands.PodiumsCommand.Position;
import me.pugabyte.nexus.features.minigames.utils.MinigameNight.NextMGN;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.pugabyte.nexus.utils.CitizensUtils.isNPC;
import static me.pugabyte.nexus.utils.StringUtils.camelCase;

public class ActionBar {
	private static final int DELAY = Time.SECOND.x(5);

	private List<String> messages = new ArrayList<String>() {{
		add("&3You are currently in the &eMinigame Lobby&3!");
		add("&3Right click a sign to join a game.");
		add("&3Join us at &e{local_mgn_time} &3on &e{local_mgn_day} &3for &eMinigame Night!");
		add("&3Make sure to join &c/discord &3on &eMinigame Night.");
		add("&3Use &c/discord link &3to connect your Discord and MC accounts");
//		add("&3Use &c/discord mgnr &3to set up Minigame Night &ereminders");
		add("&3Please use the &eMinigame Channel &3with &c/ch m");
		add("&bPodium Players of the Week - {podiums_left}");
		add("&bPodium Players of the Week - {podiums_middle}");
		add("&bPodium Players of the Week - {podiums_right}");
	}};

	public ActionBar() {
		Tasks.repeat(5, messages.size() * DELAY, () -> {
			AtomicInteger wait = new AtomicInteger(0);
			messages.iterator().forEachRemaining(message ->
					Tasks.wait(wait.getAndAdd(DELAY), () ->
							Minigames.getWorld().getPlayers().forEach(player -> {
								if (isInRegion(player) && !isNPC(player))
									ActionBarUtils.sendActionBar(player, interpolate(message, player), DELAY);
							})));
		});
	}

	public String interpolate(String message, Player player) {
		Matcher matcher = Pattern.compile("\\{podiums_.*}").matcher(message);
		while (matcher.find()) {
			String group = matcher.group();
			String position = group.substring("{podiums_".length(), group.length() - 1);
			NPC npc = PodiumsCommand.getNpc(Position.get(position));
			if (npc != null)
				message = message.replace(group, ChatColor.DARK_AQUA + npc.getName());
		}

		NextMGN mgn = new NextMGN(player);
		message = message.replace("{local_mgn_time}", mgn.getTimeFormatted());
		message = message.replace("{local_mgn_day}", camelCase(mgn.getNext().getDayOfWeek().name()));
		return message;
	}

	public boolean isInRegion(Player player) {
		return Minigames.getWorldGuardUtils().isInRegion(player.getLocation(), Minigames.getLobbyRegion());
	}

}
