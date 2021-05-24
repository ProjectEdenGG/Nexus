package me.pugabyte.nexus.features.minigames.models.sabotage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lexikiq.HasUniqueId;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.framework.interfaces.Colored;
import me.pugabyte.nexus.framework.interfaces.ColoredAndNamed;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;

@Getter
@RequiredArgsConstructor
public enum SabotageTeam implements ColoredAndNamed {
	CREWMATE(ChatColor.WHITE),
	IMPOSTOR(ChatColor.RED) {
		@Override
		public SabotageTeam render(SabotageTeam viewer) {
			return viewer == IMPOSTOR ? this : CREWMATE;
		}
	},
	JESTER(ChatColor.LIGHT_PURPLE)
	;

	private final ChatColor teamColor;

	@Override
	public String toString() {
		return camelCase(this);
	}

	@Override
	public @NotNull String getName() {
		return toString();
	}

	@Override
	public @NotNull Color getColor() {
		return teamColor.getColor();
	}

	// doesn't use ChatColor cus its static values are never equal to custom values
	private static final Map<Color, SabotageTeam> BY_COLOR;
	static {
		Map<Color, SabotageTeam> byChatColor = new HashMap<>();
		for (SabotageTeam team : SabotageTeam.values())
			byChatColor.put(team.getTeamColor().getColor(), team);
		BY_COLOR = Map.copyOf(byChatColor);
	}

	@Contract("null -> null; !null -> !null")
	public static SabotageTeam of(Colored color) {
		if (color == null)
			return null;
		return BY_COLOR.get(color.getColor());
	}

	public List<Minigamer> players(Match match) {
		return match.getMinigamers().stream().filter(minigamer -> minigamer.isAlive() && of(minigamer) == this).collect(Collectors.toList());
	}

	public static List<Minigamer> getNonImpostors(Match match) {
		return match.getMinigamers().stream().filter(minigamer -> minigamer.isAlive() && of(minigamer) != IMPOSTOR).collect(Collectors.toList());
	}

	public SabotageTeam render(SabotageTeam viewer) {
		return CREWMATE;
	}

	public final SabotageTeam render(Colored viewer) {
		return render(of(viewer));
	}

	public static SabotageTeam render(Colored viewer, Colored target) {
		return of(target).render(viewer);
	}

	public static SabotageTeam render(Minigamer viewer, Colored target) {
		// let spectators see everyone, and let minigamers see themselves
		if (!viewer.isAlive() || (target instanceof HasUniqueId && viewer.getUniqueId().equals(((HasUniqueId) target).getUniqueId())))
			return of(target);
		return render((Colored) viewer, target);
	}
}
