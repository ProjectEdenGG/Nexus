package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.quests.QuestReward;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.models.trophy.TrophyHolderService;
import gg.projecteden.nexus.models.trophy.TrophyType;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;
import java.util.function.BiConsumer;

@Getter
@AllArgsConstructor
public enum Pugmas25QuestReward implements QuestReward {
	COSTUME_SPARKLER(((uuid, amount) -> {
		new CostumeUserService().edit(uuid, user -> user.getOwnedCostumes().add("hand/misc/sparkler"));
		PlayerUtils.send(uuid, Pugmas25.PREFIX + "You now own the Sparkler hand costume! &c/costumes");
	})),
	TROPHY_ANNIVERSARY_CAKE(((uuid, amount) -> {
		new TrophyHolderService().edit(uuid, user -> user.earn(TrophyType.PUGMAS_2025_ANNIVERSARY_CAKE));
		PlayerUtils.send(uuid, Pugmas25.PREFIX + "You now own the Pugmas 2025 Anniversary Cake trophy! &c/trophies");
	})),
	TROPHY_NUTCRACKER(((uuid, amount) -> {
		new TrophyHolderService().edit(uuid, user -> user.earn(TrophyType.PUGMAS_2025_NUTCRACKER));
		PlayerUtils.send(uuid, Pugmas25.PREFIX + "You now own the Pugmas 2025 Nutcracker trophy! &c/trophies");
	})),
	TROPHY_MINIGOLF(((uuid, amount) -> {
		new TrophyHolderService().edit(uuid, user -> user.earn(TrophyType.PUGMAS_2025_MINIGOLF));
		PlayerUtils.send(uuid, Pugmas25.PREFIX + "You now own the Pugmas 2025 Minigolf trophy! &c/trophies");
	})),
	;

	private final BiConsumer<UUID, Integer> consumer;
}
