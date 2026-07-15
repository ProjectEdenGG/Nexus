package gg.projecteden.nexus.features.equipment.stattrack.stats.armor;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.utils.MaterialTag;

@Id("damage_absorbed")
@DisplayName("Damage Absorbed")
public class DamageAbsorbed extends StatTrackStatistic {
	@Override
	public MaterialTag getApplicableTools() {
		return MaterialTag.ARMOR;
	}
}
