package gg.projecteden.nexus.models.job.jobs;

import gg.projecteden.nexus.features.events.y2020.pugmas20.quests.OrnamentVendor.PugmasTreeType;
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
public class Pugmas20TreeRegenJob extends AbstractJob {
	private PugmasTreeType treeType;
	private int treeId;

	@Override
	protected CompletableFuture<JobStatus> run() {
		var future = completable();
		treeType.build(treeId).thenRun(() -> future.complete(JobStatus.COMPLETED));
		return future;
	}

}
