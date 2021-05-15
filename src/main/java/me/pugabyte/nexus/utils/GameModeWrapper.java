package me.pugabyte.nexus.utils;

import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import me.lexikiq.HasHumanEntity;
import org.bukkit.GameMode;
import org.jetbrains.annotations.Contract;

import java.util.Set;

/**
 * Wrapper for gamemodes to provide some common methods for testing what players can do
 */
@EqualsAndHashCode
public class GameModeWrapper {
	private final GameMode gameMode;
	private static final Set<GameMode> SURVIVAL_MODES = ImmutableSet.of(GameMode.SURVIVAL, GameMode.ADVENTURE);
	private static final Set<GameMode> CREATIVE_MODES = ImmutableSet.of(GameMode.CREATIVE, GameMode.SPECTATOR);
	private static final Set<GameMode> BUILD_MODES = ImmutableSet.of(GameMode.SURVIVAL, GameMode.CREATIVE);

	private GameModeWrapper(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	/**
	 * Creates a wrapper around the provided gamemode
	 * @param gameMode gamemode to wrap
	 * @return new wrapper
	 */
	@Contract("_ -> new")
	public static GameModeWrapper of(GameMode gameMode) {
		return new GameModeWrapper(gameMode);
	}

	/**
	 * Creates a wrapper using the provided player's gamemode
	 * @param player player to grab
	 * @return new wrapper
	 */
	@Contract("_ -> new")
	public static GameModeWrapper of(HasHumanEntity player) {
		return of(player.getPlayer().getGameMode());
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

	/**
	 * Returns if this gamemode is able to place or break blocks.
	 * <p>
	 * As of 1.16, this includes the modes {@link GameMode#SURVIVAL} and {@link GameMode#CREATIVE}.
	 * @return true if gamemode can build
	 */
	public boolean canBuild() {
		return BUILD_MODES.contains(gameMode);
	}

	/**
	 * Returns if this gamemode is the same as the provided gamemode
	 * @param gameMode other gamemode
	 * @return true if both gamemodes are the same
	 */
	public boolean is(GameMode gameMode) {
		return this.gameMode.equals(gameMode);
	}

	/**
	 * Returns if this gamemode is the same as the provided gamemode
	 * @param gameModeWrapper other gamemode
	 * @return true if both gamemodes are the same
	 */
	public boolean is(GameModeWrapper gameModeWrapper) {
		return equals(gameModeWrapper);
	}

	/**
	 * Returns if this gamemode is the same as the provided player's gamemode
	 * @param player player to check
	 * @return true if both gamemodes are the same
	 */
	public boolean is(HasHumanEntity player) {
		return is(player.getPlayer().getGameMode());
	}
}
