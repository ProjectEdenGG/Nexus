package gg.projecteden.nexus.features.fakenpc;

import com.google.common.io.BaseEncoding;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.PlayerUtils.SkinProperties;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
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
					Nexus.warn("Could not set fakeNPC skin via URL");
					t.printStackTrace();
					future.complete(false);
				});
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

	private static void setSkin(FakeNPC fakeNPC, @NonNull String uuid, @NonNull String signature, @NonNull String texture, boolean update) {
		String json = new String(BaseEncoding.base64().decode(texture), StandardCharsets.UTF_8);
		if (!json.contains("textures")) {
			throw new IllegalArgumentException("Invalid texture data");
		}

		fakeNPC.setSkinProperties(new SkinProperties(uuid, texture, signature));
		if (update)
			fakeNPC.applySkin();
	}
}
