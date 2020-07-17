package me.pugabyte.bncore.features.delivery;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.delivery.Delivery;
import me.pugabyte.bncore.models.delivery.DeliveryService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DeliveryCommand extends CustomCommand {
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

		if (delivery.getSurvivalItems().size() == 0) {
			for (int i = 0; i < 5; i++)
				delivery.addToSurvival(new ItemBuilder(Material.DIRT).name("Dirt" + i + " Delivery").amount(1).build());
		}

		new DeliveryMenu(delivery, worldGroup).getInv().open(player());
	}

	@Path("deleteAll")
	@Permission("group.admin")
	void clearDatabase() {
		service.deleteAll();
	}

}
