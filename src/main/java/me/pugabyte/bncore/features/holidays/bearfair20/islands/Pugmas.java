package me.pugabyte.bncore.features.holidays.bearfair20.islands;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import org.bukkit.event.Listener;

public class Pugmas implements Listener {
	private String region = BearFair20.BFRg + "_pugmas";

	public Pugmas() {
		BNCore.registerListener(this);
	}
}
