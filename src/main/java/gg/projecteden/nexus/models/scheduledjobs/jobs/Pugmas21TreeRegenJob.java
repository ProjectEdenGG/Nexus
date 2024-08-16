package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.RetryIfInterrupted;
import gg.projecteden.nexus.features.events.y2021.pugmas21.models.Pugmas21TreeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@RetryIfInterrupted
public class Pugmas21TreeRegenJob extends AbstractJob {
	private Pugmas21TreeType treeType;
	private int treeId;

	@Override
	protected CompletableFuture<JobStatus> run() {
		return treeType.build(treeId).thenCompose($ -> completed());
	}

}
