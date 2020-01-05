package me.pugabyte.bncore.features.minigames.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerScoredEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import static me.pugabyte.bncore.utils.Utils.colorize;

@Data
@EqualsAndHashCode(exclude = "match")
public class Minigamer {
	@NonNull
	private Player player;
	@ToString.Exclude
	private Match match;
	private Team team;
	private boolean respawning = false;
	private int score = 0;

	public String getName() {
		return player.getName();
	}

	public String getColoredName() {
		if (team == null)
			return player.getName();
		return team.getColor() + player.getName();
	}

	public void join(String name) {
		join(ArenaManager.find(name));
	}

	public void join(Arena arena) {
		if (match == null) {
			match = MatchManager.get(arena);
			if (!match.join(this))
				match = null;
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

	public boolean isIn(Mechanic mechanic) {
		return isIn(mechanic.getClass());
	}

	public boolean isPlaying(Mechanic mechanic) {
		return isPlaying(mechanic.getClass());
	}

	public boolean isInLobby(Mechanic mechanic) {
		return isInLobby(mechanic.getClass());
	}

	public boolean isIn(Class<? extends Mechanic> mechanic) {
		if (match != null)
			return mechanic.isInstance(match.getArena().getMechanic());
		return false;
	}

	public boolean isPlaying(Class<? extends Mechanic> mechanic) {
		if (match != null)
			return mechanic.isInstance(match.getArena().getMechanic()) && match.isStarted();
		return false;
	}

	public boolean isInLobby(Class<? extends Mechanic> mechanic) {
		if (match != null)
			return mechanic.isInstance(match.getArena().getMechanic()) && !match.isStarted();
		return false;
	}

	public void toGamelobby() {
		teleport(Minigames.getGamelobby());
	}

	public void tell(String message) {
		player.sendMessage(Utils.getPrefix("Minigames") + colorize(message));
	}

	public void teleport(Location location) {
		// TODO: Allow/disallow teleportation
		player.teleport(location);
	}

	public void scored() {
		scored(1);
	}

	public void scored(int scored) {
		setScore(score + scored);
	}

	public void setScore(int score) {
		int diff = score - this.score;

		MinigamerScoredEvent event = new MinigamerScoredEvent(this, diff);
		Utils.callEvent(event);
		if (event.isCancelled()) return;

		this.score += event.getAmount();
		match.getScoreboard().update();
	}

	public void clearState() {
		// TODO: Possibly edit ConditionalPerms to disallow voxel?
		// TODO: Unvanish
		player.setGameMode(GameMode.ADVENTURE);
		player.setHealth(20);
		player.setExp(0);
		player.setTotalExperience(0);
		player.setLevel(0);
		player.getInventory().clear();
		for (PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());

		for (Player toUnhide : Bukkit.getOnlinePlayers())
			if (player != toUnhide)
				player.showPlayer(BNCore.getInstance(), toUnhide);
	}

}
