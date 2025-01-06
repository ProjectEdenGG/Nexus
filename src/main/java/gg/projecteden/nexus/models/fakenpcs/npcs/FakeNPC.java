package gg.projecteden.nexus.models.fakenpcs.npcs;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.fakenpc.FakeNPCPacketUtils;
import gg.projecteden.nexus.features.fakenpc.FakeNPCUtils;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.FakeNPCConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.fakenpcs.config.FakeNPCConfig;
import gg.projecteden.nexus.models.fakenpcs.npcs.Trait.UpdateType;
import gg.projecteden.nexus.models.fakenpcs.users.FakeNPCUser;
import gg.projecteden.nexus.models.fakenpcs.users.FakeNPCUserService;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@dev.morphia.annotations.Entity(value = "fake_npc")
@Converters({UUIDConverter.class, LocationConverter.class, FakeNPCConverter.class})
public abstract class FakeNPC implements DatabaseObject {
	@Id
	@NonNull
	@EqualsAndHashCode.Include
	protected UUID uuid;
	protected UUID owner;
	protected int id;
	protected FakeNPCType type;
	protected Location location;
	protected boolean spawned;
	protected Hologram hologram; // TODO: move to trait

	// TODO: remove transient: fix invalid BSON error
	protected transient Map<Class<? extends Trait>, Trait> traits = new HashMap<>();

	protected transient Entity entity;

	// TODO: Switch to LookCloseTrait
//	protected boolean lookClose;
//	protected int lookCloseRadius;

	public FakeNPC(FakeNPCType type, OfflinePlayer owner, Location location, String name) {
		this.uuid = UUID.randomUUID();
		this.owner = owner.getUniqueId();
		this.id = FakeNPCConfig.getNextId();
		this.type = type;
		this.location = location;

		addDefaultTraits();

		// TODO: Switch to Hologram Trait
		this.hologram = new Hologram(name);

		// TODO: Switch to LookCloseTrait
//		this.lookClose = false;
//		this.lookCloseRadius = 10;
		//

		createEntity();
	}

	public void addDefaultTraits() {
		for (FakeNPCTraitType traitType : FakeNPCTraitType.values()) {
			if (traitType.isDefault() && !hasTrait(traitType))
				addTrait(traitType.create());
		}
	}

	// OVERRIDES
	public void createEntity() {
		if (this.entity != null) {
			this.entity.setCustomNameVisible(false);
			this.spawned = true;
		}

		createHologramLines();
	}

	public void spawn() {
		setSpawned(true);
		updateTraits(UpdateType.SPAWN);

		for (FakeNPCUser user : new FakeNPCUserService().getOnline())
			if (user.canSeeNPC(this))
				user.show(this);
	}

	public void despawn() {
		setSpawned(false);
		updateTraits(UpdateType.DESPAWN);
	}

	public void respawn() {
		despawn();
		Tasks.wait(1, this::spawn);
	}

	public void teleport(Location location) {
		setLocation(location);
		NMSUtils.teleport(getEntity(), getLocation());
		respawn();
	}

	// INFO

	public org.bukkit.entity.Entity getBukkitEntity() {
		return getEntity().getBukkitEntity();
	}

	public FakeNPCUser getOwningUser() {
		return FakeNPCUser.of(owner);
	}

	public World getWorld() {
		return getLocation().getWorld();
	}

	public void setSpawned(boolean spawned) {
		this.spawned = spawned;
		this.hologram.spawned = spawned;
	}

	public String getName() {
		return getHologram().getName();
	}

	public void setName(String name) {
		getHologram().setName(name);
		refreshHologramLines();
	}

	public void setNameVisible(boolean visible) {
		getHologram().setNameVisible(visible);
	}

	public boolean isNameVisible() {
		return getHologram().isNameVisible();
	}

	// TRAITS

	private void updateTraits(UpdateType type) {
		for (Class<? extends Trait> clazz : getTraits().keySet()) {
			Trait trait = getTrait(clazz);
			trait.update(type);
		}
	}

	public void addTrait(Trait trait) {
		if (trait == null)
			throw new InvalidInputException("Cannot add null trait to FakeNPC " + FakeNPCUtils.getNameAndId(this));

		traits.put(trait.getClass(), trait);
		if (trait.getNpcUUID() == null)
			trait.linkTo(this);

		if (isSpawned())
			trait.onSpawn();
	}

