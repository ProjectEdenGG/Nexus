package gg.projecteden.nexus.features.fakenpc;

import gg.projecteden.nexus.features.fakenpc.FakeNPC.Hologram.VisibilityType;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FakeNPC {
	@EqualsAndHashCode.Include
	private @NonNull UUID uuid;
	private UUID ownerUUID;
	private int id;
	private FakeNPCType type;
	private Location location;
	private boolean spawned;
	private Hologram hologram;

	private Entity entity;
	private boolean lookClose;
	private int lookCloseRadius;

	public FakeNPC(FakeNPCType type, Player owner) {
		this(type, owner, owner.getLocation(), Name.of(owner));
	}

	public FakeNPC(FakeNPCType type, OfflinePlayer owner, Location location, String name) {
		this.uuid = UUID.randomUUID();
		this.ownerUUID = owner.getUniqueId();
		this.id = -1;
		this.type = type;
		this.location = location;
		this.spawned = true;
		init();

		this.hologram = new Hologram(List.of(name), true, VisibilityType.ALWAYS);
		this.createHologram();

		if (this.entity != null && !Nullables.isNullOrEmpty(this.hologram.lines))
			this.entity.setCustomNameVisible(false);

		this.lookClose = false;
		this.lookCloseRadius = 10;
	}

	public void init() {}

	public void spawn() {
		setSpawned(true);
	}

	public void despawn() {
		setSpawned(false);

		FakeNPCManager.getPlayerVisibleNPCs().keySet().forEach(uuid -> {
			if (FakeNPCUtils.isNPCVisibleFor(this, uuid)) {
				FakeNPCManager.getPlayerVisibleNPCs().get(uuid).remove(this);
				FakeNPCPacketUtils.despawnFor(this, uuid);
			}
		});
	}

	public void respawn() {
		despawn();
		Tasks.wait(1, this::spawn);
	}

	public void delete() {
		despawn();
		FakeNPCManager.getNPCList().remove(this);
	}

	public void teleport(Location location) {
		setLocation(location);
		NMSUtils.teleport(getEntity(), getLocation());
		respawn();
	}

	public String getName() {
		return getHologram().getLines().get(0);
	}

	public OfflinePlayer getOwner() {
		return PlayerUtils.getPlayer(getOwnerUUID());
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
		refreshHologram();
	}

	public boolean canSee(org.bukkit.entity.Entity entity) {
		if (!entity.getWorld().equals(getLocation().getWorld()))
			return false;

		if (Distance.distance(getLocation(), entity.getLocation()).gt(getLookCloseRadius()))
			return false;

		if (getEntity().getBukkitEntity() instanceof LivingEntity livingEntity)
			return livingEntity.hasLineOfSight(entity);

		return true;
	}

	public void createHologram() {
		if (getEntity() == null)
			return;

		List<ArmorStand> armorStands = new ArrayList<>();
		for (int i = 0; i < this.getHologram().getLines().size(); i++)
			armorStands.add(NMSUtils.createHologram(getEntity().getLevel()));

		this.hologram.setArmorStandList(armorStands);
	}

	public void refreshHologram() {
		FakeNPCPacketUtils.despawnHologram(this);

		for (ArmorStand armorStand : hologram.getArmorStandList())
			armorStand.remove(RemovalReason.DISCARDED);

		createHologram();
		FakeNPCPacketUtils.updateHologram(this);
	}

	@Data
	@NoArgsConstructor
	public static class Hologram {
		private List<ArmorStand> armorStandList = new ArrayList<>();
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

			public boolean applies(FakeNPC fakeNPC, Player player) {
				if (!FakeNPCUtils.isInSameWorld(player, fakeNPC))
					return false;

				if (this == HIDDEN) return false;
				if (this == ALWAYS) return true;

				// TODO: check if player has interacted with the npc previously

				Location playerLoc = player.getLocation();
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
