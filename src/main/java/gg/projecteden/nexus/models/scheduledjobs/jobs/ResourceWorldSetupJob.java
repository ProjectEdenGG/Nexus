package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.RetryIfInterrupted;
import gg.projecteden.nexus.features.shops.ResourceWorldCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.CompletableFuture;

@Data
@EqualsAndHashCode(callSuper = true)
@RetryIfInterrupted
public class ResourceWorldSetupJob extends AbstractJob {

	@Override
	protected CompletableFuture<JobStatus> run() {
		ResourceWorldCommand.setupWorlds();
		return completed();
	}

}
