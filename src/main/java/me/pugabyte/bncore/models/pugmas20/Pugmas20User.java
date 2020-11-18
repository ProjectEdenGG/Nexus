package me.pugabyte.bncore.models.pugmas20;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.features.events.models.QuestStage;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity("pugmas20_user")
@Converters({UUIDConverter.class})
public class Pugmas20User extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	// Advent
	private List<Integer> foundDays = new ArrayList<>();

	// Quest - Light The Tree
	private QuestStage lightTreeStage = QuestStage.NOT_STARTED;

	// Quest - Toy Testing
	private QuestStage toyTestingStage = QuestStage.NOT_STARTED;

	// Quest - Ornament Vendor
	private QuestStage ornamentVendorStage = QuestStage.NOT_STARTED;

	// Quest - The Mines
	private QuestStage minesStage = QuestStage.NOT_STARTED;

}
