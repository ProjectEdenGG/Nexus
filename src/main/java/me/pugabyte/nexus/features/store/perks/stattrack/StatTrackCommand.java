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
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

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
		ItemStack item = Objects.requireNonNull(inventory().getItem(hand));

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

	@Path("debug")
	void readLore() {
		final ItemStack item = getToolRequired();
		final StatItem statItem = new StatItem(item);

		send(PREFIX + "Debug for " + camelCase(item.getType()));
		send("&3ID: &e" + statItem.getId());
		if (Utils.isNullOrEmpty(statItem.getStats()))
			send("&3Stats: &cNone");
		else {
			send("&3Stats:");
			statItem.getStats().forEach((stat, value) ->
					send("&e  " + stat + ": " + value));
		}
	}

}
