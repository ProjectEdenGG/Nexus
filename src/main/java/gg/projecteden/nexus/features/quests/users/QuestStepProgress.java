package gg.projecteden.nexus.features.quests.users;

import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class QuestStepProgress implements PlayerOwnedObject {
	@NonNull
	private UUID uuid;
	private boolean firstInteraction = true;

}
