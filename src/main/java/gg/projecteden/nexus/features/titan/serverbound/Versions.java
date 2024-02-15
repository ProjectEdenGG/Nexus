package gg.projecteden.nexus.features.titan.serverbound;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.titan.models.Message;
import gg.projecteden.nexus.features.titan.models.Serverbound;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Versions extends Serverbound {

    String titan;
    String saturn;

	@Override
	public void onReceive(Player player) {
		new LocalResourcePackUserService().edit(player, user -> {
			user.setTitanVerions(this);
			Nexus.log("Received Saturn/Titan updates from " + player.getName() + ". Saturn: " + user.getSaturnVersion() + " Titan: " + user.getTitanVersion());
		});
	}
}
