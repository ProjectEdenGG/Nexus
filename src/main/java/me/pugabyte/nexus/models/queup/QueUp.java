package me.pugabyte.nexus.models.queup;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.queup.QueUp.API.ActiveSong;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.UUID;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@Data
@Builder
@Entity("queup")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class QueUp implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private String lastSong;

	@SneakyThrows
	public String getCurrentSong() {
		ActiveSong activeSong = ActiveSong.call();

		if (
				activeSong == null ||
				activeSong.getData() == null ||
				activeSong.getData().getSong() == null ||
				activeSong.getData().getSongInfo() == null
		) {
			return null;
		}

		String song = activeSong.getData().getSongInfo().getName();
		String user = activeSong.getData().getSong().getUserName();

		song = stripColor(StringEscapeUtils.unescapeHtml(song).replaceAll("&apos;", "'")); // it doesnt know what &apos; is??
		user = stripColor(StringEscapeUtils.unescapeHtml(user).replaceAll("&apos;", "'"));

		return "&e" + song + " &3(Queued by &e" + user + "&3)";
	}

	public static class API {
		private static final String BASE_URL = "https://api.queup.net/";

		private static final String ROOM_NAME = "projecteden";
		private static final String ROOM_ID = "60553abb972f3f00068689ef";

		private static final String ACTIVE_SONG_PATH = "room/" + ROOM_ID + "/playlist/active";
		private static final String USER_PATH = "user/";

		private static final Request REQUEST = new Request.Builder().url(BASE_URL + ACTIVE_SONG_PATH).build();

		private static final Gson gson = new Gson();
		private static final OkHttpClient client = new OkHttpClient();

		@Data
		static class ActiveSong {
			private ActiveSongData data;

			private static final Request REQUEST = new Request.Builder().url(BASE_URL + ACTIVE_SONG_PATH).build();

			public static ActiveSong call() {
				return API.call(REQUEST, ActiveSong.class);
			}

			@Data
			static class ActiveSongData {
				private Song song;
				private SongInfo songInfo;

				@Data
				static class Song {
					@SerializedName("userid")
					private String userId;

					String getUserName() {
						return API.call(BASE_URL + USER_PATH + userId, User.class).getData().getUsername();
					}
				}

				@Data
				static class SongInfo {
					private String name;
				}
			}
		}

		@Data
		static class User {
			private UserData data;

			@Data
			private static class UserData {
				private String username;
			}
		}

		private static <T> T call(String url, Class<T> responseClass) {
			return call(new Request.Builder().url(url).build(), responseClass);
		}

		private static <T> T call(Request request, Class<T> responseClass) {
			try (Response response = client.newCall(request).execute()) {
				if (response.body() == null)
					throw new InvalidInputException("QueUp API response is null");

				return gson.fromJson(response.body().string(), responseClass);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

}
