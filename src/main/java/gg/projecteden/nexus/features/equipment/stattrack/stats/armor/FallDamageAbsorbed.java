package gg.projecteden.nexus.features.equipment.stattrack.stats.armor;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.utils.MaterialTag;

@Id("fall_damage_absorbed")
@DisplayName("Fall Damage Absorbed")
public class FallDamageAbsorbed extends StatTrackStatistic {
	@Override
	public MaterialTag getApplicableTools() {
		return MaterialTag.ALL_BOOTS;
	}
}
