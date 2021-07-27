package gg.projecteden.nexus.models.job.jobs;

import gg.projecteden.nexus.models.job.AbstractJob;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Test2Job extends AbstractJob {
	private UUID uuid;
	private int seconds;
	private String message;

	@Override
	protected CompletableFuture<JobStatus> run() {
		var future = completable();
		Tasks.wait(Time.SECOND.x(seconds), () -> {
			try {
				final Player player = PlayerUtils.getOnlinePlayer(uuid);
				player.sendMessage(message);
				future.complete(JobStatus.COMPLETED);
			} catch (Exception ex) {
				ex.printStackTrace();
				future.complete(JobStatus.ERRORED);
			}
		});
		return future;
	}

}
