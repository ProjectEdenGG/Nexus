package me.pugabyte.nexus.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.nexus.features.minigames.mechanics.HoliSplegg;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.MatchData;
import me.pugabyte.nexus.features.minigames.models.annotations.MatchDataFor;
import org.bukkit.entity.ArmorStand;

@Data
@MatchDataFor(HoliSplegg.class)
public class HoliSpleggMatchData extends MatchData {

	public ArmorStand armorStand;
	public int time = 0;

	public HoliSpleggMatchData(Match match) {
		super(match);
	}

}
