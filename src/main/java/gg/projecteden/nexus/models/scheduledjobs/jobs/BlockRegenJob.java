package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.RetryIfInterrupted;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@RetryIfInterrupted
public class BlockRegenJob extends AbstractJob {
	private Location location;
	private Material material;
	private Material originalMaterial;

	public BlockRegenJob(Location location, Material material) {
		this.location = location;
		this.material = material;
	}

	@Override
	protected CompletableFuture<JobStatus> run() {
		return location.getWorld().getChunkAtAsync(location).thenRun(() -> {
			if (originalMaterial != null)
				if (location.getBlock().getType() != originalMaterial)
					return;

			location.getBlock().setType(material);
		}).thenCompose($ -> completed());
	}

}
