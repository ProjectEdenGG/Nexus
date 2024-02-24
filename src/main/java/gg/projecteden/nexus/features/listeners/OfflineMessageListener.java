package gg.projecteden.nexus.features.listeners;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.models.offline.OfflineMessageService;
import gg.projecteden.nexus.models.offline.OfflineMessageUser;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OfflineMessageListener implements Listener {

	private static final OfflineMessageService service = new OfflineMessageService();

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		OfflineMessageUser user = service.get(event.getPlayer());
		int delay = 0;
		for (int i = 0; i < user.getMessages().size(); i++)
			Tasks.wait(TimeUtils.TickTime.SECOND.x(5 + delay++), user::sendNext);
	}

}
