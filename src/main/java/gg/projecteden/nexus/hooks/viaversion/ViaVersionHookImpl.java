package gg.projecteden.nexus.hooks.viaversion;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import org.bukkit.entity.Player;

public class ViaVersionHookImpl extends ViaVersionHook {

	@Override
	public String getPlayerVersion(Player player) {
		try {
			ProtocolVersion version = Via.getManager().getPlatform().getApi().getPlayerProtocolVersion(player.getUniqueId());

			if (!version.isKnown())
				return "Unknown (" + version + ")";

			return version.getName();
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			return "Unknown (ViaVersion error)";
		}
	}

}
