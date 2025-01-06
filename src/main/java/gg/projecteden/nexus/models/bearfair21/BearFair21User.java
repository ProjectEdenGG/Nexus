package gg.projecteden.nexus.models.bearfair21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.models.QuestStage;
import gg.projecteden.nexus.features.events.y2021.bearfair21.Quests;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing.FishingLoot.JunkWeight;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.bearfair21.ClientsideContent.Content.ContentCategory;
import gg.projecteden.nexus.utils.Tasks;
import lombok.*;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "bearfair21_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class BearFair21User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean firstVisit = true;
	// General Quest Stuff
	private Set<ContentCategory> contentCategories = new HashSet<>(Set.of(
		ContentCategory.SPEAKER_PART_SUBWOOFER,
		ContentCategory.SPEAKER_PART_AUX_PORT,
		ContentCategory.SPEAKER_PART_TANGLED_WIRE,
		ContentCategory.SPEAKER_PART_SPEAKER_HEAD
	));
	private Set<Integer> metNPCs = new HashSet<>();
	private Set<Integer> nextStepNPCs = new HashSet<>(Set.of(
		BearFair21NPC.ORGANIZER.getId(),        // Intro
		BearFair21NPC.MAYOR.getId(),            // Main
		BearFair21NPC.BEEKEEPER.getId(),        // Side
		BearFair21NPC.LUMBERJACK.getId(),        // Side
		BearFair21NPC.FISHERMAN2.getId(),        // Side
		BearFair21NPC.AXEL.getId(),            // MGN
		BearFair21NPC.PUGMAS_MAYOR.getId(),    // Pugmas
		BearFair21NPC.JOSE.getId(),            // HALLOWEEN
		BearFair21NPC.BRUCE.getId()            // SDU
	));
	private int activeTaskId = -1;
	private Set<Location> treasureChests = new HashSet<>();
	private boolean foundAllTreasureChests = false;

	// MAIN
	QuestStage questStage_Main = QuestStage.NOT_STARTED;
	Set<Integer> invitees = new HashSet<>();

	// MAIN - RECYCLE
	QuestStage questStage_Recycle = QuestStage.NOT_STARTED;
	JunkWeight junkWeight = JunkWeight.MAX;
	int recycledItems = 0;

	// MAIN - BEE KEEPER
	QuestStage questStage_BeeKeeper = QuestStage.NOT_STARTED;
	boolean hiveAccess = false;

	// MAIN - LUMBERJACK
	QuestStage questStage_Lumberjack = QuestStage.NOT_STARTED;

	// MGN
	QuestStage questStage_MGN = QuestStage.NOT_STARTED;
	boolean mgn_laptopScreen = false;
	boolean mgn_laptopMotherboard = false;

	boolean mgn_connectWiring = false;
	boolean mgn_unscrambledWiring = false;
	boolean mgn_setupRouter = false;

	boolean mgn_receivedAxelCall = false;
	boolean mgn_foundSpeaker = false;
	boolean mgn_boughtCar = false;
	boolean mgn_openedTrunk = false;

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
	boolean receivedBrikkies = false;
	Set<Integer> taughtNpcIds = new HashSet<>();
	Set<Location> featherLocations = new HashSet<>();

	private int mapId;

	public void addRecycledItems(int count) {
		this.recycledItems += count;

		if (this.recycledItems >= JunkWeight.MIN.getAmount() && questStage_Recycle != QuestStage.COMPLETE) {
			questStage_Recycle = QuestStage.COMPLETE;
			getNextStepNPCs().remove(BearFair21NPC.FISHERMAN2.getId());
			Tasks.wait(TickTime.SECOND.x(2), () -> Quests.giveKey(this));
		}

		this.junkWeight = junkWeight.update(this.recycledItems);
	}

	public boolean hasMet(int npcId) {
		return getMetNPCs().contains(npcId);
	}

	public void cancelActiveTask() {
		Tasks.cancel(this.activeTaskId);
		this.activeTaskId = -1;
	}
}
