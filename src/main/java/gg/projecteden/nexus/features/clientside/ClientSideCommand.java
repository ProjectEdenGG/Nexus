package gg.projecteden.nexus.features.clientside;

import gg.projecteden.nexus.features.clientside.models.IClientSideEntity;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity.ClientSideEntityType;
import gg.projecteden.nexus.features.listeners.events.PlayerChangingWorldsEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.clientside.ClientSideConfigService;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.models.clientside.ClientSideUserService;
import lombok.NonNull;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ClientSideCommand extends CustomCommand {
	private final ClientSideConfigService configService = new ClientSideConfigService();
	private final ClientSideConfig config = configService.get0();
	private final ClientSideUserService userService = new ClientSideUserService();
	private ClientSideUser user;

	public ClientSideCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = userService.get(player());
	}

	@Path("entities create")
	void entities_create() {
		final Entity target = getTargetEntityRequired();
		final IClientSideEntity<?, ?> clientSideEntity = ClientSideEntityType.of(target);
		configService.edit0(config -> config.getEntities().add(clientSideEntity));
		target.remove();
		send(PREFIX + "Created client side " + camelCase(clientSideEntity.getType()));
	}

	@Path("entities delete")
	void entities_delete() {
//		userService.getOnline().forEach(user -> user.onRemove());
	}

	@Path("entities send all")
	void entities_send_all() {
		config.getEntities(world()).forEach(entity -> entity.send(player()));
		send(PREFIX + "Sent " + config.getEntities(world()).size() + " client side entities");
	}

	@EventHandler
	public void on(PlayerChangingWorldsEvent event) {
		new ClientSideUserService().get(event.getPlayer()).clearVisibleEntities();
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		new ClientSideUserService().get(event.getPlayer()).clearVisibleEntities();
	}

}
