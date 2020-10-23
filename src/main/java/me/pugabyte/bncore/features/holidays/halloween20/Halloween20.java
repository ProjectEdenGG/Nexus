package me.pugabyte.bncore.features.holidays.halloween20;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;

public class Halloween20 implements Listener {
	@Getter
	public static String region = "halloween20";
	@Getter
	public static World world = Bukkit.getWorld("safepvp");

	public Halloween20() {
		new LostPumpkins();
	}


}
