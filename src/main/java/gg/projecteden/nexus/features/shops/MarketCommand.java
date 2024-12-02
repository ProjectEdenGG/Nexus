package gg.projecteden.nexus.features.shops;

import gg.projecteden.nexus.features.shops.providers.BrowseMarketProvider;
import gg.projecteden.nexus.features.shops.providers.ResourceWorldMarketProvider.AutoSellBehavior;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.SubWorldGroup;
import lombok.NonNull;

public class MarketCommand extends CustomCommand {

	public MarketCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (!SubWorldGroup.RESOURCE.contains(world()))
			error("This command is only available in the resource world");

		new BrowseMarketProvider(null).open(player());
	}

	@Path("autoSellBehavior <behavior>")
	@Description("Set behavior for auto selling to the resource market")
	void toggleGlobalAutoSell(AutoSellBehavior setting) {
		Shop shop = new ShopService().get(player());
		shop.setResourceMarketAutoSellBehavior(setting);

		send(PREFIX + "Auto Selling behavior set to &e" + StringUtils.camelCase(setting));
	}

	@Path("reload")
	@Permission(Group.STAFF)
	void reload() {
		Market.load();
		send(PREFIX + "Market reloaded");
	}

}
