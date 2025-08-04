package gg.projecteden.nexus.models.pugmas25;

import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.pugmas25.Advent25Present.Advent25PresentStatus;
import gg.projecteden.nexus.utils.JsonBuilder;
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

import static gg.projecteden.nexus.models.pugmas25.Advent25Present.Advent25PresentStatus.AVAILABLE;
import static gg.projecteden.nexus.models.pugmas25.Advent25Present.Advent25PresentStatus.LOCKED;
import static gg.projecteden.nexus.models.pugmas25.Advent25Present.Advent25PresentStatus.MISSED;
import static gg.projecteden.nexus.models.pugmas25.Advent25Present.Advent25PresentStatus.OPENED;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Advent25User implements PlayerOwnedObject {
	@NonNull
	private UUID uuid;

	private Set<Integer> collected = new HashSet<>();
	private Set<Integer> found = new HashSet<>();

	public transient final Map<Integer, ItemFrame> frames = new HashMap<>();

	public static void refreshAllPlayers() {
		Pugmas25UserService userService = new Pugmas25UserService();
		Pugmas25.get().getOnlinePlayers().forEach(player -> {
			Pugmas25User user = userService.get(player);
			user.advent().refreshAll();
		});
	}

	public void refreshAll() {
		for (Advent25Present present : Advent25Config.get().getPresents())
			present.refresh(this);
	}

	private ItemFrame getItemFrame(Advent25Present present) {
		return frames.get(present.getDay());
	}

	public void tryCollect(Advent25Present present) {
		try {
			collect(present);
		} catch (InvalidInputException ex) {
			sendMessage(new JsonBuilder(Pugmas25.PREFIX + "&c").next(ex.getJson()));
			found(present.getDay());
		}
	}

	public boolean hasCollected(LocalDate date) {
		return hasCollected(date.getDayOfMonth());
	}

	public boolean hasCollected(Advent25Present present) {
		return hasCollected(present.getDay());
	}

	public boolean hasCollected(int day) {
		return collected.contains(day);
	}

	public void collect(Advent25Present present) {
		validateCanCollect(present);

		collected.add(present.getDay());
		found.add(present.getDay());
		sendMessage(Pugmas25.PREFIX + "You found present &e#" + present.getDay() + "&3!");
		present.refresh(this);

		PlayerUtils.mailItem(getOnlinePlayer(), present.getItem().build(), null, WorldGroup.SURVIVAL);
		PlayerUtils.send(getOnlinePlayer(), Pugmas25.PREFIX + "This present has been sent to your &esurvival &c/mail box");
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).receiver(getOnlinePlayer()).play();
	}

	private void validateCanCollect(Advent25Present present) {
		if (hasCollected(present))
			throw new InvalidInputException("You already collected this present");

		if (Pugmas25.get().is25thOrAfter()) {
			if (present.getDay() == 25 && collected.size() != 24)
				throw new InvalidInputException("You need to find the rest of the presents to open this one");
		} else if (present.getDay() != Pugmas25.get().now().getDayOfMonth())
			throw new InvalidInputException("This present is for day &e#" + present.getDay());
	}

	public boolean canCollect(Advent25Present present) {
		try {
			validateCanCollect(present);
			return true;
		} catch (InvalidInputException ignore) {
			return false;
		}
	}

	public boolean hasFound(Advent25Present present) {
		return hasFound(present.getDay());
	}

	public boolean hasFound(int day) {
		return found.contains(day);
	}

	public void found(int day) {
		if (hasFound(day))
			return;

		found.add(day);
		sendMessage(Pugmas25.PREFIX + "Location of present &e#" + day + " &3saved. " +
				"View with the &eAdvent Calendar menu &3or &c/pugmas advent waypoint " + day);

		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).receiver(getOnlinePlayer()).play();
	}

	public int getUncollected() {
		return 25 - collected.size();
	}

	public void teleportAsync(Advent25Present present) {
		getOnlinePlayer().teleportAsync(present.getLocation().toCenterLocation());
	}

	public Advent25PresentStatus getStatus(Advent25Present present) {
		final Advent25PresentStatus status;
		if (hasCollected(present))
			status = OPENED;
		else if (canCollect(present))
			status = AVAILABLE;
		else if (Pugmas25.get().now().isBefore(present.getDate().atStartOfDay()))
			status = LOCKED;
		else
			status = MISSED;

		return status;
	}

}
