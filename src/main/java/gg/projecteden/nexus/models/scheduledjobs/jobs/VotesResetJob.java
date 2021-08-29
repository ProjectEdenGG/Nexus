package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.nexus.features.votes.EndOfMonth;
import gg.projecteden.nexus.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.nexus.models.scheduledjobs.common.Schedule;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schedule("0 0 1 * *")
public class VotesResetJob extends AbstractJob {

	@Override
	protected CompletableFuture<JobStatus> run() {
		return EndOfMonth.run().thenCompose($ -> completed());
	}

}
