package gg.projecteden.nexus.models.quests;

import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class QuestTaskStepProgress implements PlayerOwnedObject {
	@NonNull
	private UUID uuid;
	private boolean firstInteraction = true;

}
