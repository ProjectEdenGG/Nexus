package me.pugabyte.nexus.models.bearfair21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import eden.utils.TimeUtils.Time;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2021.bearfair21.Quests;
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

import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC.*;

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
	private Set<Integer> metNPCs = new HashSet<>();
	// TODO BF21: Set this system up
	private Set<Integer> nextStepNPCs = new HashSet<>(Arrays.asList(
			ORGANIZER.getId(), // Main
		BEEKEEPER.getId(), // Side
		LUMBERJACK.getId(), // Side
		FISHERMAN2.getId(), // Side
		AXEL.getId(), // MGN
		PUGMAS_MAYOR.getId(), // Pugmas
		JOSE.getId(), // HALLOWEEN
		BRUCE.getId() // SDU
	));
	private int activeTaskId = -1;

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

	boolean mgn_foundSpeaker = false;
	boolean mgn_boughtCar = false;

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

		if (this.recycledItems >= JunkWeight.MIN.getAmount() && questStage_Recycle != QuestStage.COMPLETE) {
			questStage_Recycle = QuestStage.COMPLETE;
			Tasks.wait(Time.SECOND.x(2), () -> Quests.giveKey(this));
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
