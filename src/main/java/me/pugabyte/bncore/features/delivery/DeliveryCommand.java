package me.pugabyte.bncore.features.delivery;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.delivery.Delivery;
import me.pugabyte.bncore.models.delivery.DeliveryService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
@Permission("group.admin")
public class DeliveryCommand extends CustomCommand implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("Delivery");
	private final DeliveryService service = new DeliveryService();
	private Delivery delivery;

	public DeliveryCommand(CommandEvent event) {
		super(event);
		delivery = service.get(player());
	}

	@Path
	void main() {
		WorldGroup worldGroup = WorldGroup.get(player());
		if (!Delivery.getSupportedWorldGroups().contains(worldGroup))
			error("You cannot do that in this world");

		if (delivery.get(worldGroup).isEmpty())
			error("You do not have any pending deliveries");

		new DeliveryMenu(delivery, worldGroup).open(player());
	}

	@Path("clear")
	void clearDatabase() {
		service.clearCache();
		service.deleteAll();
		service.clearCache();
	}

	@Path("test [material] [amount]")
	void test(Material material, @Arg("1") int amount) {
		delivery.setupDelivery(material == null ? getToolRequired() : new ItemStack(material, amount));
	}

//	@EventHandler
//	public void onWorldChange(PlayerChangedWorldEvent event) {
//		Player player = event.getPlayer();
//		WorldGroup worldGroup = WorldGroup.get(player);
//		Delivery delivery = service.get(player);
//		List<ItemStack> items = new ArrayList<>();
//
//		if (WorldGroup.SURVIVAL.equals(worldGroup))
//			items = delivery.getSurvivalItems();
//		else if (WorldGroup.SKYBLOCK.equals(worldGroup))
//			items = delivery.getSkyblockItems();
//
//		if (items.size() == 0) return;
////		if (!new CooldownService().check(player, "deliveryReminder", Time.HOUR.x(1))) return;
//
////		send(player, "\nSize3: " + items.size());
////		send(player, stripColor(items.toString());
//
//		send(player, PREFIX + "&3You have an unclaimed delivery, use &e/delivery &3to claim it!");
//	}

}

