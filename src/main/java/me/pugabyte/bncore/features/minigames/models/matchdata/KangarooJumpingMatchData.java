package me.pugabyte.bncore.features.minigames.models.matchdata;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import lombok.Data;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;

import java.util.ArrayList;

@Data
public class KangarooJumpingMatchData extends MatchData {

	public ArrayList<Hologram> hologramArrayList = new ArrayList<>();

	public KangarooJumpingMatchData(Match match) {
		super(match);
	}
}
