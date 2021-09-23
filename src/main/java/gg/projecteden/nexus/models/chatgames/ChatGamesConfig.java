package gg.projecteden.nexus.models.chatgames;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.chat.games.ChatGameType;
import gg.projecteden.nexus.features.chat.games.ChatGamesCommand;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.EnumUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static gg.projecteden.nexus.features.discord.Discord.discordize;
import static gg.projecteden.nexus.utils.StringUtils.colorize;
import static gg.projecteden.utils.StringUtils.getDiscordPrefix;
import static gg.projecteden.utils.StringUtils.prettyMoney;

@Data
@Entity(value = "chat_games_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class ChatGamesConfig implements PlayerOwnedObject {
	public static final int REQUIRED_PLAYERS = 7;
	@Id
	@NonNull
	private UUID uuid;
	private int queuedGames;

	public void addQueuedGames(int amount) {
		queuedGames += amount;
	}

	public void removeQueuedGames(int amount) {
		queuedGames = Math.max(0, queuedGames - amount);
	}

	public boolean hasQueuedGames() {
		return queuedGames > 0;
	}

	@Getter
	@Setter
	private static ChatGame currentGame;

	public static boolean hasRequiredPlayers() {
		return OnlinePlayers.builder().afk(false).vanished(false).get().size() >= REQUIRED_PLAYERS;
	}

	public static void queue(int amount, Player player) {
		new ChatGamesConfigService().edit0(config -> {
			if (!hasRequiredPlayers())
				PlayerUtils.send(player, ChatGamesCommand.PREFIX + "The games will start when there are " + REQUIRED_PLAYERS + " active players");
			else
				if (!config.hasQueuedGames())
					Broadcast.all()
						.prefix("ChatGames")
						.message("&e" + Nickname.of(player) + " &3has queued chat games! They will be played every few minutes. &eYou can queue more in the VPS.")
						.muteMenuItem(MuteMenuItem.CHAT_GAMES)
						.send();
				else
					PlayerUtils.send(player, ChatGamesCommand.PREFIX + "You queued " + amount + " more chat games. They will be played every few minutes");

			config.addQueuedGames(amount);
			processQueue();
		});
	}

	public static void processQueue() {
		processQueue(false);
	}

	public static void processQueue(boolean force) {
		if (!force)
			if (!hasRequiredPlayers())
				return;

		if (getCurrentGame() != null)
			return;

		final ChatGamesConfigService service = new ChatGamesConfigService();
		final ChatGamesConfig config = service.get0();
		if (!config.hasQueuedGames())
			return;

		ChatGameType.random().create().queue();
		config.removeQueuedGames(1);
		service.save(config);
	}

	@Data
	@RequiredArgsConstructor
	public static class ChatGame {
		private final ChatGameType gameType;
		private final String answer;
		private final JsonBuilder broadcast;
		private final String discordBroadcast;
		private List<UUID> completed = new ArrayList<>();

		public ChatGame(ChatGameType gameType, String answer, JsonBuilder broadcast) {
			this(gameType, answer, broadcast, discordize(broadcast));
		}

		private boolean started;
		private int taskId;

		public void queue() {
			ChatGamesConfig.setCurrentGame(this);
			taskId = Tasks.wait(TickTime.MINUTE.x(RandomUtils.randomDouble(1, 10)), this::start);
		}

		public void cancel() {
			Tasks.cancel(taskId);
			if (started)
				Broadcast.all()
					.prefix("ChatGames")
					.message("Game cancelled")
					.muteMenuItem(MuteMenuItem.CHAT_GAMES)
					.send();

			ChatGamesConfig.setCurrentGame(null);
		}

		public void start() {
			Tasks.cancel(taskId);
			ChatGamesConfig.setCurrentGame(this);
			started = true;

			Broadcast.ingame()
				.prefix("ChatGames")
				.message(broadcast)
				.muteMenuItem(MuteMenuItem.CHAT_GAMES)
				.send();

			Broadcast.discord()
				.prefix("ChatGames")
				.message(discordBroadcast)
				.send();

			new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL)
				.everyone()
				.muteMenuItem(MuteMenuItem.CHAT_GAMES)
				.volume(MuteMenuItem.CHAT_GAMES_SOUND)
				.pitchStep(1)
				.play();

			taskId = Tasks.wait(TickTime.SECOND.x(gameType.getTimeInSeconds()), this::stop);
		}

		private void stop() {
			broadcastEndIngame();
			broadcastEndDiscord();

			new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL)
				.everyone()
				.muteMenuItem(MuteMenuItem.CHAT_GAMES)
				.volume(MuteMenuItem.CHAT_GAMES_SOUND)
				.pitchStep(1)
				.play();

			ChatGamesConfig.setCurrentGame(null);
			Tasks.wait(TickTime.SECOND, ChatGamesConfig::processQueue);
		}

		private void broadcastEndIngame() {
			JsonBuilder message = new JsonBuilder("&3Game over! The correct answer was &e" + answer + "&3. ");
			if (!completed.isEmpty()) {
				message.next("&e" + Nickname.of(completed.get(0)) + " &3was the " + (completed.size() == 1 ? "only" : "first") + " to answer correctly!");

				if (completed.size() > 1) {
					message.next(" &eHover for rankings");

					message.hover(new ArrayList<>() {{
						add("&eRankings:");
						for (int i = 1; i <= completed.size(); i++)
							add("&3" + i + ": &e" + Nickname.of(completed.get(i - 1)));
					}});
				}
			}

			Broadcast.ingame()
				.prefix("ChatGames")
				.message(message)
				.muteMenuItem(MuteMenuItem.CHAT_GAMES)
				.send();
		}

		private void broadcastEndDiscord() {
			final MessageBuilder message = new MessageBuilder(getDiscordPrefix("ChatGames") +
				"Game over! The correct answer was **" + answer + "**. ");

			if (completed.isEmpty())
				message.append("No one answered correctly");
			else {
				message.append(String.format("%s was the %s to answer correctly!", Nickname.discordOf(completed.get(0)), completed.size() == 1 ? "only" : "first"));

				if (completed.size() > 1) {
					message.setEmbeds(new EmbedBuilder() {{
						for (int i = 1; i <= completed.size(); i++)
							appendDescription(String.format("**%d**: %s%n", i, Nickname.discordOf(completed.get(i - 1))));
					}}.setTitle("Rankings").build());
				}
			}

			Discord.send(message);
		}

		public void onAnswer(Nerd player) {
			if (completed.contains(player.getUniqueId())) {
				PlayerUtils.send(player, ChatGamesCommand.PREFIX + colorize("&cYou've already correctly answered this game"));
				return;
			}

			completed.add(player.getUniqueId());

			Nexus.log(ChatGamesCommand.PREFIX + player.getNickname() + " answered correctly");
			PlayerUtils.send(player, ChatGamesCommand.PREFIX + colorize("&3That's correct! You've been given &e" + Prize.random().apply(this, player)));

			new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL)
				.receiver(player.getPlayer())
				.pitchStep(6)
				.play();
		}

		public enum Prize {
			ECONOMY {
				@Override
				String apply(ChatGame game, Nerd player) {
					int amount = Math.max(25, 150 - (50 * (game.getCompleted().size() - 1)));
					new BankerService().deposit(player, amount, ShopGroup.SURVIVAL, TransactionCause.SERVER);
					return prettyMoney(amount);
				}
			},
			;

			public static Prize random() {
				return EnumUtils.random(Prize.class);
			}

			abstract String apply(ChatGame game, Nerd player);
		}
	}

}
