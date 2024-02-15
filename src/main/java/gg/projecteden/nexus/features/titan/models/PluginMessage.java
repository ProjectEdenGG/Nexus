package gg.projecteden.nexus.features.titan.models;

import com.google.gson.JsonObject;
import gg.projecteden.nexus.features.titan.*;
import gg.projecteden.nexus.features.titan.clientbound.ResetMinigame;
import gg.projecteden.nexus.features.titan.clientbound.SaturnUpdate;
import gg.projecteden.nexus.features.titan.clientbound.UpdateState;
import gg.projecteden.nexus.features.titan.serverbound.Handshake;
import gg.projecteden.nexus.features.titan.serverbound.TitanConfig;
import gg.projecteden.nexus.features.titan.serverbound.Versions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public enum PluginMessage {
	HANDSHAKE(Handshake.class),
    SATURN_UPDATE(SaturnUpdate.class),
    TITAN_CONFIG(TitanConfig.class),
    VERSIONS(Versions.class),
	UPDATE_STATE(UpdateState.class),
	RESET_MINIGAME(ResetMinigame.class);

    final @NonNull Class<? extends Message> clazz;

    public void receive(JsonObject object, Player player) {
        if (object == null) return;
        Message message = ServerClientMessaging.GSON.fromJson(object, getClazz());
        if (message instanceof Serverbound serverbound)
			serverbound.onReceive(player);
    }




}
