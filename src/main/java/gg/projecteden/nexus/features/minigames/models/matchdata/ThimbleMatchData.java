package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.Thimble;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@MatchDataFor(Thimble.class)
public class ThimbleMatchData extends MatchData {
	private final Map<UUID, Material> chosenColors = new ConcurrentHashMap<>();

	public ThimbleMatchData(Match match) {
		super(match);
	}

	public Material getAvailableColorId() {
		final List<Material> COLOR_CHOICES = ((Thimble) MechanicType.THIMBLE.get()).getCOLOR_CHOICES();

		Optional<Material> first = COLOR_CHOICES.stream()
			.filter(id -> !containsColor(id))
			.findFirst();

		if (first.isEmpty())
			throw new InvalidInputException("No available colors");

		return first.get();
	}

	public boolean containsColor(Material material) {
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
