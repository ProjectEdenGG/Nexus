package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.RetryIfInterrupted;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.shops.ResourceWorldCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.CompletableFuture;

@Data
@RetryIfInterrupted
@EqualsAndHashCode(callSuper = true)
public class ResourceWorldSetupJob extends AbstractJob {

	@Override
	protected CompletableFuture<JobStatus> run() {
		if (Nexus.getEnv() != Env.PROD)
			return completed();

		ResourceWorldCommand.setupWorlds();
		return completed();
	}

}
