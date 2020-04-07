package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.features.minigames.mechanics.Thimble;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@MatchDataFor(Thimble.class)
public class ThimbleMatchData extends MatchData {
	private List<Minigamer> turnList = new ArrayList<>();
	private Map<Player, Material> chosenConcrete = new HashMap<>();
	private int turns;
	private Minigamer turnPlayer;
	private int turnWaitTaskId;
	@Accessors(fluent = true)
	private boolean isEnding;

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
