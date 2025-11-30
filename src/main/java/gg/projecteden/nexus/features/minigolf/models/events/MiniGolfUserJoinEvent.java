package gg.projecteden.nexus.features.minigolf.models.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.models.minigolf.MiniGolfUser;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

public class MiniGolfUserJoinEvent extends MiniGolfUserEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;

	@Getter
	ProtectedRegion courseRegion;

	public MiniGolfUserJoinEvent(MiniGolfUser user, ProtectedRegion courseRegion) {
		super(user);
		this.courseRegion = courseRegion;
	}
}
