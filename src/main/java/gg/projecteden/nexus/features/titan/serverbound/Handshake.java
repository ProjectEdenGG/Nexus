package gg.projecteden.nexus.features.titan.serverbound;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.titan.ClientMessage;
import gg.projecteden.nexus.features.titan.clientbound.CustomBlocks;
import gg.projecteden.nexus.features.titan.clientbound.Decorations;
import gg.projecteden.nexus.features.titan.clientbound.UpdateState;
import gg.projecteden.nexus.features.titan.models.CustomCreativeItem;
import gg.projecteden.nexus.features.titan.models.Serverbound;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUser;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Getter
public class Handshake extends Serverbound {

	String messagingVersion;

	@Override
	public void onReceive(Player player) {
		new LocalResourcePackUserService().edit(player, LocalResourcePackUser::useNewMessagingFormat);

		ClientMessage.builder()
			.players(player)
			.message(UpdateState.builder()
				.worldGroup(StringUtils.camelCase(WorldGroup.of(player)))
				.mode(StringUtils.camelCase(player.getGameMode().name()))
				.vanished(Vanish.isVanished(player))
				.build())
			.send();

		// TODO CUSTOMBLOCKS: FILTER
		ClientMessage.builder()
			.players(player)
			.message(new CustomBlocks(Arrays.stream(CustomBlock.values()).map(CustomCreativeItem::new).toList().toArray(new CustomCreativeItem[0])))
			.send();

//		ClientMessage.builder()
//			.players(player)
//			.message(new Decorations(Arrays.stream(DecorationType.values()).map(CustomCreativeItem::new).toList().toArray(new CustomCreativeItem[0])))
//			.send();

		Chatter.of(player).notifyTitanOfChannelChange();
	}
}
