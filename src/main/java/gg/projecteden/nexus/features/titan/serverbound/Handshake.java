package gg.projecteden.nexus.features.titan.serverbound;

import gg.projecteden.nexus.features.titan.ClientMessage;
import gg.projecteden.nexus.features.titan.clientbound.UpdateState;
import gg.projecteden.nexus.features.titan.models.Serverbound;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUser;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import org.bukkit.entity.Player;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

@Getter
public class Handshake extends Serverbound {

	String messagingVersion;

	@Override
	public void onReceive(Player player) {
		new LocalResourcePackUserService().edit(player, LocalResourcePackUser::useNewMessagingFormat);

		ClientMessage.builder()
			.players(player)
			.message(UpdateState.builder()
				.worldGroup(camelCase(WorldGroup.of(player)))
				.mode(camelCase(player.getGameMode().name()))
				.vanished(Vanish.isVanished(player))
				.build())
			.send();
	}
}
