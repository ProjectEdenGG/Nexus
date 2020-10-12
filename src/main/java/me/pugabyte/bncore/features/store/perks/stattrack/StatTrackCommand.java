package me.pugabyte.bncore.features.store.perks.stattrack;

import java.util.Arrays;
import lombok.NonNull;
import me.pugabyte.bncore.features.store.perks.stattrack.models.Stat;
import me.pugabyte.bncore.features.store.perks.stattrack.models.StatItem;
import me.pugabyte.bncore.features.store.perks.stattrack.models.Tool;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@Permission("stat.track")
public class StatTrackCommand extends CustomCommand {

	public StatTrackCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		new StatTrackListener();
	}

	@Path("start")
	void start() {
		EquipmentSlot hand = getHandWithToolRequired();
		ItemStack item = player().getInventory().getItem(hand);
		for (Tool tool : Tool.values()) {
			if (tool.getTools().contains(item.getType())) {
				player().getInventory().setItem(hand, new StatItem(item).write().getItem());
				send(PREFIX + "Enabled statistic tracking on " + camelCase(item.getType()));
				return;
			}
		}
		send(PREFIX + "Statistic tracking cannot be enabled on " + camelCase(item.getType()));
	}

	@Path("list [page]")
	void list(@Arg("1") int page) {
		send(PREFIX + "Currently tracked statistics:");
		paginate(Arrays.asList(Stat.values()), stat -> json(" &7- &e" + stat), "/stattrack list", page);
	}

}
