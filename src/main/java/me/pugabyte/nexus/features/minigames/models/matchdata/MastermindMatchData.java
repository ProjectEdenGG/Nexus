package me.pugabyte.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.Region;
import lombok.Data;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.mechanics.Mastermind;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.MatchData;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.annotations.MatchDataFor;
import me.pugabyte.nexus.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.nexus.utils.EnumUtils;
import me.pugabyte.nexus.utils.FireworkLauncher;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.LocationUtils.CardinalDirection;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.StringUtils.TimespanFormatter;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static me.pugabyte.nexus.utils.Utils.getFirstIndexOf;

@Data
@MatchDataFor(Mastermind.class)
public class MastermindMatchData extends MatchData {
	private int guess = 1;
	public boolean repeats = false;
	private boolean colorblind = false;
	private final List<Material> answer = new ArrayList<>();

	public static final int answerLength = 4;
	public static final int maxGuesses = 10;
	public static final Material validateCorrect = Material.LIME_CONCRETE;
	public static final Material validateExists = Material.YELLOW_CONCRETE;
	public static final Material validateIncorrect = Material.RED_CONCRETE;

	private static final Set<Material> materials = new LinkedHashSet<Material>() {{
		add(Material.RED_CONCRETE);
		add(Material.ORANGE_CONCRETE);
		add(Material.YELLOW_CONCRETE);
		add(Material.LIME_CONCRETE);
		add(Material.LIGHT_BLUE_CONCRETE);
		add(Material.BLUE_CONCRETE);
		add(Material.PURPLE_CONCRETE);
		add(Material.PINK_CONCRETE);
	}};

	private static final Set<Material> colorblindMaterials = new LinkedHashSet<Material>() {{
		add(Material.TNT);
		add(Material.PUMPKIN);
		add(Material.SPONGE);
		add(Material.MELON);
		add(Material.DIAMOND_BLOCK);
		add(Material.PURPLE_WOOL);
		add(Material.BRAIN_CORAL_BLOCK);
		add(Material.DIRT);
	}};

	public MastermindMatchData(Match match) {
		super(match);

		createAnswer();
	}

	public Set<Material> getMaterials() {
		return colorblind ? colorblindMaterials : materials;
	}

	public void giveLoadout(Minigamer minigamer) {
		minigamer.getPlayer().getInventory().clear();
		for (Material material : getMaterials())
			ItemUtils.giveItem(minigamer.getPlayer(), new ItemStack(material, 64));
	}

	public void reset(Minigamer minigamer) {
		guess = 1;
		createAnswer();
		arena.regenerate();
		minigamer.setScore(0);
		giveLoadout(minigamer);
		resetResultsSign();
	}

	public void createAnswer() {
		answer.clear();

		int tryRepeat = 0;
		while (answer.size() < answerLength) {
			Material material = RandomUtils.randomElement(getMaterials());
			if (!repeats && answer.contains(material))
				continue;

			if (repeats && !answer.contains(material))
				if (++tryRepeat <= 4)
					continue;

			answer.add(material);
		}
	}

	public void guess(Minigamer minigamer) {
		if (this.guess > maxGuesses)
			return;

		Region wallRegion = arena.getRegion("wall");
		Region guessRegion = arena.getRegion("guess");

		for (Block block : WEUtils.getBlocks(guessRegion)) {
			if (block.getType() == Material.AIR)
				throw new MinigameException("You must fill in every block before guessing");
			if (!getMaterials().contains(block.getType()))
				throw new MinigameException("Unknown block in guess area");
		}

		Location wallOrigin = WEUtils.toLocation(wallRegion.getMinimumPoint());
		wallOrigin.setY(wallOrigin.getY() + (guess - 1) * 2);
		BlockFace direction = getDirection(wallOrigin.getBlock());

		WEUtils.paster().clipboard(guessRegion).at(wallOrigin).build();
		WEUtils.getBlocks(guessRegion).forEach(block -> block.setType(Material.AIR));

		List<Material> guess = new ArrayList<>();
		List<Material> answer = new ArrayList<>(this.answer);

		for (int i = 0; i < answerLength; i++)
			guess.add(wallOrigin.getBlock().getRelative(direction, i).getType());

		int correct = 0, exists = 0;
		for (int i = 0; i < guess.size(); i++) {
			if (guess.get(i) == answer.get(i)) {
				++correct;
				guess.set(i, Material.AIR);
				answer.set(i, Material.AIR);
			}
		}

		for (Material current : guess) {
			if (current == Material.AIR)
				continue;
			if (answer.contains(current)) {
				++exists;
				int firstIndexOf = getFirstIndexOf(answer, current);
				answer.set(firstIndexOf, Material.AIR);
			}
		}

		int incorrect = answerLength - correct - exists;

		Block validateOrigin = wallOrigin.getBlock().getRelative(direction, 5);
		int relative = 0;
		for (int i = 0; i < correct; i++)
			validateOrigin.getRelative(direction, relative++).setType(validateCorrect);
		for (int i = 0; i < exists; i++)
			validateOrigin.getRelative(direction, relative++).setType(validateExists);
		for (int i = 0; i < incorrect; i++)
			validateOrigin.getRelative(direction, relative++).setType(validateIncorrect);

		Region resultsSignRegion = arena.getRegion("results_sign");
		Block resultsSignBlock = WEUtils.toLocation(resultsSignRegion.getMinimumPoint()).getBlock();
		if (MaterialTag.SIGNS.isTagged(resultsSignBlock.getType()) && resultsSignBlock.getState() instanceof Sign) {
			Sign resultsSign = (Sign) resultsSignBlock.getState();
			resultsSign.setLine(1, colorize("&aCorrect: &f" + correct));
			resultsSign.setLine(2, colorize("&eWrong spot: &f" + exists));
			resultsSign.setLine(3, colorize("&cIncorrect: &f" + incorrect));
			resultsSign.update();
		}

		if (correct == 4) {
			win(minigamer);
			return;
		}

		++this.guess;
		if (this.guess > maxGuesses)
			lose(minigamer);
	}

