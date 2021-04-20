package me.pugabyte.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.mechanics.Mastermind.MastermindColor;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.MatchData;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.utils.EnumUtils;
import me.pugabyte.nexus.utils.FireworkLauncher;
import me.pugabyte.nexus.utils.LocationUtils.CardinalDirection;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import me.pugabyte.nexus.utils.WorldEditUtils;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static me.pugabyte.nexus.utils.Utils.getFirstIndexOf;

@Data
@NoArgsConstructor
public abstract class IMastermindMatchData extends MatchData {
	protected Map<Minigamer, Integer> guesses = new HashMap<>();
	protected Set<Minigamer> colorblind = new HashSet<>();
	protected boolean repeats = false;
	protected final List<MastermindColor> answer = new ArrayList<>();

	protected static int answerLength = 4;
	protected static int maxGuesses = 10;
	protected static final Material validateCorrect = Material.LIME_CONCRETE;
	protected static final Material validateExists = Material.YELLOW_CONCRETE;
	protected static final Material validateIncorrect = Material.RED_CONCRETE;

	public IMastermindMatchData(Match match) {
		super(match);
	}

	public boolean canGuess(Minigamer minigamer) {
		int guess = getGuess(minigamer);
		return guess >= 1 && guess <= 10;
	}

	public int getGuess(Minigamer minigamer) {
		guesses.putIfAbsent(minigamer, 1);
		return guesses.get(minigamer);
	}

	void incrementGuess(Minigamer minigamer) {
		guesses.put(minigamer, getGuess(minigamer) + 1);
	}

	public boolean isColorblind(Minigamer minigamer) {
		return colorblind.contains(minigamer);
	}

	public void setColorblind(Minigamer minigamer, boolean colorblind) {
		if (colorblind)
			this.colorblind.add(minigamer);
		else
			this.colorblind.remove(minigamer);
	}

	public void toggleColorblind(Minigamer minigamer) {
		if (colorblind.contains(minigamer))
			this.colorblind.remove(minigamer);
		else
			this.colorblind.add(minigamer);
	}

	abstract void win(Minigamer minigamer);

	abstract void lose(Minigamer minigamer);

	abstract void endOfGameChatButtons(Minigamer minigamer);

	protected Set<Material> getMaterials(Minigamer minigamer) {
		return isColorblind(minigamer) ? MastermindColor.getColorblindKit() : MastermindColor.getKit();
	}

	public void giveLoadout(Minigamer minigamer) {
		minigamer.getPlayer().getInventory().clear();
		for (Material material : getMaterials(minigamer))
			PlayerUtils.giveItem(minigamer.getPlayer(), new ItemStack(material, 64));
	}

	public void createAnswer() {
		answer.clear();

		int tryRepeat = 0;
		while (answer.size() < answerLength) {
			MastermindColor color = EnumUtils.random(MastermindColor.class);
			if (!repeats && answer.contains(color))
				continue;

			if (repeats && !answer.contains(color))
				if (++tryRepeat <= 4)
					continue;

			answer.add(color);
		}
	}

