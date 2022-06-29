package gg.projecteden.nexus.models.jobs;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "job_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class JobConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<JobType, Integer> availableJobs = new ConcurrentHashMap<>();
	private Map<JobType, List<Integer>> npcIds = new ConcurrentHashMap<>();
	private Map<JobType, List<JobItemConfig>> jobItemConfigs = new ConcurrentHashMap<>();

	@Data
	private static class JobItemConfig {
		private UUID uuid;
		private ItemStack item;
		private int tier;
		private int sellExperience;
		private double purchasePrice, sellPrice;
		private int stock;
		private int sellCap;
	}

}
