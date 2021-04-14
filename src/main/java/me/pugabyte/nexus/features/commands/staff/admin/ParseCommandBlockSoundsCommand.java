package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.particles.effects.DotEffect;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.EnumUtils;
import me.pugabyte.nexus.utils.LocationUtils.CardinalDirection;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static me.pugabyte.nexus.utils.LocationUtils.getCenteredLocation;
import static me.pugabyte.nexus.utils.Utils.sortByKey;

@NoArgsConstructor
public class ParseCommandBlockSoundsCommand extends CustomCommand implements Listener {

	public ParseCommandBlockSoundsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("parseCommandBlockSounds")
	public void parseCommandBlockSounds() {
		Block start = location().add(0, -1, 0).getBlock();
		look(start, null);
		AtomicReference<String> all = new AtomicReference<>("");

		sortByKey(sounds).forEach((wait, commands) -> {
			String code = "Tasks.wait(" + wait + ", () -> {";
			for (String command : commands) {
				String[] args = command
						.replace("/playsound minecraft:", "")
						.replace("master @a[distance=..35] ~ ~ ~ ", "")
						.toUpperCase()
						.split(" ");

				String sound = args[0].replace(".", "_");
				try {
					Sound.valueOf(sound);
				} catch (IllegalArgumentException ex) {
					sound = "___" + sound + "___";
				}

				String volume = args[1];
				if (volume.contains(".")) volume = volume + "F";
				String pitch = args[2];
				if (pitch.contains(".")) pitch = pitch + "F";

				code += System.lineSeparator() + "    playSound(player, Sound." + sound + ", " + volume + ", " + pitch + ");";
			}
			code += System.lineSeparator() + "});";
			all.set(all.get() + System.lineSeparator() + code);
		});

		String message = all.get();
		send(json(PREFIX + "Click to copy").copy(message));
	}

	int wait = 0;
	int SAFETY = 0;

	Map<Integer, List<String>> sounds = new HashMap<>();
	Map<Location, Integer> treeWait = new HashMap<>();

	void look(Block start, BlockFace ignore) {
		for (BlockFace direction : CardinalDirection.blockFaces()) {
			if (++SAFETY > 1000) {
				send("Safety engaging");
				break;
			}

			if (direction == ignore)
				continue;
			Block relative = start.getRelative(direction);

			if (treeWait.containsKey(start.getLocation()))
				wait = treeWait.get(start.getLocation());

			if (relative.getBlockData() instanceof RedstoneWire) {
				treeWait.put(relative.getLocation(), wait);
				found(relative);
				look(relative, direction.getOppositeFace());
			}

			if (relative.getState() instanceof CommandBlock) {
				CommandBlock commandBlock = (CommandBlock) relative.getState();
				sounds.put(wait, new ArrayList<String>(sounds.getOrDefault(wait, new ArrayList<>())) {{ add(commandBlock.getCommand()); }});
				treeWait.put(relative.getLocation(), wait);
				found(relative);
				look(relative, direction.getOppositeFace());
			}

			if (relative.getBlockData() instanceof Repeater) {
				Repeater repeater = (Repeater) relative.getBlockData();
				BlockFace newDirection = repeater.getFacing().getOppositeFace();
				if (direction != newDirection)
					continue;

				wait += repeater.getDelay();
				found(relative);
				look(relative, direction.getOppositeFace());
			}
		}
	}

	void found(Block block) {
		DotEffect.builder()
				.player(player())
				.location(getCenteredLocation(block.getLocation().add(0, 1, 0)).add(0, .5, 0))
				.speed(0.1)
				.ticks(Time.SECOND.x(5))
				.color(EnumUtils.random(ColorType.class).getColor())
				.start();
	}

}
