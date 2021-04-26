package me.pugabyte.nexus.models.minigamessetting;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.modifiers.MinigameModifier;
import me.pugabyte.nexus.features.minigames.models.modifiers.MinigameModifiers;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.util.UUID;

@Data
@Entity("minigames_setting")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Converters({UUIDConverter.class})
public class MinigamesSetting extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private MinigameModifier modifier = MinigameModifiers.NONE.getModifier();
}
