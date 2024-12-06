package gg.projecteden.nexus.features.chat.games;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.features.chat.events.ChatEvent;
import gg.projecteden.nexus.features.vanish.events.VanishToggleEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.afk.events.NotAFKEvent;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfig;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfig.ChatGame;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfigService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@NoArgsConstructor
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
	@Permission(Group.SENIOR_STAFF)
	void add() {
		service.edit0(user -> user.setEnabled(true));
		send(PREFIX + "&aEnabled");
	}

	@Confirm
	@Path("disable")
	@Description("Clear the queue")
	@Permission(Group.SENIOR_STAFF)
	void clear() {
		service.edit0(user -> user.setEnabled(false));
		send(PREFIX + "&cDisabled");
	}

	@Path("rewards [enabled]")
	@Description("Toggles if chat games give rewards to players")
	@Permission(Group.ADMIN)
	void toggleRewards(@Switch @Arg("false") boolean enabled) {
		service.edit0(user -> user.setRewardsEnabled(enabled));
		send(PREFIX + "Rewards " + (enabled ? "&aenabled" : "&cdisabled"));
	}

	@Path("start [type]")
	@Description("Start a chat game")
	void start(@Arg(value = "random", permission = Group.SENIOR_STAFF) ChatGameType type, @Switch @Arg(value = "false", permission = Group.SENIOR_STAFF) boolean now) {
		if (ChatGamesConfig.getCurrentGame() != null && ChatGamesConfig.getCurrentGame().isStarted())
			error("There is already an active game");

		if (type == ChatGameType.RANDOM)
			type = ChatGameType.random();

		type.create().queue(now ? 0.01 : RandomUtils.randomDouble(1, 3));
		send(PREFIX + "Chat games have been " + (now ? "started" : "queued. They will start soon!"));
	}

	@EventHandler
	public void onChat(ChatEvent event) {
		if (event.getChatter() == null)
			return;

		Nerd nerd = Nerd.of(event.getChatter());

		final ChatGame game = ChatGamesConfig.getCurrentGame();
		if (game == null || game.getAnswer() == null || !game.isStarted())
			return;

		String message = event.getOriginalMessage();
		final boolean correct = message.equalsIgnoreCase(game.getAnswer());

		if (correct) {
			event.setCancelled(true);
			game.onAnswer(nerd);
			return;
		}

		if (game.isAnswerSimilar(message)) {
			event.setCancelled(true);
			PlayerUtils.send(event.getChatter(), PREFIX + "&3Your guess &e" + message + " &3is &aclose&3!");
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
