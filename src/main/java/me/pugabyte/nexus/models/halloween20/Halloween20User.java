package me.pugabyte.nexus.models.halloween20;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.events.y2020.halloween20.models.ComboLockNumber;
import me.pugabyte.nexus.features.events.y2020.halloween20.models.QuestStage;
import me.pugabyte.nexus.features.events.y2020.halloween20.models.SoundButton;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity("halloween20_user")
@Converters({UUIDConverter.class, LocationConverter.class})
public class Halloween20User implements PlayerOwnedObject {

	@Id
	@NonNull
	private UUID uuid;

	@Override
	public UUID getUuid() {
		return uuid;
	}

	// Pumpkin Finding
	private QuestStage.LostPumpkins lostPumpkinsStage = QuestStage.LostPumpkins.NOT_STARTED;
	@Embedded
	private List<Location> foundPumpkins = new ArrayList<>();

	// Main Quest
	private QuestStage.Combination combinationStage = QuestStage.Combination.NOT_STARTED;
	@Embedded
	private List<ComboLockNumber> foundComboLockNumbers = new ArrayList<>();

	// Buttons
	@Embedded
	private List<SoundButton> foundButtons = new ArrayList<>();

}
