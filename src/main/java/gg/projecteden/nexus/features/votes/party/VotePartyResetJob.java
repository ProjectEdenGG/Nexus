package gg.projecteden.nexus.features.votes.party;

import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;

import java.util.concurrent.CompletableFuture;

public class VotePartyResetJob extends AbstractJob {
	@Override
	protected CompletableFuture<JobStatus> run() {
		VoteParty.setCompleted(false);
		return CompletableFuture.completedFuture(JobStatus.COMPLETED);
	}
}
