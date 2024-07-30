package gg.projecteden.nexus.models.vulan24;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.community.VuLan24DailyQuest;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.BoatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Entity(value = "vulan24", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class VuLan24User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean readyToVisit;
	private boolean visited;

	private UUID boatUUID;
	private BoatType boatType = BoatType.OAK;

	private VuLan24DailyQuest dailyQuest;
	private boolean finishedDailyQuest;

	public static VuLan24User of(HasUniqueId player) {
		return new VuLan24UserService().get(player);
	}

	public VuLan24DailyQuest getDailyQuest() {
		if (dailyQuest == null)
			dailyQuest = EnumUtils.random(VuLan24DailyQuest.class);

		return dailyQuest;
	}

	public void newDailyQuest() {
		VuLan24DailyQuest oldDailyQuest = getDailyQuest();
		VuLan24DailyQuest newDailyQuest;

		if (!finishedDailyQuest)
			return;

		while (true) {
			newDailyQuest = EnumUtils.random(VuLan24DailyQuest.class);

			// Prevent same quest two days in a row
			if (oldDailyQuest == newDailyQuest)
				continue;
			// Prevent cooked meat quests two days in a row
			if (oldDailyQuest.name().contains("COOKED") && newDailyQuest.name().contains("COOKED"))
				continue;

			break;
		}

		dailyQuest = newDailyQuest;
		finishedDailyQuest = false;
	}
}
