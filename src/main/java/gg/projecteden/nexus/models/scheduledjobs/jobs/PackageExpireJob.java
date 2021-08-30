package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.models.scheduledjobs.common.RetryIfInterrupted;
import gg.projecteden.nexus.features.store.Package;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@RetryIfInterrupted
public class PackageExpireJob extends AbstractJob {
	private UUID uuid;
	private String packageId;

	@Override
	protected CompletableFuture<JobStatus> run() {
		Package packageType = Package.getPackage(packageId);
		if (packageType == null)
			throw new InvalidInputException("Package " + packageId + " does not exist");

		packageType.expire(uuid);
		return completed();
	}

}
