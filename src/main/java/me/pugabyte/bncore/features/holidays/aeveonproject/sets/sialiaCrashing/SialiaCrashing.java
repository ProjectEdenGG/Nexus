package me.pugabyte.bncore.features.holidays.aeveonproject.sets.sialiaCrashing;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.Set;
import me.pugabyte.bncore.features.holidays.annotations.Region;
import org.bukkit.event.Listener;

@Region("sialia_crashing")
public class SialiaCrashing implements Listener, Set {
	@Getter
	static boolean active = true;

	// sialia -> crashing = ~471 ~ ~-8

	public SialiaCrashing() {
		BNCore.registerListener(this);

		new Sounds();
		new Particles();
	}
}
