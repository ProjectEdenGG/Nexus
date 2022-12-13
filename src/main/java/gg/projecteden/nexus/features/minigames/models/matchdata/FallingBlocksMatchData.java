package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.FallingBlocks;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@MatchDataFor(FallingBlocks.class)
public class FallingBlocksMatchData extends MatchData {
	private final Map<UUID, Material> chosenColors = new ConcurrentHashMap<>();
	public int spawnedPowerups = 0;
	public int maxPowerUps = 8;
	public List<Integer> fallingBlockTasks = new ArrayList<>();
	public List<Integer> addLayerTask = new ArrayList<>();
	public List<Minigamer> thickLines = new ArrayList<>();
	public List<Minigamer> pauseBlocks = new ArrayList<>();

	public FallingBlocksMatchData(Match match) {
		super(match);
	}

	public Material getNextColor() {
		final List<Material> COLOR_CHOICES = ((FallingBlocks) MechanicType.FALLING_BLOCKS.get()).getCOLOR_CHOICES();
		Collections.shuffle(COLOR_CHOICES);
		Optional<Material> first = COLOR_CHOICES.stream()
			.filter(material -> !isColorChosen(material))
			.findFirst();

		if (first.isEmpty())
			throw new InvalidInputException("No available colors");

		return first.get();
	}

	public boolean isColorChosen(Material material) {
		return chosenColors.containsValue(material);
	}

	public Material getColor(Minigamer minigamer) {
		return chosenColors.get(minigamer.getUuid());
	}

	public void setColor(Minigamer minigamer, Material material) {
		chosenColors.put(minigamer.getUuid(), material);
	}

	public void removeColor(Minigamer minigamer) {
		chosenColors.remove(minigamer.getUuid());
	}
}
