package me.pugabyte.bncore.features.holidays.halloween20;

import me.pugabyte.bncore.BNCore;
import org.bukkit.event.Listener;

public class Halloween20 implements Listener {

	public static String region = "halloween20";

	public Halloween20() {
		BNCore.registerListener(new LostPumpkins());
	}


}
