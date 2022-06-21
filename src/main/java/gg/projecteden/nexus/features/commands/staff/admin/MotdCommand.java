package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

@NoArgsConstructor
@Permission(Group.ADMIN)
public class MotdCommand extends CustomCommand implements Listener {
	private static final String originalMotd = "&f &f &f &f &f &f &f &f &f &f &f &f &f &f &f &f &f " +
		"&a&l⚘ &f &#ffff44&lProject Eden" + (Nexus.getEnv() == Env.PROD ? "" : " &6[" + Nexus.getEnv().name() + "]") + " &f &a&l⚘\n" +
		"&f &f &3Survival &7| &3Creative &7| &3Minigames &7| &3Close Community";
	private static String motd = originalMotd;

	public MotdCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<text...>")
	void motd(String text) {
		motd = colorize(text.replace("\\n", System.lineSeparator()));
		send(PREFIX + "Motd updated");
	}

	@Path("reset")
	void motdReset() {
		motd = originalMotd;
		send(PREFIX + "Motd Reset");
	}

	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		if (motd != null)
			event.setMotd(colorize(motd));
	}

}
