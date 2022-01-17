package gg.projecteden.nexus.features.ambience.old;

import gg.projecteden.nexus.features.ambience.old.sound.SoundPlayer;
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
