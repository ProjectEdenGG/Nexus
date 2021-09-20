package gg.projecteden.nexus.features.chat.games;

import com.google.common.base.Joiner;
import gg.projecteden.nexus.models.banker.Banker;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.SoundUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

public enum Games {

	HOVER(15) {
		@Override
		public JsonBuilder create() {
			this.answer = RandomUtils.randomElement(Games.WORDS);
			return new JsonBuilder("&eHover with your mouse &3and type the correct word in chat!").hover("&e" + this.answer);
		}
	},
	UNMUTE(30) {
		@Override
		public JsonBuilder create() {
			this.answer = RandomUtils.randomElement(Games.WORDS);
			StringBuilder muted = new StringBuilder();
			for (char c : this.answer.toCharArray()) {
				if (c == ' ')
					muted.append(" ");
				else if (RandomUtils.chanceOf(50))
					muted.append("*");
				else
					muted.append(c);
			}
			return new JsonBuilder("&3Fix the muted word: &e" + muted + ". &eType the full word in chat!");
		}
	},
	UNSCRAMBLE(30) {
		@Override
		public JsonBuilder create() {
			this.answer = RandomUtils.randomElement(Games.WORDS);
			String[] split = this.answer.split(" ");
			StringBuilder scrambled = new StringBuilder();
			for (String string : split) {
				scrambled.append(shuffle(string)).append(" ");
			}
			return new JsonBuilder("&3Unscramble the word: &e" + scrambled.toString().trim() + ". &eType the full word in chat!");
		}

		private String shuffle(String word) {
			ArrayList<Character> chars = new ArrayList<>();
			for (char c : word.toCharArray()) {
				chars.add(c);
			}
			Collections.shuffle(chars);
			char[] shuffled = new char[chars.size()];
			for (int i = 0; i < shuffled.length; i++) {
				shuffled[i] = chars.get(i);
			}
			return new String(shuffled);
		}
	},
	MATH(15) {
		@Override
		public JsonBuilder create() {
			int num1 = RandomUtils.randomInt(11, 75);
			int num2 = RandomUtils.randomInt(11, 75);
			boolean add = RandomUtils.getRandom().nextBoolean();
			this.answer = (add ? (num1 + num2) : (num1 - num2)) + "";
			return new JsonBuilder(String.format("&3What's %d %s %s?", num1, add ? "+" : "-", num2) + " &eAnswer in chat!");
		}
	};

	private final int timeInSeconds;
	protected String answer;
	protected int correctAnswers;
	protected List<UUID> answeredPlayers = new ArrayList<>();

	Games(int timeInSeconds) {
		this.timeInSeconds = timeInSeconds;
	}

	public void reset() {
		this.answer = null;
		this.correctAnswers = 0;
		this.answeredPlayers.clear();
	}

	public int getTimeInSeconds() {
		return this.timeInSeconds;
	}

	public String getAnswer() {
		return this.answer;
	}

	public abstract JsonBuilder create();

	public void onAnswer(Player player) {
		if (this.answeredPlayers.contains(player.getUniqueId())) {
			player.sendMessage(ChatGames.PREFIX + colorize("&cYou've already correctly answered this game"));
			return;
		}
		this.correctAnswers++;
		int amount;
		switch (this.correctAnswers) {
			case 1 -> amount = 150;
			case 2 -> amount = 100;
			case 3 -> amount = 50;
			default -> amount = 25;
		}
		new BankerService().deposit(Banker.of(player), amount, Shop.ShopGroup.of(player.getWorld()), Transaction.TransactionCause.SERVER);
		player.sendMessage(ChatGames.PREFIX + colorize("&3That's correct! You've been given &e$" + amount));
		this.answeredPlayers.add(player.getUniqueId());
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, SoundUtils.getPitch(6));
	}

	// Feel free to change this, it's just a bigger list
	private static final List<String> WORDS = new ArrayList<>() {{
		addAll(Arrays.stream(Material.values()).filter(Material::isItem).map(StringUtils::camelCase).collect(Collectors.toList()));
		addAll(Arrays.stream(EntityType.values()).map(StringUtils::camelCase).collect(Collectors.toList()));
	}};

}
