package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.nexus.models.scheduledjobs.common.AbstractJob;
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
public class BlockRegenJob extends AbstractJob {
	private Location location;
	private Material material;

	@Override
	protected CompletableFuture<JobStatus> run() {
		location.getBlock().setType(material);
		return completed();
	}

}
