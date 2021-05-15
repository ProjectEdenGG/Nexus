package me.pugabyte.nexus.models.queup;

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
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.queup.QueUp.API.ActiveSong;
import me.pugabyte.nexus.utils.HttpUtils;

import java.util.UUID;

import static me.pugabyte.nexus.utils.HttpUtils.unescapeHtml;
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

		song = stripColor(unescapeHtml(song));
		user = stripColor(unescapeHtml(user));

		return "&e" + song + " &3(Queued by &e" + user + "&3)";
	}

	public static class API {
		private static final String BASE_URL = "https://api.queup.net/";

		private static final String ROOM_NAME = "projectedengg";
		private static final String ROOM_ID = "60553abb972f3f00068689ef";

		private static final String ACTIVE_SONG_PATH = "room/" + ROOM_ID + "/playlist/active";
		private static final String USER_PATH = "user/";

		@Data
		static class ActiveSong {
			private ActiveSongData data;

			public static ActiveSong call() {
				return HttpUtils.mapJson(ActiveSong.class, BASE_URL + ACTIVE_SONG_PATH);
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
						return HttpUtils.mapJson(User.class, BASE_URL + USER_PATH + userId).getData().getUsername();
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

	}

}
