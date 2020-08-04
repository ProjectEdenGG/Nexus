package me.pugabyte.bncore.features.holidays.aeveonproject.sets.sialiaCrashing;

import lombok.Getter;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.Set;
import me.pugabyte.bncore.features.holidays.annotations.Region;
import org.bukkit.event.Listener;

@Region("sialia_crashing")
public class SialiaCrashing implements Listener, Set {
	@Getter
	static boolean active = false;
		/*
		Sound repeated when sialia is crashing
			/playsound minecraft:entity.elder_guardian.curse master @a[distance=..20] ~ ~ ~ 10 0.8

	 */
}
