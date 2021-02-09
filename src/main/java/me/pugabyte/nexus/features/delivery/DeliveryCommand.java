package me.pugabyte.nexus.features.delivery;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.models.delivery.Delivery;
import me.pugabyte.nexus.models.delivery.DeliveryService;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		processEvent(event);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		processEvent(event);
	}

	public void processEvent(PlayerEvent event) {
		Player player = event.getPlayer();
		WorldGroup worldGroup = WorldGroup.get(player);
		Delivery delivery = service.get(player);
		if (!delivery.getItems().containsKey(worldGroup)) return;
		if (delivery.getItems().get(worldGroup) == null) return;
		List<ItemStack> items = new ArrayList<>(delivery.getItems().get(worldGroup));

		if (items.size() == 0) return;
		if (!new CooldownService().check(player, "deliveryReminder", Time.HOUR.x(1))) return;

		send(player, PREFIX + "&3You have an unclaimed delivery, use &e/delivery &3to claim it!");
	}

}