	public boolean hasTrait(FakeNPCTraitType type) {
		return getTrait(type) != null;
	}

	public <T extends Trait> T getTrait(FakeNPCTraitType type) {
		return getTrait(type.getClazz());
	}

	public <T extends Trait> T getTrait(Class<? extends Trait> clazz) {
		return (T) traits.get(clazz);
	}

	public <T extends Trait> T getOrAddTrait(FakeNPCTraitType type) {
		Trait trait = getTrait(type);
		if (trait == null) {
			trait = type.create();
			addTrait(trait);
		}

		return (T) trait;
	}

	// HOLOGRAMS

	public void refreshHologramLines() {
		if (hologram != null)
			deleteHologramLines();

		createHologramLines();
		FakeNPCPacketUtils.updateHologram(this);
	}

	public void createHologramLines() {
		if (getEntity() == null)
			return;

		deleteHologramLines();

		List<ArmorStand> armorStands = new ArrayList<>();
		for (int i = 0; i < Hologram.MAX_LINES; i++)
			armorStands.add(NMSUtils.createHologram(NMSUtils.toNMS(getBukkitEntity().getWorld())));

		this.hologram.setArmorStandList(armorStands);
	}

	public void deleteHologramLines() {
		FakeNPCPacketUtils.despawnHologram(this);

		for (ArmorStand armorStand : hologram.getArmorStandList())
			armorStand.remove(RemovalReason.DISCARDED);
	}

	// TODO: line 1 not showing up
	@Data
	@NoArgsConstructor
	public static class Hologram {
		public static final int MAX_LINES = 4;
		private UUID npcUUID;
		private transient List<ArmorStand> armorStandList = new ArrayList<>();
		private String name;
		private boolean nameVisible;
		private List<String> lines = new ArrayList<>();
		private boolean spawned;
		private VisibilityType visibilityType;
		private Integer visibilityRadius = 10;

		@AllArgsConstructor
		public enum VisibilityType {
			HIDDEN(false, null),
			ALWAYS(true, 0),
			ALWAYS_AFTER_INTRODUCTION(true, 10),
			WITHIN_RADIUS(true, 10),
			WITHIN_RADIUS_AFTER_INTRODUCTION(true, 10),
			;

			@Getter
			private final boolean visible;
			@Getter
			private final Integer defaultRadius;

			public boolean applies(FakeNPC fakeNPC, FakeNPCUser user) {
				if (!FakeNPCUtils.isInSameWorld(user, fakeNPC))
					return false;

				Location playerLoc = user.getOnlinePlayer().getLocation();
				Location npcLoc = fakeNPC.getLocation();
				int radius = fakeNPC.getHologram().getVisibilityRadius();

				boolean withinRadius = Distance.distance(playerLoc, npcLoc).lt(radius);
				boolean hasInteracted = user.getInteractedNPCs().contains(fakeNPC);

				return switch (this) {
					case HIDDEN -> false;
					case ALWAYS -> true;
					case ALWAYS_AFTER_INTRODUCTION -> hasInteracted;
					case WITHIN_RADIUS -> withinRadius;
					case WITHIN_RADIUS_AFTER_INTRODUCTION -> withinRadius && hasInteracted;
				};
			}
		}

		public Hologram(String name) {
			this(name, new ArrayList<>(), true, VisibilityType.ALWAYS);
		}

		public Hologram(String name, List<String> lines, boolean spawned, VisibilityType visibilityType) {
			this(name, lines, spawned, visibilityType, visibilityType.getDefaultRadius());
		}

		public Hologram(String name, List<String> lines, boolean spawned, VisibilityType visibilityType, int radius) {
			this.name = name;
			this.lines = lines;
			this.spawned = spawned;
			this.visibilityType = visibilityType;
			this.visibilityRadius = radius;
		}

		public void setLines(List<String> newLines) {
			// TODO: despawn hologram if newlines is empty or null

			if (newLines.size() > MAX_LINES)
				newLines = newLines.stream().limit(MAX_LINES).collect(Collectors.toList());

			this.lines = newLines;
		}

		public void setLine(int ndx, String line) {
			List<String> _lines = new ArrayList<>(getLines());
			_lines.set(ndx, line);

			this.lines = _lines;
		}

		public void setVisibilityType(VisibilityType type) {
			this.visibilityType = type;
			this.spawned = type.isVisible();
		}
	}
}
