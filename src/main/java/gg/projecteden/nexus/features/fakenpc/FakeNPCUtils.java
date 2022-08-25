package gg.projecteden.nexus.features.fakenpc;

import com.google.common.io.BaseEncoding;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.fakenpc.types.PlayerNPC;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FakeNPCUtils {

	public static boolean isNPCVisibleFor(FakeNPC fakeNPC, UUID uuid) {
		FakeNPCManager.getPlayerVisibleNPCs().putIfAbsent(uuid, new HashSet<>());
		return FakeNPCManager.getPlayerVisibleNPCs().get(uuid).contains(fakeNPC);
	}

	public static boolean isHologramVisibleFor(FakeNPC fakeNPC, UUID uuid) {
		FakeNPCManager.getPlayerVisibleHolograms().putIfAbsent(uuid, new HashSet<>());
		return FakeNPCManager.getPlayerVisibleHolograms().get(uuid).contains(fakeNPC);
	}

	public static boolean isInSameWorld(Player player, FakeNPC fakeNPC) {
		return player.getWorld().equals(fakeNPC.getLocation().getWorld());
	}

	public static CompletableFuture<Boolean> setMineSkin(PlayerNPC playerNPC, String url, boolean update) {
		CompletableFuture<Boolean> future = new CompletableFuture<>();
		Tasks.async(() -> {
			DataOutputStream out = null;
			BufferedReader reader = null;

			try {
				URL target = new URL("https://api.mineskin.org/generate/url");
				HttpURLConnection con = (HttpURLConnection) target.openConnection();
				con.setRequestMethod("POST");
				con.setDoOutput(true);
				con.setConnectTimeout(1000);
				con.setReadTimeout(30000);
				out = new DataOutputStream(con.getOutputStream());
				out.writeBytes("url=" + URLEncoder.encode(url, StandardCharsets.UTF_8));
				out.close();
				reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				JSONObject output = (JSONObject) new JSONParser().parse(reader);
				JSONObject data = (JSONObject) output.get("data");
				String uuid = (String) data.get("uuid");
				JSONObject texture = (JSONObject) data.get("texture");
				String textureEncoded = (String) texture.get("value");
				String signature = (String) texture.get("signature");
				con.disconnect();

				Tasks.sync(() -> {
					setSkin(playerNPC, uuid, signature, textureEncoded, update);
					future.complete(true);
				});

			} catch (Throwable t) {
				t.printStackTrace();
				future.complete(false);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException ignored) {}
				}
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException ignored) {}
				}
			}
		});

		return future;
	}

	private static void setSkin(PlayerNPC playerNPC, @NonNull String uuid, @NonNull String signature, @NonNull String texture, boolean update) {
		String json = new String(BaseEncoding.base64().decode(texture), StandardCharsets.UTF_8);
		if (!json.contains("textures")) {
			throw new IllegalArgumentException("Invalid texture data");
		}

		playerNPC.setSkinProperties(new SkinProperties(uuid, texture, signature));
		if (update)
			playerNPC.applySkin();
	}

	public static @Nullable String getUUID(String name) {
		try {
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			InputStreamReader reader = new InputStreamReader(url.openStream());
			return new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();
		} catch (Exception ex) {
			Nexus.warn("An error occurred when getting UUID of player: " + name);
			ex.printStackTrace();
		}

		return null;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SkinProperties {
		private String uuid;
		private String texture;
		private String signature;

		public static SkinProperties of(Player player) {
			Property property = NMSUtils.getSkinProperty(player);
			return new SkinProperties(player.getUniqueId().toString(), property.getValue(), property.getSignature());
		}

		public static SkinProperties of(OfflinePlayer player) {
			return of(player.getUniqueId().toString());
		}

		public static @Nullable SkinProperties of(String uuid) {
			try {
				URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
				InputStreamReader reader = new InputStreamReader(url.openStream());
				JsonObject textureProperty = new JsonParser().parse(reader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
				String texture = textureProperty.get("value").getAsString();
				String signature = textureProperty.get("signature").getAsString();
				return new SkinProperties(uuid, texture, signature);
			} catch (Exception ex) {
				Nexus.warn("An error occurred when getting skin of UUID: " + uuid);
				ex.printStackTrace();
			}

			return null;
		}
	}
}
