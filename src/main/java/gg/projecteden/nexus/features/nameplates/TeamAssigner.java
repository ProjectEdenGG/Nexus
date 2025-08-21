package gg.projecteden.nexus.features.nameplates;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface TeamAssigner {
	static @NotNull Scoreboard scoreboard() {
		return Bukkit.getScoreboardManager().getMainScoreboard();
	}

	@NotNull Team teamFor(Entity player);
}
