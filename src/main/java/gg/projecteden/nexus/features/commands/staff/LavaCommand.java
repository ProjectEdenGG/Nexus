package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.lava.InfiniteLava;
import gg.projecteden.nexus.models.lava.InfiniteLavaService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static gg.projecteden.nexus.models.lava.InfiniteLava.DISABLED_WORLDS;

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
		if (DISABLED_WORLDS.contains(worldGroup()))
			error("Not allowed in " + camelCase(worldGroup()));

		if (enable == null)
			enable = !infiniteLava.isEnabled();

		infiniteLava.setEnabled(enable);
		service.save(infiniteLava);

		send(PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
	}

	@EventHandler
	public void onPlaceLava(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		if (!Rank.of(player).isStaff())
			return;

		Material material = event.getBucket();
		if (!material.equals(Material.LAVA_BUCKET))
			return;

		InfiniteLava infiniteLava = new InfiniteLavaService().get(player);
		if (!infiniteLava.isEnabled())
			return;

		PlayerInventory playerInv = player.getInventory();
		Tasks.wait(1, () -> playerInv.setItemInMainHand(new ItemStack(material)));
	}
}
