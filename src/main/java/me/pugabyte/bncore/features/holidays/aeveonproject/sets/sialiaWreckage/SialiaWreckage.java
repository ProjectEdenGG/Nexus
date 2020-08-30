package me.pugabyte.bncore.features.holidays.aeveonproject.sets.sialiaWreckage;

import lombok.Getter;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSet;
import me.pugabyte.bncore.features.holidays.annotations.Region;
import org.bukkit.event.Listener;

@Region("sialia_wreckage")
public class SialiaWreckage implements Listener, APSet {
	@Getter
	static boolean active = false;
}
