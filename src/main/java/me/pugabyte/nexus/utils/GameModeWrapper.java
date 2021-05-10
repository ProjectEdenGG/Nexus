package me.pugabyte.nexus.utils;

import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import me.lexikiq.HasHumanEntity;
import org.bukkit.GameMode;

import java.util.Set;

@EqualsAndHashCode
public class GameModeWrapper {
	private final GameMode gameMode;
	private static final Set<GameMode> SURVIVAL_MODES = ImmutableSet.of(GameMode.SURVIVAL, GameMode.ADVENTURE);
	private static final Set<GameMode> CREATIVE_MODES = ImmutableSet.of(GameMode.CREATIVE, GameMode.SPECTATOR);

	private GameModeWrapper(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	public static GameModeWrapper of(GameMode gameMode) {
		return new GameModeWrapper(gameMode);
	}

	public static GameModeWrapper of(HasHumanEntity player) {
		return new GameModeWrapper(player.getPlayer().getGameMode());
	}

	/**
	 * Returns if this gamemode is survival-like. This gamemode cannot fly, has hunger, etc.
	 * <p>
	 * As of 1.16, this includes the modes {@link GameMode#SURVIVAL} and {@link GameMode#ADVENTURE}.
	 * @return true if gamemode is survival-like
	 */
	public boolean isSurvival() {
		return SURVIVAL_MODES.contains(gameMode);
	}

	/**
	 * Returns if this gamemode is creative-like. This gamemode can fly, does not have hunger, etc.
	 * <p>
	 * As of 1.16, this includes the modes {@link GameMode#CREATIVE} and {@link GameMode#SPECTATOR}.
	 * @return true if gamemode is creative-like
	 */
	public boolean isCreative() {
		return CREATIVE_MODES.contains(gameMode);
	}

	public boolean is(GameMode gameMode) {
		return this.gameMode.equals(gameMode);
	}
	
	public boolean is(GameModeWrapper gameModeWrapper) {
		return equals(gameModeWrapper);
	}

	public boolean is(HasHumanEntity player) {
		return is(player.getPlayer().getGameMode());
	}
}
