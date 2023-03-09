package gg.projecteden.nexus.models.scheduledjobs.jobs.party;

import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.party.PartyManager;
import gg.projecteden.nexus.models.party.Party;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OfflineRemoverJob extends AbstractJob {

	UUID partyId;
	UUID user;

	@Override
	protected CompletableFuture<JobStatus> run() {
		Party party = PartyManager.byPartyId(partyId);
		if (party != null && !Nerd.of(user).isOnline())
			party.kickOffline(user);
		return completed();
	}
}
