package gg.projecteden.nexus.features.events.y2024.vulan24;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.IEventCommand;
import gg.projecteden.nexus.features.events.y2024.vulan24.lantern.LanternAnimation;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

@Aliases("vulan")
@NoArgsConstructor
@Permission(Group.STAFF)
public class VuLan24Command extends IEventCommand {

	public VuLan24Command(CommandEvent event) {
		super(event);
	}

	@Override
	public EdenEvent getEdenEvent() {
		return VuLan24.get();
	}


	@Path("lanternanimation debugPoints <id>")
	@Permission(Group.ADMIN)
	void debugPoints(int id) {
		if (LanternAnimation.getInstance() == null)
			new LanternAnimation();

		for (List<Location> path : LanternAnimation.getInstance().getPaths()) {
			Location loc = path.get(id);
			player().sendBlockChange(loc, Material.RED_CONCRETE.createBlockData());
		}
	}

	@Path("lanternanimation start")
	@Permission(Group.ADMIN)
	void startAnimation() {
		if (LanternAnimation.getInstance() != null)
			error("There is already an active animation");
		new LanternAnimation();
		LanternAnimation.getInstance().start();
	}

	@Path("lanternanimation stop")
	@Permission(Group.ADMIN)
	void stopAnimation() {
		if (LanternAnimation.getInstance() != null)
			LanternAnimation.getInstance().cleanup();
	}

}
