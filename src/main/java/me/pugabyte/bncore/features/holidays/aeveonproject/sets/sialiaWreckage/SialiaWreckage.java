package me.pugabyte.bncore.features.holidays.aeveonproject.sets.sialiaWreckage;

import lombok.Getter;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.Set;
import me.pugabyte.bncore.features.holidays.annotations.Region;
import org.bukkit.event.Listener;

@Region("sialia_wreckage")
public class SialiaWreckage implements Listener, Set {
	@Getter
	static boolean active = false;
}
