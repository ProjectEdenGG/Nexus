package me.pugabyte.nexus.features.store.perks.stattrack;

import lombok.NonNull;
import me.pugabyte.nexus.features.store.perks.stattrack.models.Stat;
import me.pugabyte.nexus.features.store.perks.stattrack.models.StatItem;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.Tool;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

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
		ItemStack item = inventory().getItem(hand);
		for (Tool tool : Tool.values()) {
			if (tool.getTools().contains(item.getType())) {
				inventory().setItem(hand, new StatItem(item).write().getItem());
				send(PREFIX + "Enabled statistic tracking on " + camelCase(item.getType()));
				return;
			}
		}
		send(PREFIX + "Statistic tracking cannot be enabled on " + camelCase(item.getType()));
	}

	@Path("list [page]")
	void list(@Arg("1") int page) {
		send(PREFIX + "Currently tracked statistics:");
		paginate(Arrays.asList(Stat.values()), (stat, index) -> json(" &7- &e" + stat), "/stattrack list", page);
	}

}
