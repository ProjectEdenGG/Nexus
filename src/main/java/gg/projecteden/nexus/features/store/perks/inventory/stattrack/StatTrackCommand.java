package gg.projecteden.nexus.features.store.perks.inventory.stattrack;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.features.store.perks.inventory.stattrack.models.Stat;
import gg.projecteden.nexus.features.store.perks.inventory.stattrack.models.StatItem;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ToolType;
import lombok.NonNull;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

@HideFromWiki // TODO
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

		for (ToolType toolType : ToolType.values()) {
			if (toolType.getTools().contains(item.getType())) {
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
		new Paginator<Stat>()
			.values(Arrays.asList(Stat.values()))
			.formatter((stat, index) -> json(" &7- &e" + stat))
			.command("/stattrack list")
			.page(page)
			.send();
	}

	@Path("debug")
	void readLore() {
		final ItemStack item = getToolRequired();
		final StatItem statItem = new StatItem(item);

		send(PREFIX + "Debug for " + camelCase(item.getType()));
		send("&3ID: &e" + statItem.getId());
		if (Nullables.isNullOrEmpty(statItem.getStats()))
			send("&3Stats: &cNone");
		else {
			send("&3Stats:");
			statItem.getStats().forEach((stat, value) ->
					send("&e  " + stat + ": " + value));
		}
	}

}
