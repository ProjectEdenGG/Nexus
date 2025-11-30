package gg.projecteden.nexus.features.events.y2025.pugmas25.features;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Snowman;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.clientside.ClientSideConfig.ClientSideItemFrameModifier;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class Pugmas25Snowmen implements Listener {

	private final Pugmas25UserService userService = new Pugmas25UserService();

	public Pugmas25Snowmen() {
		Nexus.registerListener(this);

		ClientSideConfig.registerItemFrameModifier(new ClientSideItemFrameModifier() {
			@Override
			public ItemStack modify(ClientSideUser user, ClientSideItemFrame itemFrame) {
				Pugmas25Snowman snowman = Pugmas25Snowman.fromFrameLocation(itemFrame.getLocation());
				if (snowman == null)
					return itemFrame.content();

				DecorationType display = DecorationType.SNOWMAN_PLAIN;
				var pugmasUser = new Pugmas25UserService().get(user);
				if (pugmasUser.getDecoratedSnowmen().contains(snowman))
					display = DecorationType.SNOWMAN_FANCY;

				return display.getConfig().getItemBuilder().resetName().build();
			}
		});
	}

	@EventHandler
	public void on(DecorationInteractEvent event) {
		if (!Pugmas25.get().shouldHandle(event.getPlayer()))
			return;

		if (event.getDecorationType() != DecorationType.SNOWMAN_PLAIN && event.getDecorationType() != DecorationType.SNOWMAN_FANCY)
			return;

		Pugmas25Snowman snowman = Pugmas25Snowman.fromFrameLocation(event.getDecoration().getClientsideLocation());
		if (snowman == null)
			return;

		ItemStack tool = ItemUtils.getTool(event.getPlayer());
		if (Nullables.isNullOrAir(tool) || !Pugmas25QuestItem.BOX_OF_DECORATIONS.fuzzyMatch(tool))
			return;

		Pugmas25User pugmasUser = userService.get(event.getPlayer());
		if (pugmasUser.getDecoratedSnowmen().contains(snowman))
			return;

		pugmasUser.decorateSnowman(snowman);
		userService.save(pugmasUser);

		if (pugmasUser.getDecoratedSnowmen().size() == Pugmas25Snowman.values().length) {
			PlayerUtils.removeItem(event.getPlayer(), tool);
			PlayerUtils.giveItem(event.getPlayer(), Pugmas25QuestItem.BOX_OF_DECORATIONS_EMPTY.get());
		}
	}

}
