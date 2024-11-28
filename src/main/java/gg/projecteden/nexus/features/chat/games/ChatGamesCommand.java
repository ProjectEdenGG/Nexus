package gg.projecteden.nexus.features.chat.games;

import gg.projecteden.nexus.features.chat.events.ChatEvent;
import gg.projecteden.nexus.features.vanish.events.VanishToggleEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.afk.events.NotAFKEvent;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfig;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfig.ChatGame;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfigService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@NoArgsConstructor
@Permission(Group.SENIOR_STAFF)
public class ChatGamesCommand extends CustomCommand implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("ChatGames");
	private static final ChatGamesConfigService service = new ChatGamesConfigService();

	static {
		ChatGamesConfig.processQueue();
	}

	public ChatGamesCommand(CommandEvent event) {
		super(event);
	}

	@HideFromWiki
	@Path("ignorePlayers [enable]")
	@Permission(Group.ADMIN)
	void ignorePlayers(Boolean enable) {
		if (enable == null)
			enable = !ChatGamesConfig.isIgnorePlayers();

		ChatGamesConfig.setIgnorePlayers(enable);
		send(PREFIX + "Ignoring players " + (ChatGamesConfig.isIgnorePlayers() ? "&aenabled" : "&cdisabled"));
	}

	@HideFromWiki
	@Path("getAnswer")
	@Permission(Group.ADMIN)
	void getAnswer() {
		ChatGame chatGame = ChatGamesConfig.getCurrentGame();
		if (chatGame == null)
			error("There isn't an active game");

		send("Answer: " + chatGame.getAnswer());
	}

	@Path("enable")
	@Description("Enables chat game")
	void add() {
		service.edit0(user -> user.setEnabled(true));
		send(PREFIX + "&aEnabled");
	}

	@Confirm
	@Path("disable")
	@Description("Clear the queue")
	void clear() {
		service.edit0(user -> user.setEnabled(false));
		send(PREFIX + "&cDisabled");
	}

	@Path("start <type>")
	@Description("Start a chat game")
	void start(ChatGameType type) {
		if (ChatGamesConfig.getCurrentGame() != null && ChatGamesConfig.getCurrentGame().isStarted())
			error("There is already an active game");

		type.create().start();
		send(PREFIX + "Game started");
	}

	@EventHandler
	public void onChat(ChatEvent event) {
		final ChatGame game = ChatGamesConfig.getCurrentGame();
		if (game == null || game.getAnswer() == null || !game.isStarted())
			return;

		if (event.getOriginalMessage().equalsIgnoreCase(game.getAnswer())) {
			if (event.getChatter() != null)
				game.onAnswer(Nerd.of(event.getChatter()));

			event.setCancelled(true);
		}
	}

	@EventHandler
	public void on(NotAFKEvent event) {
		ChatGamesConfig.processQueue();
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		ChatGamesConfig.processQueue();
	}

	@EventHandler
	public void on(VanishToggleEvent event) {
		ChatGamesConfig.processQueue();
	}

}
