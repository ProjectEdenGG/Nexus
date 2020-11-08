package me.pugabyte.bncore.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockTypes;
import lombok.Data;
import me.pugabyte.bncore.features.minigames.mechanics.Mastermind;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;
import me.pugabyte.bncore.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.Utils.CardinalDirection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static me.pugabyte.bncore.utils.Utils.getFirstIndexOf;

@Data
@MatchDataFor(Mastermind.class)
public class MastermindMatchData extends MatchData {
	private int guess = 1;
	private final boolean colorblind = false;
	private final List<Material> answer = new ArrayList<>();

	public static final int answerLength = 4;
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

	public void reset() {
		guess = 0;
		createAnswer();
	}

	public void createAnswer() {
		answer.clear();
		for (int i = 0; i < answerLength; i++)
			answer.add(RandomUtils.randomElement(getMaterials()));
	}

	public void guess() {
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

		int correct = 0, exists = 0, incorrect = 0;
		for (int i = 0; i < guess.size(); i++) {
			Material current = guess.get(i);
			if (current == answer.get(i)) {
				++correct;
				answer.set(i, Material.AIR);
			} else if (answer.contains(current)) {
				++exists;
				answer.set(getFirstIndexOf(answer, current), Material.AIR);
			} else
				++incorrect;
		}

		Block validateOrigin = wallOrigin.getBlock().getRelative(direction, 5);
		int relative = 0;
		for (int i = 0; i < correct; i++)
			validateOrigin.getRelative(direction, relative++).setType(validateCorrect);
		for (int i = 0; i < exists; i++)
			validateOrigin.getRelative(direction, relative++).setType(validateExists);
		for (int i = 0; i < incorrect; i++)
			validateOrigin.getRelative(direction, relative++).setType(validateIncorrect);

		++this.guess;
		if (this.guess > 10)
			gameOver();
	}

	private void gameOver() {
	}

	public BlockFace getDirection(Block wallOrigin) {
		for (CardinalDirection direction : CardinalDirection.values())
			if (wallOrigin.getRelative(direction.toBlockFace()).getType() == Material.AIR)
				return direction.toBlockFace();

		throw new MinigameException("Could not determine the direction of the wall");
	}

}
