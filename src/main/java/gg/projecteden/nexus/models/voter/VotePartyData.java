package gg.projecteden.nexus.models.voter;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(value = "vote_party", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
public class VotePartyData implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private LocalDateTime startDate;
	private int currentAmount;
	private int currentTarget;
	private boolean completed;

	public LocalDateTime getStartDate() {
		if (startDate == null)
			startDate = LocalDateTime.now();
		return startDate;
	}

}
