package gg.projecteden.nexus.models.pugmas25;

import com.destroystokyo.paper.ParticleBuilder;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity.ClientSideEntityType;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Fishing.Pugmas25AnglerLoot;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Snowmen.Pugmas25Snowman;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Waystones.Pugmas25Waystone;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "pugmas25_user", noClassnameStored = true)
@NoArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Pugmas25User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private boolean readyToVisit = false;
	private boolean visited = false;

	private boolean unlockedCabin = false;
	private Location spawnLocation = Pugmas25.get().warp;

	private boolean receivedAnglerQuestInstructions = false;
	private Pugmas25AnglerLoot receivedAnglerQuestFish = null;
	private boolean caughtAnglerQuestLoot = false;
	private boolean completedAnglerQuest = false;
	private int completedAnglerQuests = 0;

	private boolean receivedAeronautInstructions = false;
	private boolean balloonSchemExists = false;

	private int slotMachineRolls = 0;
	private int slotMachineRewards = 0;

	private boolean startedMiniGolf;

	private Set<Pugmas25Waystone> foundWaystones = new HashSet<>();
	private Set<Pugmas25Snowman> decoratedSnowmen = new HashSet<>();
	private Set<Location> foundNutCrackers = new HashSet<>();

	@Getter(AccessLevel.PRIVATE)
	private Advent25User advent;

	public Advent25User advent() {
		if (advent == null)
			advent = new Advent25User(uuid);

		return advent;
	}

	public void unlockWaystone(Pugmas25Waystone waystone) {
		sendMessage(Pugmas25.PREFIX + "Unlocked waystone: &b" + StringUtils.camelCase(waystone));
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_CHIME).receiver(getPlayer()).play();
		foundWaystones.add(waystone);

		new ParticleBuilder(Particle.WITCH)
			.location(waystone.getFrameLoc().toCenterLocation().add(0, 0.5, 0))
			.count(25)
			.offset(0.5, 0.5, 0.5)
			.receivers(getOnlinePlayer())
			.spawn();

		ClientSideItemFrame itemFrame = (ClientSideItemFrame) ClientSideConfig.getEntities(waystone.getFrameLoc(), ClientSideEntityType.ITEM_FRAME, 1).stream().findFirst().orElse(null);
		if (itemFrame != null)
			ClientSideUser.of(uuid).refresh(itemFrame.getUuid());
	}

	public void decorateSnowman(Pugmas25Snowman snowman) {
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_CHIME).receiver(getPlayer()).play();
		decoratedSnowmen.add(snowman);

		new ParticleBuilder(Particle.HAPPY_VILLAGER)
			.location(snowman.getFrameLoc().toCenterLocation().add(0, 0.5, 0))
			.count(25)
			.offset(0.5, 0.5, 0.5)
			.receivers(getOnlinePlayer())
			.spawn();

		ClientSideItemFrame itemFrame = (ClientSideItemFrame) ClientSideConfig.getEntities(snowman.getFrameLoc(), ClientSideEntityType.ITEM_FRAME, 1).stream().findFirst().orElse(null);
		if (itemFrame != null)
			ClientSideUser.of(uuid).refresh(itemFrame.getUuid());
	}

	public void resetAnglerQuest() {
		this.caughtAnglerQuestLoot = false;
		this.completedAnglerQuest = false;
	}

	public void incrementCompletedAnglerQuests() {
		this.completedAnglerQuests += 1;
	}

	public void incrementSlotMachineRolls() {
		this.slotMachineRolls += 1;
	}

	public void incrementSlotMachineRewards() {
		this.slotMachineRewards += 1;
	}
}
