package gg.projecteden.nexus.models.fakenpcs.npcs;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.fakenpc.FakeNPCPacketUtils;
import gg.projecteden.nexus.features.fakenpc.FakeNPCType;
import gg.projecteden.nexus.features.fakenpc.FakeNPCUtils;
import gg.projecteden.nexus.models.fakenpcs.config.FakeNPCConfig;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC.Hologram.VisibilityType;
import gg.projecteden.nexus.models.fakenpcs.npcs.traits.Trait;
import gg.projecteden.nexus.models.fakenpcs.users.FakeNPCUser;
import gg.projecteden.nexus.models.fakenpcs.users.FakeNPCUserService;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@dev.morphia.annotations.Entity(value = "fake_npc")
@Converters(UUIDConverter.class)
public class FakeNPC implements DatabaseObject {
	@Id
	@NonNull
	@EqualsAndHashCode.Include
	protected UUID uuid;
	protected UUID owner;
	protected int id;
	protected FakeNPCType type;
	protected Location location;
	protected boolean spawned;
	protected boolean onReload;
	protected Hologram hologram;

	protected Map<Class<? extends Trait>, Trait> traits = new HashMap<>();

	protected transient Entity entity;

	// TODO: Switch to LookCloseTrait
	protected boolean lookClose;
	protected int lookCloseRadius;

	public FakeNPC(FakeNPCType type, OfflinePlayer owner, Location location, String name) {
		this.uuid = UUID.randomUUID();
		this.owner = owner.getUniqueId();
		this.id = FakeNPCConfig.getNextId();
		this.type = type;
		this.location = location;

		this.hologram = new Hologram(List.of(name), true, VisibilityType.ALWAYS);

		// TODO: Switch to LookCloseTrait
		this.lookClose = false;
		this.lookCloseRadius = 10;
		//

		// TODO: addDefaultTraits()

		createEntity();
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
	}

	public void despawn() {
		setSpawned(false);

		for (FakeNPCUser user : new FakeNPCUserService().getOnline())
			if (user.canSeeNPC(this))
				user.hide(this);
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

	public String getName() {
		return getHologram().getLines().get(0);
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

	public void setName(String name) {
		getHologram().setLine(0, name);
		refreshHologramLines();
	}

	// TRAITS

	public void addTrait(Class<? extends Trait> clazz) {
		addTrait(getTraitFor(clazz));
	}

	public void addTrait(Trait trait) {
		if (trait == null) {
			Nexus.warn("Cannot add null trait to FakeNPC " + FakeNPCUtils.getNameAndId(this));
			return;
		}

		if (trait.getNpc() == null)
			trait.linkTo(this);

		if (isSpawned())
			trait.onSpawn();
	}

	public <T extends Trait> T getTrait(Class<T> clazz) {
		Trait trait = traits.get(clazz);
		if (trait == null) {
			trait = getTraitFor(clazz);
			addTrait(trait);
		}
		return clazz.cast(trait);
	}

	public Trait getTraitFor(Class<? extends Trait> clazz) {
		return null; // TODO
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
		for (int i = 0; i < getHologram().getLines().size(); i++)
			armorStands.add(NMSUtils.createHologram(getEntity().getLevel()));

		this.hologram.setArmorStandList(armorStands);
	}

	public void deleteHologramLines() {
		FakeNPCPacketUtils.despawnHologram(this);

		for (ArmorStand armorStand : hologram.getArmorStandList())
			armorStand.remove(RemovalReason.DISCARDED);
	}

	@Data
	@NoArgsConstructor
	public static class Hologram {
		private transient List<ArmorStand> armorStandList = new ArrayList<>();
		private List<String> lines = new ArrayList<>();
		private boolean spawned;
		private VisibilityType visibilityType;
		private Integer visibilityRadius = 10;

		@AllArgsConstructor
		public enum VisibilityType {
			HIDDEN(false, null),
			ALWAYS(true, 0),
			AFTER_INTRODUCTION(true, 10), // TODO
			WITHIN_RADIUS(true, 10),
			WITHIN_RADIUS_AFTER_INTRODUCTION(true, 10),  // TODO
			;

			@Getter
			private final boolean visible;
			@Getter
			private final Integer defaultRadius;

			public boolean applies(FakeNPC fakeNPC, FakeNPCUser user) {
				if (!FakeNPCUtils.isInSameWorld(user, fakeNPC))
					return false;

				if (this == HIDDEN) return false;
				if (this == ALWAYS) return true;

				// TODO: check if player has interacted with the npc previously

				Location playerLoc = user.getOnlinePlayer().getLocation();
				Location npcLoc = fakeNPC.getLocation();
				int radius = fakeNPC.getHologram().getVisibilityRadius();

				return Distance.distance(playerLoc, npcLoc).lt(radius);
			}
		}

		public Hologram(List<String> lines) {
			this(lines, true, VisibilityType.ALWAYS);
		}

		public Hologram(List<String> lines, boolean spawned, VisibilityType visibilityType) {
			this(lines, spawned, visibilityType, visibilityType.getDefaultRadius());
		}

		public Hologram(List<String> lines, boolean spawned, VisibilityType visibilityType, int radius) {
			this.lines = lines;
			this.spawned = spawned;
			this.visibilityType = visibilityType;
			this.visibilityRadius = radius;
		}

		public void setLines(List<String> newLines) {
			List<String> _lines = new ArrayList<>();
			_lines.add(this.lines.get(0));

			if (newLines.size() > 4)
				newLines = newLines.stream().limit(4).collect(Collectors.toList());

			_lines.addAll(newLines);

			this.lines = new ArrayList<>(_lines);
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
