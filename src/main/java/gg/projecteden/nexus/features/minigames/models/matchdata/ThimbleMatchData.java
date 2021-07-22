package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.Thimble;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@MatchDataFor(Thimble.class)
public class ThimbleMatchData extends MatchData {
	private Map<Player, Material> chosenConcrete = new HashMap<>();

	public ThimbleMatchData(Match match) {
		super(match);
	}

	public boolean concreteIsChosen(Material id) {
		return chosenConcrete.containsValue(id);
	}

	public Material getAvailableConcreteId() {
		final Material[] CONCRETE_IDS = ((Thimble) MechanicType.THIMBLE.get()).getCONCRETE_IDS();
		Optional<Material> first = Arrays.asList(CONCRETE_IDS).stream()
				.filter(id -> !concreteIsChosen(id))
				.findFirst();

		if (!first.isPresent())
			throw new InvalidInputException("No available concretes");

		return first.get();
	}

}
