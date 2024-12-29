package gg.projecteden.nexus.models.fakenpcs.users;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.fakenpc.FakeNPCPacketUtils;
import gg.projecteden.nexus.features.fakenpc.FakeNPCUtils.SkinProperties;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPCService;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

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
	private boolean selecting;
	private Map<UUID, FakeNPCUserSettings> npcSettings = new HashMap<>();

	@Data
	@NoArgsConstructor
	public static class FakeNPCUserSettings {
		boolean visible = false;
		boolean visibleHologram = false;
		boolean interacted = false;
		SkinProperties skinProperties = null;
		List<String> hologramLines = new ArrayList<>();
	}

	public static FakeNPCUser of(HasUniqueId uuid) {
		return of(uuid.getUniqueId());
	}

	public static FakeNPCUser of(UUID uuid) {
		return new FakeNPCUserService().get(uuid);
	}

	public boolean hasSelectedNPC() {
		return getSelected() != null;
	}

	public void setSelectedNPC(FakeNPC npc) {
		selected = npc.getUuid();
	}

	public FakeNPC getSelectedNPC() {
		if (!hasSelectedNPC())
			return null;

		return new FakeNPCService().get(selected);
	}

	public boolean hasInteracted(FakeNPC fakeNPC) {
		return getInteractedNPCs().contains(fakeNPC);
	}

	public Set<FakeNPC> getInteractedNPCs() {
		final FakeNPCService service = new FakeNPCService();
		return npcSettings.keySet().stream()
			.filter(uuid -> npcSettings.get(uuid).isInteracted())
			.map(service::get).collect(Collectors.toSet());
	}

	public Set<FakeNPC> getVisibleNPCs() {
		final FakeNPCService service = new FakeNPCService();
		return npcSettings.keySet().stream()
			.filter(uuid -> npcSettings.get(uuid).isVisible())
			.map(service::get).collect(Collectors.toSet());
	}

	public Set<FakeNPC> getVisibleHolograms() {
		final FakeNPCService service = new FakeNPCService();
		return npcSettings.keySet().stream()
			.filter(uuid -> npcSettings.get(uuid).isVisibleHologram())
			.map(service::get).collect(Collectors.toSet());
	}

	public boolean canSeeNPC(FakeNPC fakeNPC) {
		return getVisibleNPCs().contains(fakeNPC);
	}

	public boolean canSeeHologram(FakeNPC fakeNPC) {
		return getVisibleHolograms().contains(fakeNPC);
	}

	public void show(FakeNPC fakeNPC) {
		FakeNPCUserSettings settings = npcSettings.getOrDefault(fakeNPC.getUuid(), new FakeNPCUserSettings());
		settings.setVisible(true);
		settings.setVisibleHologram(true);
		npcSettings.put(fakeNPC.getUuid(), settings);

		if (isOnline())
			FakeNPCPacketUtils.spawnFor(fakeNPC, getOnlinePlayer());
	}

	public void hide(FakeNPC fakeNPC) {
		FakeNPCUserSettings settings = npcSettings.getOrDefault(fakeNPC.getUuid(), new FakeNPCUserSettings());
		settings.setVisible(false);
		settings.setVisibleHologram(false);
		npcSettings.put(fakeNPC.getUuid(), settings);

		if (isOnline())
			FakeNPCPacketUtils.despawnFor(fakeNPC, getOnlinePlayer());

		hideHologram(fakeNPC);
	}

	public void showHologram(FakeNPC fakeNPC) {
		FakeNPCUserSettings settings = npcSettings.getOrDefault(fakeNPC.getUuid(), new FakeNPCUserSettings());
		settings.setVisibleHologram(true);
		npcSettings.put(fakeNPC.getUuid(), settings);

		if (isOnline())
			FakeNPCPacketUtils.spawnHologramFor(fakeNPC, getOnlinePlayer());
	}

	public void hideHologram(FakeNPC fakeNPC) {
		FakeNPCUserSettings settings = npcSettings.getOrDefault(fakeNPC.getUuid(), new FakeNPCUserSettings());
		settings.setVisibleHologram(false);
		npcSettings.put(fakeNPC.getUuid(), settings);

		if (isOnline())
			FakeNPCPacketUtils.despawnHologramFor(fakeNPC, getOnlinePlayer());
	}

}
