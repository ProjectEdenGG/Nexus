package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.mechanics.HoliSplegg;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;
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
