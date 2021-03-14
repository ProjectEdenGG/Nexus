package me.pugabyte.nexus.features.commands;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

@Aliases("rp")
@NoArgsConstructor
public class ResourcePackCommand extends CustomCommand implements Listener {
	private static final String URL = "http://cdn.bnn.gg/BearNationResourcePack.zip";
	private static String hash = Utils.createSha1(URL);

	public ResourcePackCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private void resourcePack(Player player) {
		player.setResourcePack(URL, hash);
	}

	@Path
	void resourcePack() {
		if (hash == null)
			error("Resource pack hash is null");

		if (Status.DECLINED.equals(player().getResourcePackStatus()))
			error("You declined the original prompt for the resource pack. In order to use the resource pack, you must edit the server in your server list and change the \"Server Resource Packs\" option to either \"enabled\" or \"prompt\"");

		resourcePack(player());
	}

	@Permission("group.staff")
	@Path("getStatus [player]")
	void getStatus(@Arg("self") Player player) {
		send(PREFIX + "Resource pack status for " + player.getName() + ": &e" + (player.getResourcePackStatus() == null ? "null" : camelCase(player.getResourcePackStatus())));
	}

	@Async
	@Path("update")
	@Permission("group.admim")
	void update() {
		String newHash = Utils.createSha1(URL);
		if (hash != null && hash.equals(newHash))
			error("No resource pack update found");

		hash = newHash;

		if (hash == null)
			error("Resource pack hash is null");

//		TODO: Figure out a solution that actually works, this just disables the active resource pack for all players who click it
//		for (Player player : Bukkit.getOnlinePlayers())
//			if (Arrays.asList(Status.ACCEPTED, Status.SUCCESSFULLY_LOADED).contains(player.getResourcePackStatus()))
//				send(player, json(PREFIX + "There's an update to the resource pack available, click to update.").command("/rp"));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Tasks.wait(Time.SECOND.x(1), () -> resourcePack(event.getPlayer()));
	}

	@EventHandler
	public void onResourcePackEvent(PlayerResourcePackStatusEvent event) {
		Nexus.debug("Resource Pack Status Update: " + event.getPlayer().getName() + " = " + event.getStatus());
	}
}
