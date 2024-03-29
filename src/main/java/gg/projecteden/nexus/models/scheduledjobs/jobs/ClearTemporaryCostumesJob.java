package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.RetryIfInterrupted;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@RetryIfInterrupted
public class ClearTemporaryCostumesJob extends AbstractJob {

	@Override
	protected CompletableFuture<JobStatus> run() {
		final CostumeUserService service = new CostumeUserService();
		for (CostumeUser uuid : service.getAll()) {
			final CostumeUser user = service.get(uuid);
			user.setTemporaryVouchers(0);
			user.getTemporarilyOwnedCostumes().clear();
			new HashMap<>(user.getActiveCostumes()).forEach((type, id) -> {
				if (!user.owns(id))
					user.setActiveCostume(type, null);
			});
		}
		service.saveCache();
		return completed();
	}

}
