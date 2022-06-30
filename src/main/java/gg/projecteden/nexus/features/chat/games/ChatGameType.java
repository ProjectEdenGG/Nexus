package gg.projecteden.nexus.features.chat.games;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfig.ChatGame;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

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
			for (char c : answer.toCharArray()) {
				if (c == ' ')
					muted.append(" ");
				else if (RandomUtils.chanceOf(50))
					muted.append("*");
				else
					muted.append(c);
			}
			return new ChatGame(this, answer, new JsonBuilder("&3Complete the phrase: &e" + muted + ". &eType the full phrase in chat!"));
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
			ArrayList<Character> chars = new ArrayList<>();
			for (char c : word.toCharArray())
				chars.add(c);

			Collections.shuffle(chars);
			char[] shuffled = new char[chars.size()];
			for (int i = 0; i < shuffled.length; i++)
				shuffled[i] = chars.get(i);

			return new String(shuffled);
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
		return EnumUtils.random(ChatGameType.class);
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
	}};

}
