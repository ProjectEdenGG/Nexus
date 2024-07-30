package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.Schedule;
import gg.projecteden.nexus.models.vulan24.VuLan24User;
import gg.projecteden.nexus.models.vulan24.VuLan24UserService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.Listener;

import java.util.concurrent.CompletableFuture;

@Data
@EqualsAndHashCode(callSuper = true)
@Schedule("0 0 * 8 *")
public class VuLan24DailyQuestJob extends AbstractJob implements Listener {

	@Override
	protected CompletableFuture<JobStatus> run() {
		final VuLan24UserService userService = new VuLan24UserService();
		for (VuLan24User user : userService.getAll())
			user.newDailyQuest();
		userService.saveCacheSync();
		return completed();
	}

}
