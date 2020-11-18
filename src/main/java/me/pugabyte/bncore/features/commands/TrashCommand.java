package me.pugabyte.bncore.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.dumpster.Dumpster;
import me.pugabyte.bncore.models.dumpster.DumpsterService;
import me.pugabyte.bncore.utils.ItemUtils;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class TrashCommand extends CustomCommand implements Listener {
	private static final String TITLE = StringUtils.colorize("&4Trash");

	public TrashCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void trash() {
		player().openInventory(Bukkit.createInventory(null, 6 * 9, TITLE));
	}

	@Path("<materials...>")
	void trash(@Arg(type = Material.class) List<Material> materials) {
		DumpsterService dumpsterService = new DumpsterService();
		Dumpster dumpster = dumpsterService.get();

		for (Material material : materials) {
			dumpster.add(player().getInventory().all(material).values());
			player().getInventory().remove(material);
		}

		dumpsterService.save(dumpster);
		send(PREFIX + "Trashed all matching materials");
	}

	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() != null) return;
		if (!Utils.equalsInvViewTitle(event.getView(), TITLE)) return;

		DumpsterService service = new DumpsterService();
		Dumpster dumpster = service.get();

		Arrays.stream(event.getInventory().getContents())
				.filter(item -> !ItemUtils.isNullOrAir(item))
				.forEach(dumpster::add);

		service.save(dumpster);
	}

}
