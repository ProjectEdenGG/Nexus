package gg.projecteden.nexus.models.aeveonproject;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ColorConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;

import java.util.UUID;

@Data
@Entity(value = "aeveonproject_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ColorConverter.class})
public class AeveonProjectUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Color shipColor;
}
