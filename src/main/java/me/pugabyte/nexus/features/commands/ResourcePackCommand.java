package me.pugabyte.nexus.features.commands;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

@Aliases("rp")
@Permission("group.staff") // TODO: Remove Permission
@NoArgsConstructor
public class ResourcePackCommand extends CustomCommand implements Listener {
	private static final String url = "http://cdn.bnn.gg/BearNationResourcePack.zip";
	private static String hash = Utils.createSha1(url);

	public ResourcePackCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void resourcePack() {
		if (hash == null)
			error("resource pack hash is null");

		player().setResourcePack(url, hash);
	}

	@Async
	@Path("update")
	void update() {
		hash = Utils.createSha1(url);

		if (hash == null)
			error("resource pack hash is null");
	}

	@EventHandler
	public void onResourcePackEvent(PlayerResourcePackStatusEvent event) {
		Nexus.debug("RP Status Update: " + event.getPlayer().getName() + " = " + event.getStatus());
	}
}
