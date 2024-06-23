package gg.projecteden.nexus.features.resourcepack.commands;

import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreManager;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@Permission(Group.ADMIN)
@Aliases("decorstoremanager")
public class DecorationStoreManagerCommand extends CustomCommand {

	public DecorationStoreManagerCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("debug [enabled]")
	@Description("Toggle debugging the store")
	void setDebug(Boolean enabled) {
		if (enabled == null)
			enabled = !DecorationStoreManager.getDebuggers().contains(player());

		if (enabled)
			DecorationStoreManager.getDebuggers().add(player());
		else
			DecorationStoreManager.getDebuggers().remove(player());

		send(PREFIX + "Store Debug " + (enabled ? "&aEnabled" : "&cDisabled"));
	}
}
