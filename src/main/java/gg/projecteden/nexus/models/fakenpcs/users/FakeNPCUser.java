package gg.projecteden.nexus.models.fakenpcs.users;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.fakenpc.FakeNPCPacketUtils;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPCService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toSet;

@Data
@Entity(value = "fake_npc_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class FakeNPCUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private UUID selected;
	private Set<UUID> visible = new HashSet<>();
	private Set<UUID> visibleHolograms = new HashSet<>();

	public static FakeNPCUser of(HasUniqueId uuid) {
		return of(uuid.getUniqueId());
	}

	public static FakeNPCUser of(UUID uuid) {
		return new FakeNPCUserService().get(uuid);
	}

	public void setSelectedNPC(FakeNPC npc) {
		selected = npc.getUuid();
	}

	public FakeNPC getSelectedNPC() {
		if (selected == null)
			return null;

		return new FakeNPCService().get(selected);
	}

	public Set<FakeNPC> getVisibleNPCs() {
		final FakeNPCService service = new FakeNPCService();
		return visible.stream().map(service::get).collect(toSet());
	}

	public boolean canSeeNPC(FakeNPC fakeNPC) {
		return visible.contains(fakeNPC.getUuid());
	}

	public boolean canSeeHologram(FakeNPC fakeNPC) {
		return visibleHolograms.contains(fakeNPC.getUuid());
	}

	public void show(FakeNPC fakeNPC) {
		visible.add(fakeNPC.getUuid());
		if (isOnline())
			FakeNPCPacketUtils.spawnFor(fakeNPC, getOnlinePlayer());
	}

	public void hide(FakeNPC fakeNPC) {
		visible.remove(fakeNPC.getUuid());
		if (isOnline())
			FakeNPCPacketUtils.despawnFor(fakeNPC, getOnlinePlayer());

		hideHologram(fakeNPC);
	}

	public void showHologram(FakeNPC fakeNPC) {
		visibleHolograms.add(fakeNPC.getUuid());
		if (isOnline())
			FakeNPCPacketUtils.spawnHologramFor(fakeNPC, getOnlinePlayer());
	}

	public void hideHologram(FakeNPC fakeNPC) {
		visibleHolograms.remove(fakeNPC.getUuid());
		if (isOnline())
			FakeNPCPacketUtils.despawnHologramFor(fakeNPC, getOnlinePlayer());
	}

}
