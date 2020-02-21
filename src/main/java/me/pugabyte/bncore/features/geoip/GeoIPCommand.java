package me.pugabyte.bncore.features.geoip;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Async;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.geoip.GeoIP;
import me.pugabyte.bncore.models.geoip.GeoIPService;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

@Permission("group.moderator")
@NoArgsConstructor
public class GeoIPCommand extends CustomCommand implements Listener {

	public GeoIPCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		BNCore.registerListener(new GeoIPCommand());
	}

	@Path("<player>")
	@Async
	@SneakyThrows
	void geoip(GeoIP geoIp) {
		String location = geoIp.getFriendlyLocationString();
		if (isPlayer())
			send(json("&3Location of &e" + geoIp.getOfflinePlayer().getName() + "&3: &e" + location).hover(geoIp.getIp()).insert(geoIp.getIp()));
		else
			send("Location of " + geoIp.getOfflinePlayer().getName() + " (" + geoIp.getIp() + "): " + location);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Tasks.async(() -> new GeoIPService().get(event.getPlayer()));
	}

	@ConverterFor(GeoIP.class)
	GeoIP convertToGeoIP(String value) {
		return new GeoIPService().get(convertToOfflinePlayer(value));
	}

	@TabCompleterFor(GeoIP.class)
	List<String> tabCompleteGeoIP(String value) {
		return tabCompletePlayer(value);
	}

}