	private void lose(Minigamer minigamer) {
		showAnswer();
		minigamer.tell("You were not able to crack the code! Better luck next time");
		endOfGameChatButtons(minigamer);
	}

	private void showAnswer() {
		Region wallRegion = arena.getRegion("wall");
		Location wallOrigin = WEUtils.toLocation(wallRegion.getMinimumPoint());
		wallOrigin.setY(wallOrigin.getY() + maxGuesses * 2);
		BlockFace direction = getDirection(wallOrigin.getBlock());

		for (int i = 0; i < answerLength; i++)
			wallOrigin.getBlock().getRelative(direction, i).setType(answer.get(i));
	}

	private void win(Minigamer minigamer) {
		showAnswer();
		fireworks();
		guess = maxGuesses + 1;
		minigamer.tell("You are the Mastermind! You cracked the code in " + TimespanFormatter.of(minigamer.getScore()).format());
		Tasks.wait(Time.SECOND.x(8), () -> endOfGameChatButtons(minigamer));
	}

	private void endOfGameChatButtons(Minigamer minigamer) {
		new JsonBuilder()
				.newline()
				.next("&a&l  Play Again")
				.command("/mgm mastermind playAgain")
				.hover("Reset the board and play again")
				.group()
				.next("  &3||  &3")
				.group()
				.next("&c&lQuit")
				.command("/mgm quit")
				.hover("End the game")
				.newline()
				.send(minigamer.getPlayer());
	}

	private void fireworks() {
		arena.getNumberedRegionsLike("fireworks").forEach(region -> {
			for (int i = 0; i < 3; i++) {
				Location location = WGUtils.getRandomBlock(region).getLocation();

				int delay = RandomUtils.randomInt(Time.SECOND.get() / 2, Time.SECOND.get());
				Tasks.wait(delay * i, () -> {
					Type type = (Type) RandomUtils.randomElement(EnumUtils.valuesExcept(Type.class, Type.CREEPER, Type.BALL));
					FireworkLauncher.random(location)
							.type(type)
							.power(RandomUtils.randomElement(1, 1, 1, 2))
							.launch();
				});
			}
		});
	}

	public BlockFace getDirection(Block wallOrigin) {
		for (CardinalDirection direction : CardinalDirection.values())
			if (wallOrigin.getRelative(direction.toBlockFace()).getType() == Material.AIR)
				return direction.toBlockFace();

		throw new MinigameException("Could not determine the direction of the wall");
	}

	public void resetResultsSign() {
		Region resultsSignRegion = arena.getRegion("results_sign");
		Block resultsSignBlock = WEUtils.toLocation(resultsSignRegion.getMinimumPoint()).getBlock();
		if (!(MaterialTag.SIGNS.isTagged(resultsSignBlock.getType()) && resultsSignBlock.getState() instanceof Sign))
			Nexus.warn("Mastermind results sign region not configured correctly");
		else {
			Sign resultsSign = (Sign) resultsSignBlock.getState();
			resultsSign.setLine(1, colorize("&aCorrect: &f0"));
			resultsSign.setLine(2, colorize("&eWrong spot: &f0"));
			resultsSign.setLine(3, colorize("&cIncorrect: &f0"));
			resultsSign.update();
		}
	}

}
