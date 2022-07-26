package gg.projecteden.nexus.hooks.viaversion;

import com.google.gson.annotations.SerializedName;
import com.viaversion.viaversion.api.Via;
import gg.projecteden.nexus.hooks.viaversion.ViaVersionHookImpl.VersionConfig.Version;
import gg.projecteden.nexus.utils.HttpUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.Data;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViaVersionHookImpl extends ViaVersionHook {

	private static final Map<Integer, List<String>> versions = new HashMap<>();

	@Data
	static class VersionConfig {
		private Map<String, Version> versions;

		@Data
		static class Version {
			private String name;
			private String type;
			@SerializedName("protocol_id")
			private int protocolId;
		}
	}

	static {
		Tasks.async(() -> {
			try {
				final String URL = "https://gitlab.bixilon.de/bixilon/minosoft/-/raw/master/src/main/resources/assets/minosoft/mapping/versions.json";
				try (Response response = HttpUtils.callUrl(URL)) {
					final String body = "{\"versions\": " + response.body().string() + "}";
					final VersionConfig config = Utils.getGson().fromJson(body, VersionConfig.class);
					for (Version version : config.getVersions().values()) {
						if (!"release".equals(version.getType()))
							continue;

						if (version.getProtocolId() == 0)
							continue;

						versions.computeIfAbsent(version.getProtocolId(), $ -> new ArrayList<>()).add(version.getName());
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	@Override
	public String getPlayerVersion(Player player) {
		try {
			if (Bukkit.getServer().getPluginManager().getPlugin("ViaVersion") == null)
				return "Unknown (ViaVersion not loaded)";

			final int version = Via.getAPI().getPlayerVersion(player);
			if (versions.containsKey(version))
				return String.join("/", versions.get(version));
			return "Unknown (" + version + ")";
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			return "Unknown (ViaVersion error)";
		}
	}
}
