package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.minigames.mechanics.FallingBlocks;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.utils.PowerUpUtils;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

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
	public PowerUpUtils powerUpUtils;

	public List<Integer> fallingBlockTasks = new ArrayList<>();
	public List<Integer> addLayerTask = new ArrayList<>();
	public List<Minigamer> thickLines = new ArrayList<>();
	public List<Minigamer> pauseBlocks = new ArrayList<>();
	public int inconsistentChance = 0;

	public ProtectedRegion ceilingWinRg;
	public ProtectedRegion blocksRg;

	public FallingBlocksMatchData(Match match) {
		super(match);
	}

	public Material getNextColor() {
		List<Material> COLOR_CHOICES = new ArrayList<>(((FallingBlocks) MechanicType.FALLING_BLOCKS.get()).getCOLOR_CHOICES());
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

	public @Nullable Minigamer getMinigamer(Material material) {
		for (UUID uuid : chosenColors.keySet()) {
			Material _material = chosenColors.get(uuid);
			if (_material == material)
				return Minigamer.of(uuid);
		}
		return null;
	}

	public void setColor(Minigamer minigamer, Material material) {
		chosenColors.put(minigamer.getUuid(), material);
	}

	public void removeColor(Minigamer minigamer) {
		chosenColors.remove(minigamer.getUuid());
	}
}
