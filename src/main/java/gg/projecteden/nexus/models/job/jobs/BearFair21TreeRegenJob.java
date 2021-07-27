package gg.projecteden.nexus.models.job.jobs;

import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.WoodCutting.BearFair21TreeType;
import gg.projecteden.nexus.models.job.AbstractJob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BearFair21TreeRegenJob extends AbstractJob {
	private BearFair21TreeType treeType;
	private int id;

	@Override
	protected CompletableFuture<JobStatus> run() {
		var future = completable();
		treeType.build(id).thenRun(() -> future.complete(JobStatus.COMPLETED));
		return future;
	}

}
