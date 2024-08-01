package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.Schedule;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.vulan24.VuLan24;
import gg.projecteden.nexus.features.events.y2024.vulan24.lantern.VuLan24LanternAnimation;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.Listener;

import java.util.concurrent.CompletableFuture;

@Data
@EqualsAndHashCode(callSuper = true)
@Schedule("0 */4 15-31 8 *")
public class VuLan24LanternAnimationJob extends AbstractJob implements Listener {

	@Override
	protected CompletableFuture<JobStatus> run() {
		if (Nexus.getEnv() != Env.PROD)
			return completed();

		if (VuLan24.get().isAfterEvent())
			return completed();

		long currentTime = VuLan24.get().getWorld().getTime();
		long waitTime;
		if (currentTime > 14000)
			waitTime = 24000 + (14000 - currentTime);
		else
			waitTime = 14000 - currentTime;
		Tasks.wait(waitTime, () -> VuLan24LanternAnimation.builder().start());
		return completed();
	}

}
