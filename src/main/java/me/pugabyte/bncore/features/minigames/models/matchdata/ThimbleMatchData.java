package me.pugabyte.bncore.features.minigames.models.matchdata;

import com.google.common.primitives.Shorts;
import lombok.Data;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.features.minigames.mechanics.Thimble;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class ThimbleMatchData extends MatchData {
	private List<Minigamer> turnList = new ArrayList<>();
	private Map<Player, Short> chosenConcrete = new HashMap<>();
	private int turns;
	private Minigamer turnPlayer;
	private int turnWaitTaskId;
	@Accessors(fluent = true)
	private boolean isEnding;

	public ThimbleMatchData(Match match) {
		super(match);
	}

	public boolean concreteIsChosen(Short id) {
		return chosenConcrete.containsValue(id);
	}

	public Short getAvailableConcreteId() {
		final short[] CONCRETE_IDS = ((Thimble) MechanicType.THIMBLE.get()).getCONCRETE_IDS();
		Optional<Short> first = Shorts.asList(CONCRETE_IDS).stream()
				.filter(id -> !concreteIsChosen(id))
				.findFirst();

		if (!first.isPresent())
			throw new InvalidInputException("No available concrete IDs");

		return first.get();
	}

}
