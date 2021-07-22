package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.GrabAJumbuck;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import lombok.Data;
import org.bukkit.entity.Entity;

import java.util.ArrayList;

@Data
@MatchDataFor(GrabAJumbuck.class)
public class GrabAJumbuckMatchData extends MatchData {
	public ArrayList<Entity> sheeps = new ArrayList<>();
	public ArrayList<Entity> items = new ArrayList<>();

	public GrabAJumbuckMatchData(Match match) {
		super(match);
	}
}
