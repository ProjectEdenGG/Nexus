package gg.projecteden.nexus.models.staffhall;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Data
@Entity(value = "staff_hall_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class StaffHallConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<StaffHallRankGroup, List<Integer>> npcIds = new ConcurrentHashMap<>();

	@NotNull
	public List<Integer> getNpcIds(StaffHallRankGroup group) {
		return npcIds.computeIfAbsent(group, $ -> new ArrayList<>());
	}

	public int getNpcId(StaffHallRankGroup group, int index) {
		final List<Integer> npcs = getNpcIds(group);
		if (npcs.size() < (index - 1))
			throw new InvalidInputException(StringUtils.camelCase(group) + " only has " + npcs.size() + " npcs");
		return npcs.get(index);
	}

	public void add(StaffHallRankGroup group, int npcId) {
		getNpcIds(group).add(npcId);
	}

	public void add(StaffHallRankGroup group, int npcId, int index) {
		getNpcIds(group).add(index - 1, npcId);
	}

	public void remove(StaffHallRankGroup group, int npcId) {
		getNpcIds(group).remove((Integer) npcId);
	}

	@AllArgsConstructor
	public enum StaffHallRankGroup {
		SENIOR_STAFF(rank -> rank.gte(Rank.OPERATOR)),
		MODERATORS(rank -> rank == Rank.MODERATOR),
		BUILDERS(rank -> rank == Rank.BUILDER || rank == Rank.ARCHITECT),
		;

		public Predicate<Rank> predicate;

		public static StaffHallRankGroup of(Rank rank) {
			for (StaffHallRankGroup group : values())
				if (group.predicate.test(rank))
					return group;

			return null;
		}
	}

}
