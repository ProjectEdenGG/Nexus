package gg.projecteden.nexus.features.test;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.EnumUtils;
import gg.projecteden.utils.Env;
import gg.projecteden.utils.RandomUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environments(Env.TEST)
@Permission(Group.ADMIN)
@NoArgsConstructor
public class ItemFrameNameCommand extends CustomCommand {
	private static final List<Player> enabledList = new ArrayList<>();
	private static final Map<Player, ItemFrameName> playerMap = new HashMap<>();
	private static final Map<BlockFace, Integer> offsets = new HashMap<>() {{
		put(BlockFace.UP, 1);
		put(BlockFace.DOWN, 1);
	}};

	public ItemFrameNameCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[enabled]")
	void toggle(Boolean enabled) {
		if (enabled == null)
			enabled = !enabledList.contains(player());

		if (enabled) {
			enabledList.add(player());
			send("&3Itemframe names &aenabled");
		} else {
			enabledList.remove(player());
			removeName(player());
			send("&3Itemframe names &cdisabled");
		}
	}

	static {
		Tasks.repeat(TickTime.SECOND, TickTime.TICK.x(2), () -> {
			for (Player player : OnlinePlayers.getAll()) {
				if (enabledList.contains(player))
					itemFrameName(player);
			}
		});
	}

	private static void itemFrameName(Player player) {
		ItemFrame itemFrame = PlayerUtils.getTargetItemFrame(player, 10, offsets);
		ItemFrameName itemFrameName = playerMap.getOrDefault(player, new ItemFrameName());

		if (itemFrame == null) {
			if (itemFrameName.getId() != null)
				removeName(player);
			return;
		}

		if (itemFrameName.getLocation() != null && itemFrameName.getLocation().equals(itemFrame.getLocation()))
			return;

		if (itemFrameName.getId() != null)
			PacketUtils.entityDestroy(player, itemFrameName.getId());

		String name = EnumUtils.random(ColorType.class).getChatColor() + "Item Name Here";
		double height = RandomUtils.randomDouble(0.0, 0.6);
		EntityArmorStand armorStand = PacketUtils.entityNameFake(player, itemFrame, height, name, 1);

		itemFrameName.setId(armorStand.getId());
		itemFrameName.setLocation(itemFrame.getLocation());

		playerMap.put(player, itemFrameName);
	}

	@Data
	@NoArgsConstructor
	private static class ItemFrameName {
		Location location;
		Integer id;
	}

	private static void removeName(Player player) {
		ItemFrameName itemFrameName = playerMap.getOrDefault(player, new ItemFrameName());
		if (itemFrameName.getId() != null)
			PacketUtils.entityDestroy(player, itemFrameName.getId());

		itemFrameName = new ItemFrameName();
		playerMap.put(player, itemFrameName);
	}
}
