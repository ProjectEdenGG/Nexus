package gg.projecteden.nexus.models.pugmas21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.pugmas21.Advent21Config.AdventPresent;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks.GlowTask;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.decoration.EntityItemFrame;
import org.inventivetalent.glow.GlowAPI;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21.PREFIX;
import static gg.projecteden.utils.TimeUtils.shortDateFormat;
import static java.util.Collections.singletonList;

@Data
@Entity(value = "pugmas21_user", noClassnameStored = true)
@NoArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Pugmas21User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	@Getter(AccessLevel.PRIVATE)
	private Advent21User advent;

	public Advent21User advent() {
		if (advent == null)
			advent = new Advent21User(uuid);

		return advent;
	}

	@Data
	@NoArgsConstructor
	@RequiredArgsConstructor
	public static class Advent21User implements PlayerOwnedObject {
		@NonNull
		private UUID uuid;
		private Set<Integer> collected = new HashSet<>();
		private Set<Integer> found = new HashSet<>();

		public transient final Map<Integer, EntityItemFrame> frames = new HashMap<>();

		private EntityItemFrame getItemFrame(AdventPresent present) {
			return frames.get(present.getDay());
		}

		public void tryCollect(AdventPresent present) {
			try {
				collected(present);
			} catch (InvalidInputException ex) {
				sendMessage(new JsonBuilder(PREFIX + "&c").next(ex.getJson()));
				found(present.getDay());
			}
		}

		public boolean hasCollected(LocalDate date) {
			if (!Pugmas21.isAdvent(date))
				throw new InvalidInputException("Advent date " + shortDateFormat(date) + " is invalid");

			return hasCollected(date.getDayOfMonth());
		}

		public boolean hasCollected(AdventPresent present) {
			return hasCollected(present.getDay());
		}

		public boolean hasCollected(int day) {
			return collected.contains(day);
		}

		public void collected(AdventPresent present) {
			if (hasCollected(present))
				throw new InvalidInputException("You already collected this present");

			if (Pugmas21.isPugmasOrAfter()) {
				if (present.getDay() == 25 && collected.size() != 24)
					throw new InvalidInputException("You need to find the rest of the presents to open this one");
			} else if (present.getDay() != Pugmas21.TODAY.getDayOfMonth())
				throw new InvalidInputException("This present is for day &e#" + present.getDay());

			collected.add(present.getDay());
			found.add(present.getDay());
			sendMessage(PREFIX + "You found present &e#" + present.getDay() + "&3!");
			PlayerUtils.giveItem(getOnlinePlayer(), present.getItem().build());
			// TODO Sound
		}

		public boolean hasFound(AdventPresent present) {
			return hasFound(present.getDay());
		}

		public boolean hasFound(int day) {
			return found.contains(day);
		}

		public void found(int day) {
			if (hasFound(day))
				return;

			found.add(day);
			sendMessage(PREFIX + "Location of present &e#" + day + " &3saved. " +
				"View with the &eAdvent Calendar menu &3or &c/pugmas advent waypoint " + day);
			// TODO Sound
		}

		public void teleportAsync(AdventPresent present) {
			getOnlinePlayer().teleportAsync(present.getLocation());
		}

		public void locate(AdventPresent present) {
			LocationUtils.lookAt(getOnlinePlayer(), present.getLocation());
			glow(present);
		}

		public void glow(AdventPresent present) {
			EntityItemFrame itemFrame = getItemFrame(present);
			if (itemFrame == null)
				itemFrame = show(present);

			GlowTask.builder()
				.duration(TickTime.SECOND.x(15))
				.entity(itemFrame.getBukkitEntity())
				.color(GlowAPI.Color.RED)
				.viewers(singletonList(getOnlinePlayer()))
				.start();
		}

		public EntityItemFrame show(AdventPresent present) {
			EntityItemFrame itemFrame = getItemFrame(present);
			if (itemFrame != null)
				PacketUtils.entityDestroy(getOnlinePlayer(), itemFrame.getId());

			itemFrame = present.sendPacket(this);
			frames.put(present.getDay(), itemFrame);
			return itemFrame;
		}

	}

}
