package me.pugabyte.bncore.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockTypes;
import lombok.Data;
import me.pugabyte.bncore.features.minigames.mechanics.Mastermind;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;
import me.pugabyte.bncore.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.bncore.utils.FireworkLauncher;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.StringUtils.TimespanFormatter;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.Utils.CardinalDirection;
import me.pugabyte.bncore.utils.Utils.EnumUtils;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static me.pugabyte.bncore.utils.Utils.getFirstIndexOf;

@Data
@MatchDataFor(Mastermind.class)
public class MastermindMatchData extends MatchData {
	private int guess = 1;
	public boolean repeats = false;
	private boolean colorblind = false;
	private final List<Material> answer = new ArrayList<>();

	public static final int answerLength = 4;
	public static final int maxGuesses = 10;
	public static final Material validateCorrect = Material.BLACK_CONCRETE;
	public static final Material validateExists = Material.LIGHT_GRAY_CONCRETE;
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
			Utils.giveItem(minigamer.getPlayer(), new ItemStack(material, 64));
	}

	public void reset(Minigamer minigamer) {
		guess = 1;
		createAnswer();
		getMatch().getArena().regenerate();
		minigamer.setScore(0);
		giveLoadout(minigamer);
	}

	public void createAnswer() {
		answer.clear();
		while (answer.size() < answerLength) {
			Material material = RandomUtils.randomElement(getMaterials());
			if (!repeats && answer.contains(material))
				continue;
			answer.add(material);
		}
	}

	public void guess(Minigamer minigamer) {
		if (this.guess > maxGuesses)
			return;

		Region wallRegion = getMatch().getArena().getRegion("wall");
		Region guessRegion = getMatch().getArena().getRegion("guess");

		for (Block block : WEUtils.getBlocks(guessRegion)) {
			if (block.getType() == Material.AIR)
				throw new MinigameException("You must fill in every block before guessing");
			if (!getMaterials().contains(block.getType()))
				throw new MinigameException("Unknown block in guess area");
		}

		Location wallOrigin = WEUtils.toLocation(wallRegion.getMinimumPoint());
		wallOrigin.setY(wallOrigin.getY() + (guess - 1) * 2);
		BlockFace direction = getDirection(wallOrigin.getBlock());

		WEUtils.paster().clipboard(guessRegion).at(wallOrigin).paste();
		WEUtils.set(guessRegion, BlockTypes.AIR);

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
		Region wallRegion = getMatch().getArena().getRegion("wall");
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
		getMatch().getArena().getNumberedRegionsLike("fireworks").forEach(region -> {
			for (int i = 0; i < 3; i++) {
				Location location = getMatch().getWGUtils().getRandomBlock(region).getLocation();

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

}
