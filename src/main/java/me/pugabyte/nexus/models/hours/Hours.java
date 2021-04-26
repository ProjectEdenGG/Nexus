package me.pugabyte.nexus.models.hours;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import eden.mongodb.serializers.LocalDateConverter;
import eden.mongodb.serializers.UUIDConverter;
import lombok.Builder;
import lombok.Data;
import me.pugabyte.nexus.models.PlayerOwnedObject;

@Data
@Builder
@Entity(value = "hours", noClassnameStored = true)
@Converters({UUIDConverter.class, LocalDateConverter.class})
public class Hours extends eden.models.hours.Hours implements PlayerOwnedObject {


}
