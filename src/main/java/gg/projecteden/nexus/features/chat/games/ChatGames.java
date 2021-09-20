package gg.projecteden.nexus.features.chat.games;

import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.chat.events.PublicChatEvent;
import gg.projecteden.nexus.features.commands.MuteMenuCommand;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.mutemenu.MuteMenuService;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.setting.Setting;
import gg.projecteden.nexus.models.setting.SettingService;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.utils.TimeUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Permission("group.staff")
public class ChatGames extends CustomCommand implements Listener {

	public static final int REQUIRED_PLAYERS = 10;
	public static final String PREFIX = StringUtils.getPrefix("ChatGames");
	private static final SettingService SERVICE = new SettingService();
	private static final Setting SETTING = SERVICE.get("chatgames", "queued");

	private static Games currentGame;

	static {
		if (SETTING.getInt() > 0) {
			schedule();
		}
	}

	public ChatGames(CommandEvent event) {
		super(event);
	}

	@Path("queue <amount>")
	void run(int amount) {
		queue(amount, player());
	}

	public static boolean queue(int amount, Player player) {
		int players = 0;
		for (Player p : Bukkit.getOnlinePlayers())
			if (!AFK.get(p).isAfk() && !Nerd.of(p).isVanished())
				players++;
		if (players < REQUIRED_PLAYERS)
			return false;

		if (SETTING.getInt() == 0) {
			Chat.Broadcast.all().prefix("ChatGames")
				.message("&e" + Nickname.of(player) + " &3has queued chat games! They will be played every few minutes. &eYou can queue more in the VPS.")
				.muteMenuItem(MuteMenuCommand.MuteMenuProvider.MuteMenuItem.CHAT_GAMES)
				.send();
		}
		else {
			player.sendMessage(ChatGames.PREFIX + "You queued " + amount + " chat games. They will be played every few minutes");
		}
		SETTING.setInt(SETTING.getInt() + amount);
		SERVICE.save(SETTING);
		schedule();
		return true;
	}

	private static void schedule() {
		Tasks.wait(TimeUtils.TickTime.MINUTE.x(RandomUtils.randomInt(0, 9)) + TimeUtils.TickTime.SECOND.x(RandomUtils.randomInt(30, 59)), () -> {
			start();
			SETTING.setInt(SETTING.getInt() - 1);
			if (SETTING.getInt() > 0) {
				schedule();
			}
		});
	}

	private static void start() {
		currentGame = RandomUtils.randomElement(Games.values());
		Chat.Broadcast.all().prefix("ChatGames").message(currentGame.create()).muteMenuItem(MuteMenuCommand.MuteMenuProvider.MuteMenuItem.CHAT_GAMES).send();
		Bukkit.getOnlinePlayers().forEach(p -> {
			if (!MuteMenuUser.hasMuted(p, MuteMenuCommand.MuteMenuProvider.MuteMenuItem.CHAT_GAMES)) {
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL,
					SoundUtils.getMuteMenuVolume(p, MuteMenuCommand.MuteMenuProvider.MuteMenuItem.CHAT_GAMES_SOUND),
					SoundUtils.getPitch(1));
			}
		});
		Tasks.wait(TimeUtils.TickTime.SECOND.x(currentGame.getTimeInSeconds()), () -> stop());
	}

	private static void stop() {
		JsonBuilder message = new JsonBuilder("&3The current game has ended! The correct answer was &e" + currentGame.getAnswer() + "&3. ");
		if (!currentGame.answeredPlayers.isEmpty()) {
			message.next("&e" + Nickname.of(currentGame.answeredPlayers.get(0)) + " &3was the " + (currentGame.answeredPlayers.size() == 1 ? "only" : "first") +" to answer correctly!");
			if (currentGame.answeredPlayers.size() > 1) {
				message.next(" &eHover for rankings");
				List<String> rankings = new ArrayList<>() {{
					add("§eRankings:");
					for (int i = 1; i <= currentGame.answeredPlayers.size(); i++) {
						add("&3" + i + ": &e" + Nickname.of(currentGame.answeredPlayers.get(i - 1)));
					}
				}};
				message.hover(rankings);
			}
		}
		Chat.Broadcast.all().prefix("ChatGames").message(message)
			.muteMenuItem(MuteMenuCommand.MuteMenuProvider.MuteMenuItem.CHAT_GAMES).send();
		Bukkit.getOnlinePlayers().forEach(p -> {
			if (!MuteMenuUser.hasMuted(p, MuteMenuCommand.MuteMenuProvider.MuteMenuItem.CHAT_GAMES)) {
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL,
					SoundUtils.getMuteMenuVolume(p, MuteMenuCommand.MuteMenuProvider.MuteMenuItem.CHAT_GAMES_SOUND),
					SoundUtils.getPitch(1));
			}
		});
		currentGame.reset();
		currentGame = null;
	}

	@EventHandler
	public void onChat(PublicChatEvent event) {
		if (event.getChatter().getPlayer() == null)
			return;
		if (currentGame == null || currentGame.getAnswer() == null)
			return;
		if (event.getOriginalMessage().equalsIgnoreCase(currentGame.getAnswer())) {
			currentGame.onAnswer(event.getChatter().getPlayer());
			event.setCancelled(true);
		}
	}

}
