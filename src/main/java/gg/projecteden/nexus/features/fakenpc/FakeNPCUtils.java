package gg.projecteden.nexus.features.fakenpc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.fakenpc.FakeNPC.SkinProperties;
import gg.projecteden.nexus.utils.Tasks;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
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
import java.util.concurrent.CompletableFuture;

public class FakeNPCUtils {

	public static void setDefaultSkin(FakeNPC fakeNPC, Player player) {
		String[] name = getSkin(player);
		fakeNPC.setSkinProperties(new SkinProperties(player.getUniqueId().toString(), name[0], name[1]));
		fakeNPC.applySkin();
	}

	private static String[] getSkin(Player player) {
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		GameProfile gameProfile = entityPlayer.getProfile();
		Property property = gameProfile.getProperties().get("textures").iterator().next();
		String texture = property.getValue();
		String signature = property.getSignature();
		return new String[]{texture, signature};
	}

	public static CompletableFuture<Boolean> setMineSkin(FakeNPC fakeNPC, String url, boolean update) {
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
					setSkin(fakeNPC, uuid, signature, textureEncoded, update);
					future.complete(true);
				});

			} catch (Throwable t) {
				Tasks.sync(() -> {
					Nexus.log("Could not set fakeNPC skin via URL");
					t.printStackTrace();
					future.complete(false);
				});
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException ignored) {
					}
				}
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException ignored) {
					}
				}
			}
		});

		return future;
	}

	private static void setSkin(FakeNPC fakeNPC, String uuid, String signature, String texture, boolean update) {
		fakeNPC.skinProperties.setUuid(uuid);
		fakeNPC.skinProperties.setTexture(texture);
		fakeNPC.skinProperties.setSignature(signature);
		if (update)
			fakeNPC.applySkin();
	}
}
