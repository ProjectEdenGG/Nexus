package gg.projecteden.nexus.features.minigolf.models.events;

import gg.projecteden.nexus.models.minigolf.MiniGolfConfig.MiniGolfCourse;
import gg.projecteden.nexus.models.minigolf.MiniGolfUser;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

public class MiniGolfUserJoinEvent extends MiniGolfUserEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;

	@Getter
	MiniGolfCourse course;

	public MiniGolfUserJoinEvent(MiniGolfUser user, MiniGolfCourse course) {
		super(user);
		this.course = course;
	}
}
