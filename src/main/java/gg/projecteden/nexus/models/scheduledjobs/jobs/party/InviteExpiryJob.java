package gg.projecteden.nexus.models.scheduledjobs.jobs.party;

import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.nexus.models.party.PartyManager;
import gg.projecteden.nexus.models.party.Party;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@NoArgsConstructor
public class InviteExpiryJob extends AbstractJob {

	UUID partyId;
	UUID user;

	@Override
	protected CompletableFuture<JobStatus> run() {
		Party party = PartyManager.byPartyId(partyId);
		if (party != null && party.getPendingInvites().contains(user))
			party.expireInvite(user);
		return completed();
	}
}
