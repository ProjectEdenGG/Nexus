package me.pugabyte.bncore.features.commands.staff;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@Permission("group.staff")
@NoArgsConstructor
public class CamCommand extends CustomCommand implements Listener {
	private static Material material;

	public CamCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	void run() {
		String name = player().getName();
		if (!name.equalsIgnoreCase("camaros"))
			error("command for Camaros only");

		send("Forcing you to run: //replace air " + material.name().toLowerCase());
		Utils.runCommandAsOp(player(), "/replace air " + material.name().toLowerCase());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onClickBlock(PlayerInteractEvent event) {
		String name = event.getPlayer().getName();
		if (!name.equalsIgnoreCase("camaros"))
			return;

		if (Utils.isNullOrAir(event.getClickedBlock()))
			return;

		ItemStack tool = Utils.getTool(event.getPlayer());
		if (Utils.isNullOrAir(tool))
			return;

		if (!tool.getType().equals(Material.WOODEN_AXE))
			return;

		if (!event.getAction().equals(Action.LEFT_CLICK_BLOCK))
			return;

		material = event.getClickedBlock().getType();

		send(event.getPlayer(), "Set replace material as " + material);
	}
}
