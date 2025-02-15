package gg.projecteden.nexus.features.events.y2021.bearfair21.quests.clientside;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BearFair21NPCNameTag {
	int npcId;
	@NonNull UUID playerUuid;
	List<ArmorStand> armorStands;

}
