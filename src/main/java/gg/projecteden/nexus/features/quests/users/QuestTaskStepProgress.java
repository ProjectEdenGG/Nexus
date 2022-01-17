package gg.projecteden.nexus.features.quests.users;

import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class QuestTaskStepProgress implements PlayerOwnedObject {
	@NonNull
	private UUID uuid;
	private boolean firstInteraction = true;

}
