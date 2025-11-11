package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.Schedule;
import gg.projecteden.nexus.models.wordle.WordleUserService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Async
@Schedule("*/30 * * * *")
public class NewWordleJob extends AbstractJob {

	@Override
	protected CompletableFuture<JobStatus> run() {
		for (Player player : OnlinePlayers.getAll()) {
			var user = new WordleUserService().get(player);
			var currentTime = user.getZonedLocalDateTime();

			if (currentTime.getHour() != 0 || currentTime.getMinute() >= 30)
				continue;

			user.notifyOfNewGame();
		}

		return completed();
	}
}
