package me.pugabyte.bncore.features.commands.staff;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.lava.InfiniteLava;
import me.pugabyte.bncore.models.lava.InfiniteLavaService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@NoArgsConstructor
@Permission("group.staff")
public class LavaCommand extends CustomCommand implements Listener {
	private final InfiniteLavaService service = new InfiniteLavaService();
	private InfiniteLava infiniteLava;

	public LavaCommand(CommandEvent event) {
		super(event);
		infiniteLava = service.get(player());
	}

	@Path("[on|off]")
	void lava(Boolean enable) {
		WorldGroup world = WorldGroup.get(player());
		if (world.equals(WorldGroup.SKYBLOCK))
			error("Not allowed in " + world);

		if (enable == null)
			enable = !infiniteLava.isEnabled();

		infiniteLava.setEnabled(enable);
		service.save(infiniteLava);

		send(PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
	}

	@EventHandler
	public void onPlaceLava(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("group.staff"))
			return;

		Material material = event.getBucket();
		if (!material.equals(Material.LAVA_BUCKET))
			return;

		WorldGroup world = WorldGroup.get(player);
		if (world.equals(WorldGroup.SKYBLOCK))
			return;

		InfiniteLava infiniteLava = new InfiniteLavaService().get(player);
		if (!infiniteLava.isEnabled())
			return;

		PlayerInventory playerInv = player.getInventory();
		Tasks.wait(1, () -> playerInv.setItemInMainHand(new ItemStack(material)));
	}
}
