package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.compass.Compass;
import gg.projecteden.nexus.models.compass.CompassService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
public class CompassCommand extends CustomCommand implements Listener {
	private final CompassService service = new CompassService();
	private Compass compass;

	public CompassCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer())
			compass = service.get(player());
	}

	@NoLiterals
	@Path("[on|off]")
	@Description("Display a compass at the top of your screen")
	void run(Boolean enable) {
		if (enable == null)
			enable = !compass.isEnabled();

		compass.setEnabled(enable);
		service.save(compass);

		if (compass.isEnabled())
			compass.start();
		else
			compass.stop();

		send(PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
	}

	@Override
	public void _shutdown() {
		Map<UUID, Compass> cache = new CompassService().getCache();
		cache.values().forEach(Compass::stop);
	}

	static {
		CompassService service = new CompassService();
		OnlinePlayers.getAll().forEach(player -> {
			Compass compass = service.get(player);
			compass.start();
		});
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Compass compass = service.get(event.getPlayer());
		compass.start();
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Compass compass = service.get(event.getPlayer());
		compass.stop();
	}

}
