package gg.projecteden.nexus.features.chat.games;

import gg.projecteden.nexus.features.chat.events.ChatEvent;
import gg.projecteden.nexus.features.vanish.events.VanishStateChangedEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
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
public class ChatGamesCommand extends CustomCommand implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("ChatGames");
	private static final ChatGamesConfigService service = new ChatGamesConfigService();
	private static final ChatGamesConfig config = service.get0();

	static {
		ChatGamesConfig.processQueue();
	}

	public ChatGamesCommand(CommandEvent event) {
		super(event);
	}

	@Path("queue add [amount]")
	@Permission(Group.MODERATOR)
	@Description("Queue a chat game")
	void add(@Arg("1") int amount) {
		ChatGamesConfig.queue(amount, player());
	}

	@Path("queue count")
	@Description("View the number of queued games")
	void count() {
		send(PREFIX + "There are " + config.getQueuedGames() + " queued games");
	}

	@Path("queue process [--force]")
	@Permission(Group.ADMIN)
	@Description("Process the queue")
	void process(@Switch boolean force) {
		ChatGamesConfig.processQueue(force);
		send(PREFIX + "Queue" + (force ? " force" : "") + " processed");
	}

	@Confirm
	@Path("queue clear")
	@Permission(Group.ADMIN)
	@Description("Clear the queue")
	void clear() {
		config.setQueuedGames(0);
		ChatGamesConfig.getCurrentGame().cancel();
		send(PREFIX + "Cleared queue");
	}

	@Path("start <type>")
	@Permission(Group.ADMIN)
	@Description("Start a chat game")
	void start(ChatGameType type) {
		if (ChatGamesConfig.getCurrentGame() != null)
			error("There is already an active game");

		type.create().start();
		send(PREFIX + "Game started");
	}

	@EventHandler
	public void onChat(ChatEvent event) {
		final ChatGame game = ChatGamesConfig.getCurrentGame();
		if (game == null || game.getAnswer() == null)
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
	public void on(VanishStateChangedEvent event) {
		ChatGamesConfig.processQueue();
	}

}
