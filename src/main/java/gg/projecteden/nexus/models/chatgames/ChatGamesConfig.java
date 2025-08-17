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
import gg.projecteden.nexus.features.chat.games.ChatGameType.TriviaQuestion;
import gg.projecteden.nexus.features.chat.games.ChatGamesCommand;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
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
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bukkit.Sound;
import org.simmetrics.metrics.StringMetrics;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@Entity(value = "chat_games_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class, LocalDateTimeConverter.class})
public class ChatGamesConfig implements PlayerOwnedObject {
	public static final int REQUIRED_PLAYERS = 2;

	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled;
	private boolean rewardsEnabled;
	// Used to track if the game should delay for an hour
	private int previousPlayerCount = -1;
	private int previousPlayerCount2 = -1;

	@Getter
	private Set<TriviaQuestion> previousTriviaQuestions = new HashSet<>();

	@Getter
	@Setter
	private static ChatGame currentGame;

	public static boolean hasRequiredPlayers() {
		return OnlinePlayers.where().afk(false).vanished(false).get().size() >= REQUIRED_PLAYERS;
	}

	public static void processQueue() {
		processQueue(false);
	}

	public static void processQueue(boolean overrideWait) {
		if (!hasRequiredPlayers())
			return;

		if (getCurrentGame() != null)
			return;

		final ChatGamesConfigService service = new ChatGamesConfigService();
		final ChatGamesConfig config = service.get0();
		if (!config.isEnabled())
			return;

		double wait = overrideWait ? 0 : (config.previousPlayerCount < 2 && config.previousPlayerCount2 < 2 ? 60 : 0);
		wait += RandomUtils.randomDouble(5, 15);

		ChatGameType.random().create().queue(wait);
		service.save(config);
	}

	@Data
	public static class ChatGame {
		private final ChatGameType gameType;
		private final List<String> answers;
		private final JsonBuilder broadcast;
		private final String discordBroadcast;
		private CopyOnWriteArrayList<ChatGameUser> completed = new CopyOnWriteArrayList<>();
		private LocalDateTime startTime;
		private boolean started;
		private int taskId;

		public String getAnswer() {
			return answers.getFirst();
		}

		@Data
		@AllArgsConstructor
		private static class ChatGameUser {
			@NonNull UUID uuid;
			LocalDateTime dateTime;
		}

		public ChatGame(ChatGameType gameType, String answer, JsonBuilder broadcast) {
			this(gameType, answer, broadcast, Discord.discordize(broadcast));
		}

		public ChatGame(ChatGameType gameType, String answer, JsonBuilder broadcast, String discordBroadcast) {
			this.gameType = gameType;
			this.answers = Collections.singletonList(answer);
			this.broadcast = broadcast;
			this.discordBroadcast = discordBroadcast;
		}

		public ChatGame(ChatGameType gameType, List<String> answers, JsonBuilder broadcast) {
			this(gameType, answers, broadcast, Discord.discordize(broadcast));
		}

		public ChatGame(ChatGameType gameType, List<String> answers, JsonBuilder broadcast, String discordBroadcast) {
			this.gameType = gameType;
			this.answers = answers;
			this.broadcast = broadcast;
			this.discordBroadcast = discordBroadcast;
		}

		public int getPosition(UUID uuid) {
			var index = 1;

			Iterator<ChatGameUser> iterator = completed.iterator();
			while (iterator.hasNext()) {
				ChatGameUser user = iterator.next();
				if (user != null && user.getUuid().equals(uuid))
					return index;
				++index;
			}

			throw new InvalidInputException("Could not find user in completed list");
		}

		public void queue(double waitInMinutes) {
			if (ChatGamesConfig.getCurrentGame() != null)
				ChatGamesConfig.getCurrentGame().cancel();

			ChatGamesConfig.setCurrentGame(this);
			taskId = Tasks.wait(TickTime.MINUTE.x(waitInMinutes), this::start);
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
				queue(RandomUtils.randomDouble(5, 15));
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

			taskId = Tasks.wait(TickTime.SECOND.x(30), this::stop);
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

			new ChatGamesConfigService().edit0(config -> {
				config.previousPlayerCount2 = config.previousPlayerCount;
				config.previousPlayerCount = completed.size();
			});

			ChatGamesConfig.setCurrentGame(null);
			Tasks.wait(TickTime.SECOND, ChatGamesConfig::processQueue);
		}

		private void broadcastEndIngame() {
			String answer = getAnswer();
			JsonBuilder message = new JsonBuilder("&3Game over! The correct answer was &e" + answer + "&3. ");

			if (this.getGameType() == ChatGameType.TRIVIA) {
				if (answers.size() > 1) {
					List<String> answersFormatted = new ArrayList<>(List.of("&3Acceptable answers:"));
					answersFormatted.addAll(answers.stream().map(_answer -> "&3- &e" + _answer).toList());
					String answerFinal = answer + " and " + (answers.size() - 1) + " more";

					message = new JsonBuilder("&3Game over! Acceptable answers were ").group()
						.next("&3[&e" + answerFinal + "&3]. ").hover(answersFormatted).group();
				}
			}

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
			String answer = getAnswer();
			final MessageCreateBuilder message = new MessageCreateBuilder().setContent(StringUtils.getDiscordPrefix("ChatGames") +
				"Game over! The correct answer was **" + answer + "**. ");

			if (completed.isEmpty())
				message.addContent("No one answered correctly");
			else {
				message.addContent(String.format("%s was the %s to answer correctly!", Nickname.discordOf(getCompleted().getFirst().getUuid()), completed.size() == 1 ? "only person" : "first"));

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
				PlayerUtils.send(nerd, ChatGamesCommand.PREFIX + StringUtils.colorize("&cYou've already correctly answered this game"));
				return;
			}

			completed.add(new ChatGameUser(nerd.getUuid(), LocalDateTime.now()));

			Nexus.log(ChatGamesCommand.PREFIX + nerd.getNickname() + " answered correctly");

			String place = StringUtils.getNumberWithSuffix(this.getPosition(nerd.getUuid())) + " place";
			String prize = Prize.random().apply(this, nerd);
			PlayerUtils.send(nerd, ChatGamesCommand.PREFIX + StringUtils.colorize("&3That's correct! You've been given &e" + prize + " &3(&e" + place + "&3)"));

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
					int amount = Math.max(25, 150 - (50 * (game.getPosition(player.getUuid()) - 1)));
					Tasks.sync(() -> new BankerService().deposit(player, amount, ShopGroup.SURVIVAL, TransactionCause.CHAT_GAME));
					return StringUtils.prettyMoney(amount);
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

		public boolean isAnswerCorrect(String message) {
			if (message.equalsIgnoreCase(getAnswer()))
				return true;

			for (String triviaAnswer : answers)
				if (triviaAnswer.equalsIgnoreCase(message))
					return true;

			return false;
		}

		private static final float NUMERIC_THRESHOLD = .2f;
		private static final float LEVENSHTEIN_THRESHOLD = .6f;

		public boolean isAnswerSimilar(String message) {
			if (message == null)
				return false;

			if (gameType == ChatGameType.MATH || (Utils.isDouble(message) && currentGame.hasNumericAnswer())) {
				return isNumberWithinPercentageThreshold(message, NUMERIC_THRESHOLD);
			} else {
				for (String answer : answers) {
					float similarity = StringMetrics.levenshtein().compare(answer, message);
					if (similarity >= LEVENSHTEIN_THRESHOLD)
						return true;
				}
			}

			return false;
		}

		private boolean hasNumericAnswer() {
			for (var answer : answers)
				if (Utils.isDouble(answer))
					return true;

			return false;
		}

		public boolean isNumberWithinPercentageThreshold(String message, float threshold) {
			Float userAnswer = null;
			try {
				userAnswer = Float.parseFloat(message);
			} catch (Exception ignored) {}

			if (userAnswer == null)
				return false;

			for (var answer : answers) {
				if (!Utils.isDouble(answer))
					continue;

				final float gameAnswer = Float.parseFloat(answer);

				float min = gameAnswer - (gameAnswer * threshold);
				float max = gameAnswer + (gameAnswer * threshold);

				if (max < min) {
					float temp = min;
					min = max;
					max = temp;
				}

				if (userAnswer >= min && userAnswer <= max)
					return true;
			}

			return false;
		}

	}

}
