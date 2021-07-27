package gg.projecteden.nexus.models.job.jobs;

import gg.projecteden.nexus.models.job.AbstractJob;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TestJob extends AbstractJob {
	private UUID uuid;
	private String message;

	@Override
	protected CompletableFuture<JobStatus> run() {
		PlayerUtils.getOnlinePlayer(uuid).sendMessage(message);
		return completed();
	}

}
