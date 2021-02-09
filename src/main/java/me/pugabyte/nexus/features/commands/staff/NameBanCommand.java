package me.pugabyte.nexus.features.commands.staff;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

@NoArgsConstructor
public class NameBanCommand extends CustomCommand implements Listener {

	public NameBanCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@EventHandler
	public void onConnect(PlayerLoginEvent event) {

	}
}
