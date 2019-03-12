package me.pugabyte.bncore.features.minigames.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Optional;

import static me.pugabyte.bncore.features.minigames.Minigames.getArenaManager;
import static me.pugabyte.bncore.features.minigames.Minigames.getMatchManager;

@Data
@EqualsAndHashCode(exclude = "match")
public class Minigamer {
	@NonNull
	private Player player;
	private Match match;
	private Team team;
	private boolean respawning = false;
	private int score = 0;

	public void join(String name) {
		Optional<Arena> arena = getArenaManager().get(name);
		if (arena.isPresent()) {
			join(arena.get());
		} else {
			tell("That arena doesn't exist!");
		}
	}

	public void join(Arena arena) {
		if (match == null) {
			match = getMatchManager().get(arena);
			match.join(this);
		} else {
			tell("You are already in a match");
		}
	}

	public void quit() {
		if (match != null) {
			match.quit(this);
			match = null;
		} else {
			tell("You are not in a match");
		}
	}

	public boolean isPlaying(Class mechanic) {
		if (match != null) {
			return mechanic.isInstance(match.getArena().getMechanic()) && match.isStarted();
		}
		return false;
	}

	public void toLobby() {
		teleport(BNCore.minigames.getGamelobby());
	}

	public void tell(String message) {
		player.sendMessage(BNCore.getPrefix("Minigames") + message);
	}

	public void teleport(Location location) {
		// TODO: Allow/disallow teleportation
		player.teleport(location);
	}

	public void scored() {
		++score;
	}

	public void clearState() {
		player.setGameMode(GameMode.ADVENTURE);
		player.setHealth(20);
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		player.setExp(0);
		player.setTotalExperience(0);
		player.setLevel(0);
		player.getInventory().clear();
	}

}
