package gg.projecteden.nexus.models.chatgames;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.chat.games.ChatGameType;
import gg.projecteden.nexus.features.chat.games.ChatGamesCommand;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
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
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
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
import org.simmetrics.metrics.StringMetrics;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;

import static gg.projecteden.nexus.features.discord.Discord.discordize;
import static gg.projecteden.nexus.utils.StringUtils.colorize;
import static gg.projecteden.nexus.utils.StringUtils.getDiscordPrefix;
import static gg.projecteden.nexus.utils.StringUtils.prettyMoney;

@Data
@Entity(value = "chat_games_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class, LocalDateTimeConverter.class})
public class ChatGamesConfig implements PlayerOwnedObject {
	public static final int REQUIRED_PLAYERS = 7;

	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled;

	@Getter
	@Setter
	private static ChatGame currentGame;

	@Getter
	@Setter
	private static boolean ignorePlayers = false;

	public static boolean hasRequiredPlayers() {
		return ignorePlayers || OnlinePlayers.where().afk(false).vanished(false).get().size() >= REQUIRED_PLAYERS;
	}

	public static void processQueue() {
		if (!hasRequiredPlayers())
			return;

		if (getCurrentGame() != null)
			return;

		final ChatGamesConfigService service = new ChatGamesConfigService();
		final ChatGamesConfig config = service.get0();
		if (!config.isEnabled())
			return;

		ChatGameType.random().create().queue();
		service.save(config);
	}

	@Data
	public static class ChatGame {
		private final ChatGameType gameType;
		private final String answer;
		private final JsonBuilder broadcast;
		private final String discordBroadcast;
		private LinkedList<ChatGameUser> completed = new LinkedList<>();
		private LocalDateTime startTime;
		private boolean started;
		private int taskId;
		@Data
		@AllArgsConstructor
		private static class ChatGameUser {
			@NonNull UUID uuid;
			LocalDateTime dateTime;
		}

		public ChatGame(ChatGameType gameType, String answer, JsonBuilder broadcast) {
			this(gameType, answer, broadcast, discordize(broadcast));
		}

		public ChatGame(ChatGameType gameType, String answer, JsonBuilder broadcast, String discordBroadcast) {
			this.gameType = gameType;
			this.answer = answer;
			this.broadcast = broadcast;
			this.discordBroadcast = discordize(discordBroadcast);
		}

		public void queue() {
			ChatGamesConfig.setCurrentGame(this);
			taskId = Tasks.wait(TickTime.MINUTE.x(RandomUtils.randomDouble(5, 15)), this::start);
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
			if (ChatGamesConfig.getCurrentGame() != null)
				ChatGamesConfig.getCurrentGame().cancel();

			if (!hasRequiredPlayers()) {
				queue();
				return;
			}

			Tasks.cancel(taskId);
			ChatGamesConfig.setCurrentGame(this);
			started = true;
			startTime = LocalDateTime.now();

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
				message.next("&e" + Nickname.of(getCompleted().getFirst().getUuid()) + " &3was the " + (completed.size() == 1 ? "only" : "first") + " to answer correctly!");

				if (completed.size() > 1)
					message.next(" &eHover for rankings");

				message.hover(new ArrayList<>() {{
					add("&eRankings:");
					for (int i = 1; i <= completed.size(); i++) {
						ChatGameUser user = getCompleted().get(i - 1);
						String timespan = StringUtils.getTimeFormat(Duration.between(currentGame.getStartTime(), user.getDateTime()));
						add("&3" + i + ": &e" + timespan + " &3- &e" + Nickname.of(user.getUuid()));
					}
				}});

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
				message.append(String.format("%s was the %s to answer correctly!", Nickname.discordOf(getCompleted().getFirst().getUuid()), completed.size() == 1 ? "only" : "first"));

				if (completed.size() > 1) {
					message.setEmbeds(new EmbedBuilder() {{
						for (int i = 1; i <= completed.size(); i++) {
							ChatGameUser user = getCompleted().get(i - 1);
							String timespan = StringUtils.getTimeFormat(Duration.between(currentGame.getStartTime(), user.getDateTime()));
							appendDescription(String.format("**%d**: %s%n", i, timespan + " - " + Nickname.discordOf(user.getUuid())));
						}
					}}.setTitle("Rankings").build());
				}
			}

			Discord.send(message);
		}

		public void onAnswer(Nerd nerd) {
			if (nerd == null)
				return;

			if (hasCompleted(nerd.getUuid())) {
				PlayerUtils.send(nerd, ChatGamesCommand.PREFIX + colorize("&cYou've already correctly answered this game"));
				return;
			}

			completed.add(new ChatGameUser(nerd.getUuid(), LocalDateTime.now()));

			Nexus.log(ChatGamesCommand.PREFIX + nerd.getNickname() + " answered correctly");
			PlayerUtils.send(nerd, ChatGamesCommand.PREFIX + colorize("&3That's correct! You've been given &e" + Prize.random().apply(this, nerd)));

			if (nerd.getPlayer() != null)
				new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL)
					.receiver(nerd.getPlayer())
					.pitchStep(6)
					.play();
		}

		public enum Prize {
			ECONOMY {
				@Override
				String apply(ChatGame game, Nerd player) {
					int amount = Math.max(25, 150 - (50 * (game.getCompleted().size() - 1)));
					Tasks.sync(() -> new BankerService().deposit(player, amount, ShopGroup.SURVIVAL, TransactionCause.SERVER));
					return prettyMoney(amount);
				}
			},
			;

			public static Prize random() {
				return EnumUtils.random(Prize.class);
			}

			abstract String apply(ChatGame game, Nerd player);
		}

		public boolean hasCompleted(UUID uuid) {
			return getUser(uuid) != null;
		}

		private ChatGameUser getUser(UUID uuid) {
			for (ChatGameUser chatGameUser : new ArrayList<>(completed)) {
				if (chatGameUser != null && chatGameUser.getUuid().equals(uuid))
					return chatGameUser;
			}

			return null;
		}

		public boolean isAnswerSimilar(String message) {
			if (message == null)
				return false;

			switch (gameType) {
				case MATH -> {
					Float userAnswer = null;
					try {
						userAnswer = Float.parseFloat(message);
					} catch (Exception ignored) {
					}

					if (userAnswer == null)
						return false;

					final float gameAnswer = Float.parseFloat(this.answer);
					final float similarMin = gameAnswer - (gameAnswer * .20f);
					final float similarMax = gameAnswer + (gameAnswer * .20f);

					if (userAnswer > similarMin && userAnswer < similarMax)
						return true;
				}

				default -> {
					final float similarity = StringMetrics.levenshtein().compare(this.answer, message);
					double similarityThreshold = .6f;
					if (similarity >= similarityThreshold)
						return true;
				}
			}

			return false;
		}

	}

}
