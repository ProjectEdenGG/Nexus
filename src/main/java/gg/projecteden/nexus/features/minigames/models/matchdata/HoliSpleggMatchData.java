package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.HoliSplegg;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import lombok.Data;
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
