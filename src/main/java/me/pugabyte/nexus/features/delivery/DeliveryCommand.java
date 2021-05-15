package me.pugabyte.nexus.features.delivery;

import eden.utils.TimeUtils.Time;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.SmartInvsPlugin;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.delivery.providers.DeliveryMenuProvider;
import me.pugabyte.nexus.features.delivery.providers.OpenDeliveryMenuProvider;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.models.delivery.DeliveryService;
import me.pugabyte.nexus.models.delivery.DeliveryUser;
import me.pugabyte.nexus.models.delivery.DeliveryUser.Delivery;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Aliases("mail")
public class DeliveryCommand extends CustomCommand implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("Delivery");
	private final DeliveryService service = new DeliveryService();
	private DeliveryUser user;

	public DeliveryCommand(CommandEvent event) {
		super(event);
		user = service.get(player());
		if (isCommandEvent())
			if (world().getName().startsWith("resource"))
				error("You cannot use Deliveries in the resource world");
	}

	@Path
	void main() {
		WorldGroup worldGroup = WorldGroup.get(player());
		if (!DeliveryUser.getSupportedWorldGroups().contains(worldGroup))
			error("You cannot use Deliveries in this world");

		new DeliveryMenuProvider(user, worldGroup()).open(player());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInvClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		Optional<SmartInventory> inv = SmartInvsPlugin.manager().getInventory(player);
		if (!inv.isPresent()) return;
		if (!(inv.get().getProvider() instanceof OpenDeliveryMenuProvider openDeliveryMenuProvider)) return;

		List<ItemStack> items = openDeliveryMenuProvider.getDelivery().getItems(); // check against inv items and diff
		List<ItemStack> contents = Arrays.asList(event.getInventory().getContents());

		for (ItemStack item : items)
			if (contents.contains(item))
				PlayerUtils.giveItem(player, item);
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		WorldGroup worldGroup = WorldGroup.get(player);
		DeliveryUser user = service.get(player);

		List<Delivery> deliveries = new ArrayList<>(user.get(worldGroup));
		if (Utils.isNullOrEmpty(deliveries))
			return;

		if (!new CooldownService().check(player, "deliveryReminder", Time.MINUTE.x(5)))
			return;

		user.sendNotification();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		DeliveryUser user = service.get(event.getPlayer());

		if (!user.getDeliveries().isEmpty())
			Tasks.wait(3, user::sendNotification);
	}

}

