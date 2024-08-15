package gg.projecteden.nexus.models.pugmas24;

import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Data;
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

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Advent24User implements PlayerOwnedObject {
	@NonNull
	private UUID uuid;

	private Set<Integer> collected = new HashSet<>();
	private Set<Integer> found = new HashSet<>();

	public transient final Map<Integer, ItemFrame> frames = new HashMap<>();

	private ItemFrame getItemFrame(Advent24Present present) {
		return frames.get(present.getDay());
	}

	public void tryCollect(Advent24Present present) {
		try {
			collected(present);
		} catch (InvalidInputException ex) {
			sendMessage(new JsonBuilder(Pugmas24.PREFIX + "&c").next(ex.getJson()));
			found(present.getDay());
		}
	}

	public boolean hasCollected(LocalDate date) {
		return hasCollected(date.getDayOfMonth());
	}

	public boolean hasCollected(Advent24Present present) {
		return hasCollected(present.getDay());
	}

	public boolean hasCollected(int day) {
		return collected.contains(day);
	}

	public void collected(Advent24Present present) {
		if (hasCollected(present))
			throw new InvalidInputException("You already collected this present");

		if (Pugmas24.get().is25thOrAfter()) {
			if (present.getDay() == 25 && collected.size() != 24)
				throw new InvalidInputException("You need to find the rest of the presents to open this one");
		} else if (present.getDay() != LocalDate.now().getDayOfMonth())
			throw new InvalidInputException("This present is for day &e#" + present.getDay());

		collected.add(present.getDay());
		found.add(present.getDay());
		sendMessage(Pugmas24.PREFIX + "You found present &e#" + present.getDay() + "&3!");
		show(present);

		PlayerUtils.mailItem(getOnlinePlayer(), present.getItem().build(), null, WorldGroup.SURVIVAL);
		PlayerUtils.send(getOnlinePlayer(), Pugmas24.PREFIX + "This present has been sent to your &esurvival &c/mail box");
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).receiver(getOnlinePlayer()).play();
	}

	public boolean hasFound(Advent24Present present) {
		return hasFound(present.getDay());
	}

	public boolean hasFound(int day) {
		return found.contains(day);
	}

	public void found(int day) {
		if (hasFound(day))
			return;

		found.add(day);
		sendMessage(Pugmas24.PREFIX + "Location of present &e#" + day + " &3saved. " +
				"View with the &eAdvent Calendar menu &3or &c/pugmas advent waypoint " + day);

		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).receiver(getOnlinePlayer()).play();
	}

	public void teleportAsync(Advent24Present present) {
		getOnlinePlayer().teleportAsync(present.getLocation().toCenterLocation());
	}

	public ItemFrame show(Advent24Present present) {
		hide(present);
		return frames.compute(present.getDay(), ($1, $2) -> present.sendPacket(this));
	}

	public void hide(Advent24Present present) {
		ItemFrame itemFrame = getItemFrame(present);
		if (itemFrame == null)
			return;

		PacketUtils.entityDestroy(getOnlinePlayer(), itemFrame.getId());
	}
}
