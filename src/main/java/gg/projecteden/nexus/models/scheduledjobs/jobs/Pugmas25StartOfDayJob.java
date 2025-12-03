package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.Schedule;
import gg.projecteden.nexus.models.pugmas25.Advent25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schedule("0 0 * * *")
public class Pugmas25StartOfDayJob extends AbstractJob {

	@Override
	protected CompletableFuture<JobStatus> run() {
		Advent25User.refreshAllPlayers();

		// Brain-dead fix
		final Pugmas25UserService userService = new Pugmas25UserService();
		for (Pugmas25User user : userService.cacheAll())
			user.resetDailys();
		userService.saveCache();

		return completed();
	}

}
