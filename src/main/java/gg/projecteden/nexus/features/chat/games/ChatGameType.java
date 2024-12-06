package gg.projecteden.nexus.features.chat.games;

import com.google.common.base.Joiner;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfig.ChatGame;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.simmetrics.metrics.StringMetrics;

import java.util.*;
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

			while (StringMetrics.levenshtein().compare(word, shuffled) >= .5) {
				List<Character> chars = new ArrayList<>(word.chars().mapToObj(c -> (char) c).toList());
				Collections.shuffle(chars);
				shuffled = Joiner.on("").join(chars);
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

}
