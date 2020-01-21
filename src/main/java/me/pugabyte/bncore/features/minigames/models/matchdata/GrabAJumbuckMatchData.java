package me.pugabyte.bncore.features.minigames.models.matchdata;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import org.bukkit.entity.Entity;

import java.util.ArrayList;

@Data
public class GrabAJumbuckMatchData extends MatchData {

	public ProtectedRegion region;
	public BlockVector max;
	public BlockVector min;
	public ArrayList<Entity> sheeps = new ArrayList<>();
	public ArrayList<Entity> items = new ArrayList<>();

	public GrabAJumbuckMatchData(Match match) {
		super(match);
	}
}
