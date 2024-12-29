package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage;

import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.framework.interfaces.IsColoredAndNamed;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum SabotageTeam implements IsColoredAndNamed {
	CREWMATE(ChatColor.WHITE),
	IMPOSTOR(ChatColor.RED) {
		@Override
		public SabotageTeam render(SabotageTeam viewer) {
			return viewer == IMPOSTOR ? this : CREWMATE;
		}
	},
	JESTER(ChatColor.LIGHT_PURPLE)
	;

	@Accessors(fluent = true)
	private final Colored colored;

	SabotageTeam(ChatColor color) {
		this(Colored.of(color));
	}

	@Override
	public String toString() {
		return StringUtils.camelCase(this);
	}

	@Override
	public @NotNull String getName() {
		return toString();
	}

	// doesn't use ChatColor cus its static values are never equal to custom values
	private static final Map<Color, SabotageTeam> BY_COLOR;
	static {
		Map<Color, SabotageTeam> byChatColor = new HashMap<>();
		for (SabotageTeam team : SabotageTeam.values())
			byChatColor.put(team.colored.getColor(), team);
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

	public static List<Minigamer> getLivingNonImpostors(Match match) {
		return match.getMinigamers().stream().filter(minigamer -> minigamer.isAlive() && of(minigamer) != IMPOSTOR).collect(Collectors.toList());
	}

	public static List<Minigamer> getNonImpostors(Match match) {
		return match.getMinigamers().stream().filter(minigamer -> of(minigamer) != IMPOSTOR).collect(Collectors.toList());
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
