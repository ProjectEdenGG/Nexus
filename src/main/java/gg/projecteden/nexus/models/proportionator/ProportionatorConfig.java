package gg.projecteden.nexus.models.proportionator;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Entity(value = "proportionator_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class ProportionatorConfig implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private double min;
	private double max;
	private List<EntityType> disabled = new ArrayList<>();
	private Map<EntityType, Double> minOverrides = new HashMap<>();
	private Map<EntityType, Double> maxOverrides = new HashMap<>();

}
