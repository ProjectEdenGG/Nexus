package gg.projecteden.nexus.models.pugmas21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestLine;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.pugmas21.Advent21Config.AdventPresent;
import gg.projecteden.nexus.utils.GlowUtils;
import gg.projecteden.nexus.utils.GlowUtils.GlowColor;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerMovementUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.bukkit.Sound;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.api.common.utils.TimeUtils.shortDateFormat;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21.PREFIX;
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

	private Pugmas21QuestLine questLine;
	private boolean firstVisit = false;

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

		public transient final Map<Integer, ItemFrame> frames = new HashMap<>();

		private ItemFrame getItemFrame(AdventPresent present) {
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
			show(present);

			PlayerUtils.mailItem(getOnlinePlayer(), present.getItem().build(), null, WorldGroup.SURVIVAL);
			PlayerUtils.send(getOnlinePlayer(), PREFIX + "This present has been sent to your &esurvival &c/mail box");
			new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).receiver(getOnlinePlayer()).play();
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

			new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).receiver(getOnlinePlayer()).play();
		}

		public void teleportAsync(AdventPresent present) {
			getOnlinePlayer().teleportAsync(present.getLocation().toCenterLocation());
		}

		// This is breaking advent presents, the server/database somehow loses reference to them when calling this method
		@Deprecated
		public void locate(AdventPresent present) {
			PlayerMovementUtils.lookAt(getOnlinePlayer(), present.getLocation());
			glow(present);
		}

		@Deprecated
		public void glow(AdventPresent present) {
			ItemFrame itemFrame = getItemFrame(present);
			if (itemFrame == null)
				itemFrame = show(present);

			GlowUtils.GlowTask.builder()
				.duration(TickTime.SECOND.x(15))
				.entity(itemFrame.getBukkitEntity())
				.color(GlowColor.RED)
				.viewers(singletonList(getOnlinePlayer()))
				.start();
		}

		public ItemFrame show(AdventPresent present) {
			hide(present);
			return frames.compute(present.getDay(), ($1, $2) -> present.sendPacket(this));
		}

		public void hide(AdventPresent present) {
			ItemFrame itemFrame = getItemFrame(present);
			if (itemFrame == null)
				return;

			PacketUtils.entityDestroy(getOnlinePlayer(), itemFrame.getId());
		}

	}

}
