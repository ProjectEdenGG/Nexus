package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import org.bukkit.entity.Entity;

import java.util.ArrayList;

@Data
public class GrabAJumbuckMatchData extends MatchData {
	public ArrayList<Entity> sheeps = new ArrayList<>();
	public ArrayList<Entity> items = new ArrayList<>();

	public GrabAJumbuckMatchData(Match match) {
		super(match);
	}
}
