package gg.projecteden.nexus.features.clientside;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.models.clientside.ClientSideUserService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.entity.Player;

/*
	TODO
		support ambience
		support chairs
*/

public class ClientSideFeature extends Feature {

	static {
		final var userService = new ClientSideUserService();
		Tasks.repeatAsync(TickTime.SECOND, TickTime.SECOND.x(10), userService::saveOnlineSync);
	}

	@Override
	public void onStart() {
		new ClientSideEntitiesManager();

		for (Player player : OnlinePlayers.getAll()) {
			ClientSideUser user = ClientSideUser.of(player);
			for (var entity : ClientSideConfig.getEntities(player.getWorld()))
				user.show(entity);
		}
	}

	@Override
	public void onStop() {
		for (var entity : ClientSideConfig.getEntities())
			for (Player player : OnlinePlayers.where().world(entity.location().getWorld()).get())
				ClientSideUser.of(player).hide(entity);

		new ClientSideUserService().saveOnlineSync();
	}

}
