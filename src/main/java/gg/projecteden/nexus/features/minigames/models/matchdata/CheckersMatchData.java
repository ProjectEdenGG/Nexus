package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.Checkers;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Data
@MatchDataFor(Checkers.class)
public class CheckersMatchData extends MatchData {

	public CheckersMatchData(Match match) { super(match); }

	int actionBarTask;
	int move = 1;
	Location zeroLoc;
	Checkers.CheckersPiece[][] board = new Checkers.CheckersPiece[8][8];
	Checkers.CheckersPiece selectedPiece;
	boolean validMove = true;
	int actionBarTaskKey;
	String actionBarMessage;
	boolean processing;
	boolean forceJumps;

	public Player getOpponent() {
		return this.getMatch().getArena().getTeams().get(0).getMinigamers(this.getMatch()).get(0).getPlayer();
	}

	public Player getOwner() {
		return this.getMatch().getArena().getTeams().get(1).getMinigamers(this.getMatch()).get(0).getPlayer();
	}

}
