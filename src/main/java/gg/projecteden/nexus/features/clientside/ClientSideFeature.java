package gg.projecteden.nexus.features.clientside;

import gg.projecteden.nexus.features.clientside.models.IClientSideEntity;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import org.bukkit.entity.Player;

public class ClientSideFeature extends Feature {

	@Override
	public void onStart() {
		new ClientSideEntitiesManager();

		for (Player player : OnlinePlayers.getAll()) {
			ClientSideUser user = ClientSideUser.of(player);
			for (IClientSideEntity<?, ?, ?> entity : ClientSideConfig.getEntities(player.getWorld()))
				user.show(entity);
		}
	}

	@Override
	public void onStop() {
		for (IClientSideEntity<?, ?, ?> entity : ClientSideConfig.getEntities())
			for (Player player : OnlinePlayers.where().world(entity.location().getWorld()).get())
				ClientSideUser.of(player).hide(entity);
	}

}
