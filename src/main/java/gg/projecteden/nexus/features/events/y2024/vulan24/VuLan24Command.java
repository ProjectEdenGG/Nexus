package gg.projecteden.nexus.features.events.y2024.vulan24;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.IEventCommand;
import gg.projecteden.nexus.features.events.y2024.vulan24.lantern.VuLan24LanternAnimation;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.vulan24.VuLan24User;
import gg.projecteden.nexus.models.vulan24.VuLan24UserService;
import gg.projecteden.nexus.models.warps.WarpType;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

@Aliases("vulan")
@NoArgsConstructor
@Permission(Group.STAFF)
public class VuLan24Command extends IEventCommand {
	private VuLan24UserService service = new VuLan24UserService();
	private VuLan24User user;

	public VuLan24Command(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Override
	public EdenEvent getEdenEvent() {
		return VuLan24.get();
	}

	@Path
	void warp() {
		if (!user.isVisited()) {
			WarpType.VULAN24.get("Avontyre").teleportAsync(player());
		} else {
			WarpType.VULAN24.get("VinhLuc").teleportAsync(player());
		}
	}

	@Path("lantern animation debug path <id>")
	@Permission(Group.ADMIN)
	void lantern_animation_debug_path(int id) {
		if (VuLan24LanternAnimation.getInstance() == null)
			VuLan24LanternAnimation.builder().build();

		for (List<Location> path : VuLan24LanternAnimation.getInstance().getPaths()) {
			Location loc = path.get(id);
			player().sendBlockChange(loc, Material.RED_CONCRETE.createBlockData());
		}
	}

	@Path("lantern animation start [--count] [--moveSpeed]")
	@Permission(Group.ADMIN)
	void lantern_animation_start(
		@Switch @Arg("10") int count,
		@Switch @Arg("2") int moveSpeed
	) {
		if (VuLan24LanternAnimation.getInstance() != null)
			error("There is already an active animation");

		VuLan24LanternAnimation.builder().startingLanterns(count).moveSpeed(moveSpeed).start();
	}

	@Path("lantern animation stop")
	@Permission(Group.ADMIN)
	void lantern_animation_stop() {
		if (VuLan24LanternAnimation.getInstance() == null)
			error("There is no active animation");

		VuLan24LanternAnimation.getInstance().cleanup();
	}

}