	protected void validate(Minigamer minigamer, Region wallRegion, Region guessRegion, Region resultsSignRegion) {
		if (!new CooldownService().check(minigamer.getPlayer(), "minigames-mastermind-guess", Time.SECOND.x(3)))
			return;

		WorldEditUtils WEUtils = minigamer.getMatch().getWEUtils();
		for (Block block : WEUtils.getBlocks(guessRegion)) {
			if (block.getType() == Material.AIR)
				throw new MinigameException("You must fill in every block before guessing");
			if (!getMaterials(minigamer).contains(block.getType()))
				throw new MinigameException("Unknown block in guess area");
		}

		Location wallOrigin = WEUtils.toLocation(wallRegion.getMinimumPoint());
		wallOrigin.setY(wallOrigin.getY() + (getGuess(minigamer) - 1) * 2);
		BlockFace direction = getDirection(wallOrigin.getBlock());

		WEUtils.paster().clipboard(guessRegion).at(wallOrigin).build();
		WEUtils.getBlocks(guessRegion).forEach(block -> block.setType(Material.AIR));

		List<MastermindColor> guess = new ArrayList<>();
		List<MastermindColor> answer = new ArrayList<>(this.answer);

		for (int i = 0; i < answerLength; i++)
			guess.add(MastermindColor.of(wallOrigin.getBlock().getRelative(direction, i).getType()));

		int correct = 0, exists = 0;
		for (int i = 0; i < guess.size(); i++) {
			if (guess.get(i) == answer.get(i)) {
				++correct;
				guess.set(i, null);
				answer.set(i, null);
			}
		}

		for (MastermindColor current : guess) {
			if (current == null)
				continue;
			if (answer.contains(current)) {
				++exists;
				int firstIndexOf = getFirstIndexOf(answer, current);
				answer.set(firstIndexOf, null);
			}
		}

		int incorrect = answerLength - correct - exists;

		Block validateOrigin = wallOrigin.getBlock().getRelative(direction, answerLength + 1);
		int relative = 0;
		for (int i = 0; i < correct; i++)
			validateOrigin.getRelative(direction, relative++).setType(validateCorrect);
		for (int i = 0; i < exists; i++)
			validateOrigin.getRelative(direction, relative++).setType(validateExists);
		for (int i = 0; i < incorrect; i++)
			validateOrigin.getRelative(direction, relative++).setType(validateIncorrect);

		Block resultsSignBlock = WEUtils.toLocation(resultsSignRegion.getMinimumPoint()).getBlock();
		if (MaterialTag.SIGNS.isTagged(resultsSignBlock.getType()) && resultsSignBlock.getState() instanceof Sign) {
			Sign resultsSign = (Sign) resultsSignBlock.getState();
			resultsSign.setLine(1, colorize("&aCorrect: &f" + correct));
			resultsSign.setLine(2, colorize("&eWrong spot: &f" + exists));
			resultsSign.setLine(3, colorize("&cIncorrect: &f" + incorrect));
			resultsSign.update();
		}

		if (correct == answerLength) {
			win(minigamer);
			return;
		}

		incrementGuess(minigamer);
		if (getGuess(minigamer) > maxGuesses)
			lose(minigamer);
	}

	protected void showAnswer(Minigamer minigamer, Region wallRegion) {
		Location wallOrigin = WEUtils.toLocation(wallRegion.getMinimumPoint());
		wallOrigin.setY(wallOrigin.getY() + maxGuesses * 2);
		BlockFace direction = getDirection(wallOrigin.getBlock());

		for (int i = 0; i < answerLength; i++)
			wallOrigin.getBlock().getRelative(direction, i).setType(isColorblind(minigamer) ? answer.get(i).getColorblind() : answer.get(i).getNormal());
	}

	protected BlockFace getDirection(Block wallOrigin) {
		for (CardinalDirection direction : CardinalDirection.values())
			if (wallOrigin.getRelative(direction.toBlockFace()).getType() == Material.AIR)
				return direction.toBlockFace();

		throw new MinigameException("Could not determine the direction of the wall");
	}

	protected void fireworks(String regionName) {
		arena.getRegionsLike(regionName).forEach(region -> {
			for (int i = 0; i < 3; i++) {
				Location location = arena.getWGUtils().getRandomBlock(region).getLocation();

				int delay = RandomUtils.randomInt(Time.SECOND.get() / 2, Time.SECOND.get());
				Tasks.wait(delay * i, () -> {
					Type type = RandomUtils.randomElement(EnumUtils.valuesExcept(Type.class, Type.CREEPER, Type.BALL));
					FireworkLauncher.random(location)
							.type(type)
							.power(RandomUtils.randomElement(1, 1, 1, 2))
							.launch();
				});
			}
		});
	}

	public void resetResultsSign() {
		for (ProtectedRegion resultsSignRegion : arena.getRegionsLike("results_sign")) {
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
}
