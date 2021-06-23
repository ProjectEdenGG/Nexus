package me.pugabyte.nexus.models.bearfair21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.resources.fishing.FishingLoot.JunkWeight;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent.Content.ContentCategory;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC.AXEL;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC.BEEKEEPER;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC.FISHERMAN2;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC.JOSE;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC.LUMBERJACK;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC.ORGANIZER;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC.PUGMAS_MAYOR;

@Data
@Entity("bearfair21_user")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class BearFair21User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	// General Quest Stuff
	private Set<ContentCategory> contentCategories = new HashSet<>(Set.of(
		ContentCategory.SPEAKER_PART_SUBWOOFER,
		ContentCategory.SPEAKER_PART_AUX_PORT,
		ContentCategory.SPEAKER_PART_TANGLED_WIRE,
		ContentCategory.SPEAKER_PART_SPEAKER_HEAD)
	);
	private JunkWeight junkWeight = JunkWeight.MAX;
	private int recycledItems = 0;
	private Set<Integer> metNPCs = new HashSet<>();
	// TODO BF21: Set this system up
	private Set<Integer> nextStepNPCs = new HashSet<>(Arrays.asList(
			ORGANIZER.getId(), // Main
			BEEKEEPER.getId(), // Side
			LUMBERJACK.getId(), // Side
			FISHERMAN2.getId(), // Side
			AXEL.getId(), // MGN
			PUGMAS_MAYOR.getId(), // Pugmas
			JOSE.getId() // HALLOWEEN
			// SDU
	));
	private int activeTaskId = -1;

	// Specific
	QuestStage questStage_Main = QuestStage.NOT_STARTED;
	Set<Integer> invitees = new HashSet<>();

	QuestStage questStage_Recycle = QuestStage.NOT_STARTED;
	QuestStage questStage_BeeKeeper = QuestStage.NOT_STARTED;
	QuestStage questStage_Lumberjack = QuestStage.NOT_STARTED;

	// MGN
	QuestStage questStage_MGN = QuestStage.NOT_STARTED;
	boolean mgn_laptopScreen = false;
	boolean mgn_laptopMotherboard = false;

	boolean mgn_connectWiring = false;
	boolean mgn_unscrambledWiring = false;
	boolean mgn_setupRouter = false;

	boolean mgn_foundSpeaker = false;

	Set<Location> mgn_beaconsActivated = new HashSet<>();
	Set<Location> mgn_speakersFixed = new HashSet<>();

	// Pugmas
	QuestStage questStage_Pugmas = QuestStage.NOT_STARTED;
	boolean pugmasCompleted = false;
	int presentNdx = 0;

	// Halloween
	QuestStage questStage_Halloween = QuestStage.NOT_STARTED;
	boolean chocolate = false;
	boolean milk = false;
	boolean flour = false;

	// SDU
	QuestStage questStage_SDU = QuestStage.NOT_STARTED;

	public void addRecycledItems(int count) {
		this.recycledItems += count;
		// TODO BF21: Decrease user junkWeight depending on their recycled items
	}

	public boolean hasMet(int npcId) {
		return getMetNPCs().contains(npcId);
	}

	public void cancelActiveTask() {
		Tasks.cancel(this.activeTaskId);
		this.activeTaskId = -1;
	}
}
