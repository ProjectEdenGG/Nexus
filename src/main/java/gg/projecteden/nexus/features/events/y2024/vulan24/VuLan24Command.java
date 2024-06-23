package gg.projecteden.nexus.features.events.y2024.vulan24;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.IEventCommand;
import gg.projecteden.nexus.features.events.y2024.vulan24.decorations.VuLanDecorStore;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Aliases("vulan")
public class VuLan24Command extends IEventCommand {

	public VuLan24Command(CommandEvent event) {
		super(event);
	}

	@Override
	public EdenEvent getEdenEvent() {
		return VuLan24.get();
	}

	@Path("store debug [enabled]")
	@Permission(Group.ADMIN)
	@Description("Toggle debugging the store")
	void setDebug(Boolean enabled) {
		if (enabled == null)
			enabled = !VuLanDecorStore.getDebuggers().contains(player());

		if (enabled)
			VuLanDecorStore.getDebuggers().add(player());
		else
			VuLanDecorStore.getDebuggers().remove(player());

		send(PREFIX + "Store Debug " + (enabled ? "&aEnabled" : "&cDisabled"));
	}

}
