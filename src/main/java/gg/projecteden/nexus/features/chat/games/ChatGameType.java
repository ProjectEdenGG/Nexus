package gg.projecteden.nexus.features.chat.games;

import com.google.common.base.Joiner;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfig;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfig.ChatGame;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfigService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.simmetrics.metrics.StringMetrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ChatGameType {
	// RANDOM type to allow random games in command
	RANDOM(0) {
		@Override
		public ChatGame create() {
			return null;
		}
	},
	HOVER(15) {
		@Override
		public ChatGame create() {
			String answer = RandomUtils.randomElement(ChatGameType.WORDS);
			return new ChatGame(this, answer,
				new JsonBuilder("&eHover with your mouse &3and type the phrase in chat!").hover("&e" + answer),
				"Type the phrase in chat: ||" + answer + "||");
		}
	},
	UNMUTE(30) {
		@Override
		public ChatGame create() {
			String answer = RandomUtils.randomElement(ChatGameType.WORDS);
			StringBuilder muted = new StringBuilder();
			for (String part : answer.split(" ")) {
				String mutedPart = part;
				for (int i = 0; i < mutedPart.length() / 2; i++)
					mutedPart = mutedPart.replaceFirst(RandomUtils.randomElement(mutedPart.split("")), "_");
				muted.append(mutedPart).append(" ");
			}
			return new ChatGame(this, answer, new JsonBuilder("&3Complete the phrase: &e" + muted.toString().trim() + ". &eType the full phrase in chat!"));
		}
	},
	UNSCRAMBLE(30) {
		@Override
		public ChatGame create() {
			String answer = RandomUtils.randomElement(ChatGameType.WORDS);
			String[] split = answer.split(" ");
			StringBuilder scrambled = new StringBuilder();
			for (String string : split)
				scrambled.append(shuffle(string)).append(" ");
			return new ChatGame(this, answer, new JsonBuilder("&3Unscramble the phrase: &e" + scrambled.toString().trim() + ". &eType the full phrase in chat!"));
		}

		private String shuffle(String word) {
			String shuffled = word;

			int SAFETY = 0;
			while (StringMetrics.levenshtein().compare(word, shuffled) >= .5) {
				List<Character> chars = new ArrayList<>(word.chars().mapToObj(c -> (char) c).toList());
				Collections.shuffle(chars);
				shuffled = Joiner.on("").join(chars);

				if (SAFETY++ > 500)
					break;
			}

			return shuffled;
		}
	},
	MATH(15) {
		@Override
		public ChatGame create() {
			int num1 = RandomUtils.randomInt(11, 75);
			int num2 = RandomUtils.randomInt(11, 75);
			boolean add = RandomUtils.getRandom().nextBoolean();
			String answer = String.valueOf((add ? (num1 + num2) : (num1 - num2)));
			return new ChatGame(this, answer, new JsonBuilder(String.format("&3What's %d %s %s?", num1, add ? "+" : "-", num2) + " &eAnswer in chat!"));
		}
	},
	TRIVIA(30) {
		@Override
		public ChatGame create() {
			final ChatGamesConfigService service = new ChatGamesConfigService();
			ChatGamesConfig config = service.get0();
			Set<TriviaQuestion> previousQuestions = config.getPreviousTriviaQuestions();

			Set<TriviaQuestion> potentialQuestions = new HashSet<>(List.of(TriviaQuestion.values()));
			TriviaQuestion trivia = RandomUtils.randomElement(potentialQuestions);

			if (previousQuestions.size() >= potentialQuestions.size()) {
				config.getPreviousTriviaQuestions().clear();
				previousQuestions.clear();
			} else {
				potentialQuestions.removeAll(previousQuestions);
				trivia = RandomUtils.randomElement(potentialQuestions);
			}

			config.getPreviousTriviaQuestions().add(trivia);
			service.save(config);

			String question = trivia.getQuestion();
			List<String> answers = getAnswers(trivia);

			return new ChatGame(this, answers, new JsonBuilder("&3" + question));
		}

		private static List<String> getAnswers(TriviaQuestion trivia) {
			List<String> answers = trivia.getAcceptableAnswers();
			String id = trivia.name().toUpperCase();

			if (id.startsWith("WEBSITE_")) {
				List<String> updatedAnswers = new ArrayList<>();
				for (String answer : answers) {
					updatedAnswers.add(answer);
					updatedAnswers.add("https://" + answer);
				}
				answers = updatedAnswers;
			} else if (id.startsWith("COMMAND_")) {
				List<String> updatedAnswers = new ArrayList<>();
				for (String answer : answers) {
					updatedAnswers.add(answer);
					updatedAnswers.add("/" + answer);
				}
				answers = updatedAnswers;
			}
			return answers;
		}
	};

	private final int timeInSeconds;

	public abstract ChatGame create();

	public static ChatGameType random() {
		return RandomUtils.randomElement(EnumUtils.valuesExcept(ChatGameType.class, RANDOM));
	}

	private static final Function<Stream<String>, List<String>> wordFormatter = stream -> stream
			.map(StringUtils::camelCase)
			.map(String::toLowerCase)
			.collect(Collectors.toList());

	private static final Function<Stream<? extends Enum<?>>, List<String>> enumWordFormatter = stream -> wordFormatter.apply(stream.map(Enum::name));

	// Feel free to add
	private static final Set<String> WORDS = new HashSet<>() {{
		addAll(enumWordFormatter.apply(Arrays.stream(Material.values()).filter(Material::isItem)));
		addAll(enumWordFormatter.apply(Arrays.stream(EntityType.values()).filter(type -> type.isAlive() && type != EntityType.PLAYER)));
		addAll(enumWordFormatter.apply(Arrays.stream(Biome.values()).filter(type -> type != Biome.CUSTOM)));
		// https://en.wikipedia.org/wiki/List_of_countries_and_dependencies_by_population - removed countries without number
		addAll(List.of(
			"China", "India", "United States", "Indonesia", "Pakistan", "Nigeria", "Brazil", "Bangladesh", "Russia",
			"Mexico", "Japan", "Philippines", "Ethiopia", "Egypt", "Vietnam", "Democratic Republic of the Congo",
			"Turkey", "Iran", "Germany", "France", "United Kingdom", "Thailand", "Tanzania", "South Africa", "Italy",
			"Myanmar", "Colombia", "Kenya", "South Korea", "Spain", "Argentina", "Algeria", "Uganda", "Iraq", "Sudan",
			"Canada", "Poland", "Morocco", "Uzbekistan", "Ukraine", "Angola", "Afghanistan", "Peru", "Malaysia",
			"Mozambique", "Saudi Arabia", "Yemen", "Ghana", "Madagascar", "Ivory Coast", "Nepal", "Cameroon",
			"Venezuela", "Australia", "North Korea", "Burkina Faso", "Syria", "Mali", "Malawi", "Sri Lanka",
			"Kazakhstan", "Chile", "Zambia", "Romania", "Chad", "Somalia", "Senegal", "Netherlands", "Guatemala",
			"Cambodia", "Ecuador", "Zimbabwe", "South Sudan", "Guinea", "Rwanda", "Benin", "Burundi", "Bolivia",
			"Tunisia", "Belgium", "Papua New Guinea", "Haiti", "Jordan", "Cuba", "Czech Republic", "Dominican Republic",
			"Portugal", "Sweden", "Greece", "United Arab Emirates", "Tajikistan", "Azerbaijan", "Israel", "Honduras",
			"Hungary", "Austria", "Belarus", "Switzerland", "Sierra Leone", "Togo", "Laos", "Libya", "Kyrgyzstan",
			"Turkmenistan", "El Salvador", "Nicaragua", "Serbia", "Bulgaria", "Republic of the Congo", "Paraguay",
			"Denmark", "Singapore", "Central African Republic", "Finland", "Norway", "Lebanon", "Palestine", "Slovakia",
			"New Zealand", "Costa Rica", "Ireland", "Liberia", "Oman", "Kuwait", "Mauritania", "Panama", "Croatia",
			"Eritrea", "Georgia", "Mongolia", "Uruguay", "Bosnia and Herzegovina", "Armenia", "Namibia", "Lithuania",
			"Qatar", "Jamaica", "Albania", "Moldova", "Gambia", "Botswana", "Gabon", "Lesotho", "Slovenia", "Latvia",
			"North Macedonia", "Guinea-Bissau", "Bahrain", "Equatorial Guinea", "Estonia", "East Timor",
			"Trinidad and Tobago", "Mauritius", "Eswatini", "Djibouti", "Cyprus", "Fiji", "Bhutan", "Guyana",
			"Comoros", "Solomon Islands", "Luxembourg", "Montenegro", "Suriname", "Malta", "Maldives", "Cape Verde",
			"Brunei", "Belize", "Bahamas", "Iceland", "Vanuatu", "Barbados", "Sao Tome and Principe", "Samoa",
			"Saint Lucia", "Kiribati", "Seychelles", "Grenada", "Saint Vincent and the Grenadines", "Micronesia",
			"Antigua and Barbuda", "Tonga", "Andorra", "Dominica", "Saint Kitts and Nevis", "Marshall Islands",
			"Liechtenstein", "Monaco", "San Marino", "Palau", "Nauru", "Tuvalu", "Vatican City"
		));
	}};

	@Getter
	public enum TriviaQuestion {
		SERVER_OWNER("Who is the Owner of Project Eden?", List.of("Griffin", "GriffinCodes")),
		SERVER_BIRTH_YEAR("What year was Project Eden born?", "2015"),
		SERVER_PREVIOUS_NAME("What is the former name of Project Eden?", "Bear Nation"),

		TICKET("If you require staff assistance ingame, what should you make?", List.of("a ticket", "ticket")),
		NERDS("What is the most common word referring to our players", List.of("nerd", "nerds")),
		MASCOT("What is the name of the server's loveable pet and mascot?", List.of("Koda", "KodaBear")),
		MOB_NET("Which item can be used to capture mobs in survival and helps transport them across large distances?", "mob net"),
		MCMMO_MAX_SKILL("What is the max mcMMO skill level you can reach on the server?", "200"),
		WEEKLY_WAKKA("What is the name of the admin who can be found hiding around survival spawn?", List.of("Wakka", "WakkaFlocka")),
		MGN("What is the name of the weekly event where players gather to play various minigames together?", List.of("minigame night", "minigame nights")),
		MEMBER_TIME("What is the time played required to achieve member rank?", List.of("1 day", "24 hours")),
		EVENT_TOKENS("What is the currency earned by participating in server events, and are used to purchase various cosmetic items?", "event tokens"),
		CHANNEL_DISCORD("If you see &5[D]&e in front of someone's username in chat, where are they chatting from?", "discord"),
		BACKUP_TIME("How much time passes inbetween every automated server backup?", "4 hours"),
		HOH("Which place on the server would you visit if you wanted to see the full list of former and current staff?", "hall of history"),
		ONEBLOCK("What is the name of server's version of skyblock?", "Oneblock"),

		VOTE_CRATE_RAREST("What is the least likely reward you can obtain from the Vote Crate?", List.of("dragon egg", "ender dragon egg", "enderdragon egg")),
		VOTE_CRATE_MOST_EXPENSIVE("How many vote points does a beacon cost in the Vote Point Store (/vps)?", "250"),
		VOTE_POINT_REWARD_MOST("What is the largest possible vote point bonus you can get from voting for the server (/vote)?", "50"),

		RANK_ELITE("Which rank is after trusted, and is awarded to long time players who engage with the community and help others?", "Elite"),
		RANK_VETERAN("Which rank is given to ex-staff members?", List.of("veteran", "vet")),

		// WEBSITE_ --> "https://..." added to answers
		WEBSITE_SERVER("What's the link to the server website?", "projecteden.gg"),
		WEBSITE_MAP("What's the link to the server map?", "map.projecteden.gg"),
		WEBSITE_WIKI("What's the link to the server wiki?", "wiki.projecteden.gg"),
		WEBSITE_STORE("What's the link to the server store?", "store.projecteden.gg"),

		// COMMAND_ --> "/..." added to answers
		COMMAND_TRUST("What command allows you to manage permissions of other players accessing your homes and protections?", List.of("trusts", "trust")),
		COMMAND_SCOREBOARD("What subcommand allows you to edit the lines on your scoreboard?", List.of("sb edit", "scoreboard edit", "sidebar edit", "featherboard edit")),
		COMMAND_TRASH("What command opens a container where you can deposit junk?", "trash"),
		COMMAND_HOMES_LIMIT("What subcommand displays the maximum number of homes you can set?", "homes limit"),
		COMMAND_MUTEMENU("What command allows you to toggle various messages and sound alerts on the server?", "mutemenu"),
		COMMAND_LOCAL("What command only lets you talk to players within a 500 block radius?", List.of("ch l", "channel l", "channel local", "chat l", "chat local")),
		COMMAND_DECOR_STORE("What subcommand takes you to the shop where you can buy many different custom modelled decorations for your builds", List.of("decor store", "decoration store")),
		COMMAND_MAIL("What command can be used to send messages and items to other players?", List.of("mail", "mail send")),
		COMMAND_APPLY("What command would you use if you wanted to submit an application to join the server's staff team?", "apply"),
		COMMAND_SHOWITEM("What command allows you to display the items in chat?", List.of("showitem", "showenchants")),
		COMMAND_RESOURCE("What command takes you to a world that resets once a month and is commonly used to gather blocks and ores?", List.of("resourceworld", "resource")),
		COMMAND_MODCHECK("What command allows you to see what mods we allow on the server?", List.of("modcheck", "modreview")),
		;

		final String question;
		final List<String> acceptableAnswers;

		TriviaQuestion(String question, String answer) {
			this(question, new ArrayList<>(Collections.singleton(answer)));
		}

		TriviaQuestion(String question, List<String> answers) {
			this.question = "&3Trivia: &e" + question;
			this.acceptableAnswers = answers;
		}
	}

}
