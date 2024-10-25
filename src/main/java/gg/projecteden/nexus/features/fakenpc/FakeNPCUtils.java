package gg.projecteden.nexus.features.fakenpc;

import com.google.common.io.BaseEncoding;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPCService;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPCTraitType;
import gg.projecteden.nexus.models.fakenpcs.npcs.traits.LookCloseTrait;
import gg.projecteden.nexus.models.fakenpcs.npcs.types.PlayerNPC;
import gg.projecteden.nexus.models.fakenpcs.users.FakeNPCUser;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.nexus.utils.nms.NMSUtils.Property;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
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
import java.util.concurrent.CompletableFuture;

public class FakeNPCUtils {

	public static @NonNull String getNameAndId(FakeNPC npc) {
		return "&e" + npc.getName() + " &3(ID: &e" + npc.getId() + "&3)";
	}

	public static boolean isInSameWorld(FakeNPCUser user, FakeNPC fakeNPC) {
		return user.isOnline() && fakeNPC.getLocation() != null && user.getOnlinePlayer().getWorld().equals(fakeNPC.getLocation().getWorld());
	}

	public static boolean canLookClose(FakeNPC npc, FakeNPCUser user) {
		return user.isOnline() && canLookClose(npc, user.getOnlinePlayer());
	}

	public static boolean canLookClose(FakeNPC npc, org.bukkit.entity.Entity entity) {
		if (npc == null || !npc.isSpawned())
			return false;

		if (entity == null || !entity.isValid())
			return false;

		LookCloseTrait trait = npc.getTrait(FakeNPCTraitType.LOOK_CLOSE);
		if (trait == null || !trait.isEnabled())
			return false;

		Location npcLocation = npc.getLocation();
		if (!entity.getWorld().equals(npcLocation.getWorld()))
			return false;

		if (Distance.distance(npcLocation, entity.getLocation()).gt(trait.getRadius()))
			return false;

		if (npc.getBukkitEntity() instanceof LivingEntity livingEntity)
			return livingEntity.hasLineOfSight(entity);
		return true;
	}

	public static @NonNull FakeNPC fromId(int id) {
		return new FakeNPCService().getAll().stream()
			.filter(npc -> npc.getId() == id)
			.findFirst()
			.orElseThrow(() -> new InvalidInputException("FakeNPC from id &e" + id + " &cnot found"));
	}

	public static CompletableFuture<Boolean> setMineSkin(PlayerNPC npc, String url, boolean update) {
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
					setSkin(npc, uuid, signature, textureEncoded, update, true);
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

	private static void setSkin(PlayerNPC npc, @NonNull String uuid, @NonNull String signature, @NonNull String texture, boolean update, boolean fromURL) {
		String json = new String(BaseEncoding.base64().decode(texture), StandardCharsets.UTF_8);
		if (!json.contains("textures")) {
			throw new IllegalArgumentException("Invalid texture data");
		}

		npc.setSkinProperties(new SkinProperties(uuid, texture, signature, fromURL));
		if (update)
			npc.applySkin();
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
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SkinProperties {
		private String uuid;
		private String texture;
		private String signature;
		private boolean fromURL;

		public static SkinProperties of(Player player) {
			Property property = NMSUtils.getSkinProperty(player);
			return new SkinProperties(player.getUniqueId().toString(), property.getValue(), property.getSignature(), false);
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
				return new SkinProperties(uuid, texture, signature, false);
			} catch (Exception ex) {
				Nexus.warn("An error occurred when getting skin of UUID: " + uuid);
				ex.printStackTrace();
			}

			return null;
		}
	}
}
