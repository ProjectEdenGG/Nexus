package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.features.events.DebugDotCommand;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.LocationUtils.CardinalDirection;
import gg.projecteden.nexus.utils.Utils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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

@HideFromWiki
@NoArgsConstructor
@Permission(Group.ADMIN)
public class ParseCommandBlockSoundsCommand extends CustomCommand implements Listener {

	public ParseCommandBlockSoundsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("parseCommandBlockSounds")
	public void parseCommandBlockSounds() {
		Block start = location().add(0, -1, 0).getBlock();
		look(start, null);
		AtomicReference<String> all = new AtomicReference<>("");

		Utils.sortByKey(sounds).forEach((wait, commands) -> {
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

	private int wait = 0;
	private int SAFETY = 0;

	private final Map<Integer, List<String>> sounds = new HashMap<>();
	private final Map<Location, Integer> treeWait = new HashMap<>();

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

			if (relative.getState() instanceof CommandBlock commandBlock) {
				sounds.put(wait, new ArrayList<>(sounds.getOrDefault(wait, new ArrayList<>())) {{
					add(commandBlock.getCommand());
				}});
				treeWait.put(relative.getLocation(), wait);
				found(relative);
				look(relative, direction.getOppositeFace());
			}

			if (relative.getBlockData() instanceof Repeater repeater) {
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
		DebugDotCommand.play(
			player(),
			block.getRelative(BlockFace.UP).getLocation().toCenterLocation(),
			EnumUtils.random(ColorType.class)
		);
	}

}
