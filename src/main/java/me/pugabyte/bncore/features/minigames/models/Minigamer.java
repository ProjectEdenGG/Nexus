package me.pugabyte.bncore.features.minigames.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Optional;

import static me.pugabyte.bncore.BNCore.colorize;

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
		Optional<Arena> arena = ArenaManager.get(name);
		if (arena.isPresent()) {
			join(arena.get());
		} else {
			tell("That arena doesn't exist!");
		}
	}

	public void join(Arena arena) {
		if (match == null) {
			Optional<Match> optionalMatch = MatchManager.get(arena);
			if (!optionalMatch.isPresent()) {
				match = new Match(arena);
				MatchManager.add(match);
			} else {
				match = optionalMatch.get();
			}

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
		teleport(Minigames.getGamelobby());
	}

	public void tell(String message) {
		player.sendMessage(BNCore.getPrefix("Minigames") + colorize(message));
	}

	public void teleport(Location location) {
		// TODO: Allow/disallow teleportation
		player.teleport(location);
	}

	public void scored() {
		++score;
	}

	public void clearState() {
		// TODO: Possibly edit ConditionalPerms to disallow voxel?
		// TODO: Unvanish
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
