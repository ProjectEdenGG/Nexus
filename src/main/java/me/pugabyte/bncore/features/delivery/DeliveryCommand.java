package me.pugabyte.bncore.features.delivery;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.delivery.Delivery;
import me.pugabyte.bncore.models.delivery.DeliveryService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class DeliveryCommand extends CustomCommand implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("Delivery");
	private final DeliveryService service = new DeliveryService();
	private Delivery delivery;

	public DeliveryCommand(CommandEvent event) {
		super(event);
		if (sender() instanceof Player)
			delivery = service.get(player());
	}

	@Path
	@Permission("group.admin")
	void main() {
		WorldGroup worldGroup = WorldGroup.get(player());
		if (WorldGroup.SURVIVAL != worldGroup && WorldGroup.SKYBLOCK != worldGroup)
			error("&cYou cannot do that in this world");

		new DeliveryMenu(delivery, worldGroup).getInv().open(player());
	}

	@Path("clear")
	@Permission("group.admin")
	void clearDatabase() {
		service.clearCache();
		service.deleteAll();
		service.clearCache();
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		WorldGroup worldGroup = WorldGroup.get(player);
		Delivery delivery = service.get(player);
		List<ItemStack> items = new ArrayList<>();

		if (WorldGroup.SURVIVAL.equals(worldGroup))
			items = delivery.getSurvivalItems();
		else if (WorldGroup.SKYBLOCK.equals(worldGroup))
			items = delivery.getSkyblockItems();

		if (items.size() == 0) return;
//		if (!new CooldownService().check(player, "deliveryReminder", Time.HOUR.x(1))) return;

		send(player, PREFIX + "&3You have an unclaimed delivery, use &e/delivery &3to claim it!");
	}

}
