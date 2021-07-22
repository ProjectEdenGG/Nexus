package gg.projecteden.nexus.features.quests.ambienceold;

import gg.projecteden.nexus.features.quests.ambienceold.sound.SoundPlayer;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Data
@RequiredArgsConstructor
public class AmbienceUserOld {
	@NonNull
	Player player;
	BlockScanner blockScanner;
	SoundPlayer soundPlayer;
	VariablesOld variables;


}
