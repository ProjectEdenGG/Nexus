package gg.projecteden.nexus.hooks.viaversion;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import org.bukkit.entity.Player;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

public class ViaVersionHookImpl extends ViaVersionHook {

	@Override
	public String getPlayerVersion(Player player) {
		try {
			var version = Via.getManager().getPlatform().getApi().getPlayerVersion(player.getUniqueId());
			var versions = ProtocolVersion.getProtocols().stream()
				.filter(protocolVersion -> protocolVersion.getVersion() == version)
				.map(ProtocolVersion::getName)
				.toList();

			if (isNullOrEmpty(versions))
				return "Unknown (" + version + ")";

			return String.join(", ", versions);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			return "Unknown (ViaVersion error)";
		}
	}

}
