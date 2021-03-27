package me.pugabyte.nexus.features.listeners;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

public class SignColors implements Listener {
	@EventHandler
	public void onSignCreate(SignChangeEvent event) {
		Sign sign = (Sign) event.getBlock();
		String[] lines = sign.getLines();
		for (int i = 0; i < lines.length; i++)
			event.setLine(i, colorize(lines[i]));
	}
}
