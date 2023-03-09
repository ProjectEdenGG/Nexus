package gg.projecteden.nexus.features.economy.commands;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.admin.CustomBoundingBoxCommand.CustomBoundingBoxEntityInteractEvent;
import gg.projecteden.nexus.features.survival.avontyre.AvontyreNPCListener;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

@HideFromWiki // TODO
@NoArgsConstructor
public class JobsCommand extends CustomCommand implements Listener {
	public static final NamespacedKey NBT_KEY = new NamespacedKey(Nexus.getInstance(), "jobs.board");

	public JobsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		new AvontyreNPCListener();
	}

	@Path
	void soon() {
		send(PREFIX + "Coming soon!");
	}

	@Path("board")
	void board() {
		soon();
	}

	@EventHandler
	public void on(CustomBoundingBoxEntityInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		Entity entity = event.getEntity().getEntity();
		if (entity == null)
			return;

		if (entity.getPersistentDataContainer().has(NBT_KEY)) {
			PlayerUtils.runCommand(event.getPlayer(), "jobs board");
			event.getPlayer().swingMainHand();
		}
	}

}
