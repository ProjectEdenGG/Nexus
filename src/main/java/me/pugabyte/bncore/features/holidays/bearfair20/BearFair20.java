package me.pugabyte.bncore.features.holidays.bearfair20;

import lombok.Data;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Halloween;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;

@Data
public class BearFair20 implements Listener {

	public static World world = Bukkit.getWorld("safepvp");
	public static String mainRg = "bearfair2020";

	public BearFair20() {
		BNCore.registerListener(this);
		new Halloween();
	}
}
