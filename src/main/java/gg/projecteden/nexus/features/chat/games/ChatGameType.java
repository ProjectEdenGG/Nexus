package gg.projecteden.nexus.features.chat.games;

import com.google.common.base.Joiner;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfig;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfig.ChatGame;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfigService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.BiomeUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.NumberDisplay;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
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
	RANDOM {
		@Override
		public ChatGame create() {
			return null;
		}
	},
	HOVER {
		@Override
		public ChatGame create() {
			String answer = RandomUtils.randomElement(ChatGameType.WORDS);
			return new ChatGame(this, answer,
				new JsonBuilder("&eHover with your mouse &3and type the phrase in chat!").hover("&e" + answer),
				"Type the phrase in chat: ||" + answer + "||");
		}
	},
	UNMUTE {
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
			return new ChatGame(this, answer,
				new JsonBuilder("&3Complete the phrase: &e" + muted.toString().trim() + ". &3Type the full phrase in chat!"),
				"Complete the phrase: `" + muted.toString().trim() + "`. Type the full phrase in chat!");
		}
	},
	UNSCRAMBLE {
		@Override
		public ChatGame create() {
			String answer = RandomUtils.randomElement(ChatGameType.WORDS);
			String[] split = answer.split(" ");
			StringBuilder scrambled = new StringBuilder();
			for (String string : split)
				scrambled.append(shuffle(string)).append(" ");
			return new ChatGame(this, answer,
				new JsonBuilder("&3Unscramble the phrase: &e" + scrambled.toString().trim() + ". &3Type the full phrase in chat!"),
				"Unscramble the phrase: `" + scrambled.toString().trim() + "`. Type the full phrase in chat!");
		}

		private String shuffle(String word) {
			String shuffled = word;
			List<Character> chars = new ArrayList<>(word.chars().mapToObj(c -> (char) c).toList());

			int SAFETY = 0;
			while (StringMetrics.levenshtein().compare(word, shuffled) >= .5) {
				Collections.shuffle(chars);
				shuffled = Joiner.on("").join(chars);

				if (SAFETY++ > 500) {
					Nexus.log("Chat Games Unscramble hit SAFETY on " + word);
					break;
				}
			}

			return shuffled;
		}
	},
	MATH {
		@Override
		public ChatGame create() {
			int num1 = 0, num2 = 0, answer;
			Utils.ArithmeticOperator operator = RandomUtils.randomElement(EnumUtils.valuesExcept(Utils.ArithmeticOperator.class, Utils.ArithmeticOperator.POWER));
			switch (operator) {
				case ADD, SUBTRACT -> { num1 = RandomUtils.randomInt(11, 75); num2 = RandomUtils.randomInt(11, 75); }
				case MULTIPLY, DIVIDE -> { num1 = RandomUtils.randomInt(2, 12); num2 = RandomUtils.randomInt(2, 12); }
			}

			if (operator == Utils.ArithmeticOperator.DIVIDE) {
				int temp = num1;
				num1 = num1 * num2;
				answer = temp;
			}
			else {
				answer = operator.run(num1, num2).intValue();
			}

			return new ChatGame(this, String.valueOf(answer),
				new JsonBuilder(String.format("&3What's &e%d %s %s&3?", num1, operator.getSymbol(), num2) + " Answer in chat!"),
				String.format("What's `%d %s %s`?", num1, operator.getSymbol(), num2) + " Answer in chat!");
		}
	},
	TRIVIA {
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
	public static final Set<String> WORDS = new HashSet<>() {{
		addAll(enumWordFormatter.apply(Arrays.stream(Material.values()).filter(Material::isItem)));
		addAll(enumWordFormatter.apply(Arrays.stream(EntityType.values()).filter(type -> type.isAlive() && type != EntityType.PLAYER)));
		addAll(Arrays.stream(BiomeUtils.values()).map(BiomeUtils::name).map(String::toLowerCase).toList());
		add("Lingering Potion of the Turtle Master");
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

	static {
		try {
			WORDS.addAll(Arrays.stream(Rank.values()).map(Rank::getName).toList());
			Rank.getStaffNerds().thenAccept((map) -> map.values().forEach(list -> WORDS.addAll(list.stream().map(Nerd::getNickname).toList())));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Getter
	public enum TriviaQuestion {
		NERD("You're a nerd", List.of("no you", "no u")),

		SERVER_OWNER("Who is the Owner of Project Eden?", List.of("Griffin", "GriffinCodes")),
		SERVER_BIRTH_YEAR("What year was Project Eden first opened?", "2015"),
		SERVER_PREVIOUS_NAME("What is the former name of Project Eden?", "Bear Nation"),

		TICKET("If you require staff assistance ingame, what should you make?", List.of("a ticket", "ticket")),
		NERDS("What is the most common word referring to our players", List.of("nerd", "nerds")),
		MASCOT("What is the name of the server's loveable pet and mascot?", List.of("Koda", "KodaBear")),
		MOB_NET("Which item can be used to capture mobs in survival and helps transport them across large distances?", "mob net"),
		MCMMO_MAX_SKILL("What is the max mcMMO skill level you can reach on the server?", getNumberAnswers(200)),
		WEEKLY_WAKKA("What is the name of the admin who can be found hiding around survival spawn?", List.of("Wakka", "WakkaFlocka")),
		MGN("What is the name of the weekly event where players gather to play various minigames together?", List.of("minigame night", "minigame nights", "minigames night", "minigames nights")),
		MEMBER_TIME("What is the time played required to achieve member rank?", List.of("1 day", "24 hours")),
		EVENT_TOKENS("What is the currency earned by participating in server events, and are used to purchase various cosmetic items?", "event tokens"),
		CHANNEL_DISCORD("If you see &5[D]&e in front of someone's username in chat, where are they chatting from?", "discord"),
		BACKUP_TIME("How much time passes inbetween every automated server backup?", List.of("4 hours", "four hours")),
		HOH("Which place on the server would you visit if you wanted to see the full list of former and current staff?", List.of("hall of history", "hoh", "hallofhistory")),
		ONEBLOCK("What is the name of server's version of skyblock?", "Oneblock"),
		BARREL("What storage block has had it's storage space increased on Project Eden?", "barrel"),
		TITAN("What is the name of the server's client-side mod made to streamline loading the resource pack?", "Titan"),

		VOTE_CRATE_RAREST("What is the least likely reward you can obtain from the Vote Crate?", List.of("dragon egg", "ender dragon egg", "enderdragon egg")),
		VOTE_CRATE_MOST_EXPENSIVE("How many vote points does a beacon cost in the Vote Point Store (/vps)?", getNumberAnswers(250)),
		VOTE_POINT_REWARD_MOST("What is the largest possible vote point bonus you can get from voting for the server (/vote)?", getNumberAnswers(50)),

		RANK_ELITE("Which rank is after trusted, and is awarded to long time players who engage with the community and help others?", "Elite"),
		RANK_VETERAN("Which rank is given to ex-staff members?", "Veteran"),

		// MINECRAFT
		BREEDABLE_HOSTILE("What is the only breedable hostile mob?", "hoglin"),
		BREED_CAMELS("What item is used to breed camels?", "cactus"),
		TAME_PARROT("What do you feed a parrot to tame them?", List.of("seeds", "wheat seeds", "beetroot seeds", "melon seeds", "pumpkin seeds", "torchflower seeds", "pitcher pod")),
		DISC_COLOR_MALL("What color is the music disc 'Mall'?", "purple"),
		DISC_COLOR_STAL("What color is the music disc 'Stal'?", "black"),
		DISC_COLOR_CAT("What color is the music disc 'Cat'?", "green"),
		DISC_COLOR_WAIT("What color is the music disc 'Wait'?", "blue"),
		AXOLOTL_COLORS("How many colors of Axolotl's are there?", List.of("5", "five")),
		MAX_LEVEL_BLAST_PROTECTION("What is the maximum vanilla enchantment level for Blast Protection?", getEnchantLevelAnswers(4)),
		MAX_LEVEL_FIRE_ASPECT("What is the maximum vanilla enchantment level for Fire Aspect?", getEnchantLevelAnswers(2)),
		MAX_LEVEL_EFFICIENCY("What is the maximum vanilla enchantment level for Efficiency?", getEnchantLevelAnswers(5)),
		MAX_LEVEL_UNBREAKING("What is the maximum vanilla enchantment level for Unbreaking?", getEnchantLevelAnswers(3)),
		BEETROOT_SOUP("How many beetroot do you need to craft beetroot soup?", List.of("6", "six")),
		CURE_EFFECT("What can you drink to cure a potion effect?", List.of("milk", "milk bucket")),
		PISTON_MAX("What is the maximum amount of blocks a piston can push?", List.of("12", "twelve")),
		SCARED_CREEPER("What mob scares creepers?", List.of("cat", "cats", "ocelot", "ocelots")),
		CAKE_SLICES("How many slices does a cake have?", getNumberAnswers(7)),
		MAX_BOOK_PAGES("What is the maximum number of pages in a writable book?", getNumberAnswers(100)),
		FOOD_MOST_SATURATION("What vanilla food item gives the most saturation?", "golden carrot"),
		VILLAGER_ORIGINAL_NAME("What was the original name for Villagers?", "testificates"),
		WOLF_HEALTH_INDICATOR("What indicates the health level of a Wolf?", List.of("their tail", "tail")),
		TAMEABLE_MOB_FALL_DMG_IMMUNE("What tameable mob is immune to fall damage?", "cat"),
		SMITTEN_TURTLE_DROP("What item drops if lightning strikes a turtle?", List.of("a bowl", "bowl")),
		SMITTEN_PIG_CONVERT("Which mob is created when lightning strikes a pig?", "zombified piglin"),
		SMITTEN_RED_MOOSHROOM_CONVERT("Which mob is created when lightning strikes a red mooshroom?", "brown mooshroom"),
		SMITTEN_BROWN_MOOSHROOM_CONVERT("Which mob is created when lightning strikes a brown mooshroom?", "red mooshroom"),
		SMITTEN_VILLAGER_CONVERT("Which mob is created when lightning strikes a villager?", "witch"),
		MAX_BUILD_HEIGHT("What is the build height limit?", getNumberAnswers(320)),
		HOSTILE_DAY_PASSIVE("What hostile mob becomes passive during the day?", "spider"),
		ANGERED_SPIT("When angered, what mob spits at you?", "llama"),
		BIOME_PACKED_ICE("In which biome would you find packed ice?", List.of("ice spikes", "frozen ocean", "frozen peaks")),
		INSOMNIA_MOB("What flying mob will spawn if you don't sleep for several in-game days?", "phantom"),
		ITEM_GUIDE_PIG("What item is used to guide pigs when riding them?", "carrot on a stick"),
		BLOCK_PLAYS_MUSIC("What vanilla block can be used to play music?", "jukebox"),
		PILLAGER_STRUCTURE("Which structure is home to the Pillagers?", List.of("pillager outpost", "woodland mansion")),
		PARROT_SPAWN_BIOME("Which biome is home to the parrot?", "jungle"),
		VILLAGE_RAID("What in-game event brings waves of hostile mobs attacking a village?", List.of("a raid", "raid")),

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

		// TODO Rank stuff - i.e. rank above/below rank
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

		private static List<String> getNumberAnswers(int number) {
			NumberDisplay numberDisplay = StringUtils.getNumberDisplay(number);

			return List.of(String.valueOf(number), numberDisplay.asWords());
		}

		private static List<String> getEnchantLevelAnswers(int level) {
			NumberDisplay numberDisplay = StringUtils.getNumberDisplay(level);

			return List.of(String.valueOf(level), numberDisplay.asWords(), numberDisplay.asRoman());
		}
	}
}
