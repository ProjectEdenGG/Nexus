package gg.projecteden.nexus.models.minigamessetting;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifier;
import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifiers;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Entity(value = "minigames_setting", noClassnameStored = true)
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Converters({UUIDConverter.class})
public class MinigamesSetting implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private MinigameModifier modifier = MinigameModifiers.NONE.getModifier();
}
