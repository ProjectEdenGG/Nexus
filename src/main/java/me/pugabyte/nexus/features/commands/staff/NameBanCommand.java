package me.pugabyte.nexus.features.commands.staff;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nameban.NameBanConfig;
import me.pugabyte.nexus.models.nameban.NameBanConfigService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

@NoArgsConstructor
public class NameBanCommand extends CustomCommand implements Listener {
	final NameBanConfigService service = new NameBanConfigService();
	final NameBanConfig config = service.get(Nexus.getUUID0());

	public NameBanCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@EventHandler
	public void onConnect(PlayerLoginEvent event) {

	}
}
