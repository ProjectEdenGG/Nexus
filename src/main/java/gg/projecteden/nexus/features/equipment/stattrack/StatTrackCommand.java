package gg.projecteden.nexus.features.equipment.stattrack;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.stattrack.StatTrackItem;
import gg.projecteden.nexus.models.stattrack.StatTrackItemService;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.Map.Entry;
import java.util.UUID;

@Permission(Group.ADMIN)
public class StatTrackCommand extends CustomCommand {

	private static final StatTrackItemService SERVICE = new StatTrackItemService();

	public StatTrackCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("enable")
	void enable() {
		ItemStack tool = getToolRequired();
		tool = StatTrack.enableFor(tool);
		player().getInventory().setItemInMainHand(tool);
	}

	@Path("check")
	void check() {
		ItemStack tool = getToolRequired();
		UUID statTrackId = StatTrack.getStatTrackId(tool);
		if (statTrackId == null)
			error("That tool does not have StatTrack enabled");

		StatTrackItem item = SERVICE.get(statTrackId);
		send(PREFIX + "Stats:");
		line();
		for (Entry<String, Integer> value : item.getValues().entrySet())
			send("&e - " + StatTrack.idToDisplay(value.getKey()) + ": " + value.getValue());
	}

	@Path("template")
	void template() {
		player().give(StatTrack.getTemplate());
	}

}
