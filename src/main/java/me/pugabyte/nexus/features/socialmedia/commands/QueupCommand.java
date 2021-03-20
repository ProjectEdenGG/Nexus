package me.pugabyte.nexus.features.socialmedia.commands;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NonNull;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.features.socialmedia.SocialMedia.BNSocialMediaSite;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.queup.Queup;
import me.pugabyte.nexus.models.queup.QueupService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.StringEscapeUtils;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@Aliases("dubtrack")
@SuppressWarnings("SameParameterValue")
public class QueupCommand extends CustomCommand {
	private static final String URL = BNSocialMediaSite.QUEUP.getUrl();

	private static boolean enabled = true;
	private static final QueupService service = new QueupService();
	private static final Queup queup = service.get();

	public QueupCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(json("&e" + URL));

		if (!isNullOrEmpty(queup.getLastSong()))
			send("&3Currently playing: &e" + queup.getLastSong());
	}

	@Path("updates [enable]")
	@Permission("group.staff")
	void updates(Boolean enable) {
		if (enable == null)
			enable = !enabled;

		enabled = enable;
		send(PREFIX + "Song updates " + (enable ? "&aenabled" : "&cdisabled"));
	}

	private static final String BASE_URL = "https://api.queup.net/";

	private static final String ROOM_NAME = "bearnation";
	private static final String ROOM_ID = "60553abb972f3f00068689ef";

	private static final String ACTIVE_SONG_PATH = "room/" + ROOM_ID + "/playlist/active";
	private static final String USER_PATH = "user/";

	private static final Request ACTIVE_SONG_REQUEST = new Request.Builder().url(BASE_URL + ACTIVE_SONG_PATH).build();

	static {
		Tasks.repeatAsync(Time.SECOND, Time.SECOND.x(15), () -> {
			if (!enabled)
				return;

			ActiveSong activeSong = call(ACTIVE_SONG_REQUEST, ActiveSong.class);

			if (
					activeSong == null ||
					activeSong.getData() == null ||
					activeSong.getData().getSong() == null ||
					activeSong.getData().getSongInfo() == null
			) {
				queup.setLastSong(null);
				service.save(queup);
				return;
			}

			String song = activeSong.getData().getSongInfo().getName();
			String user = activeSong.getData().getSong().getUserName();

			song = stripColor(StringEscapeUtils.unescapeHtml(song));
			user = stripColor(StringEscapeUtils.unescapeHtml(user));

			String currentSong = song + " &3(Queued by &e" + user + "&3)";

			if (currentSong.equals(queup.getLastSong()))
				return;

			queup.setLastSong(currentSong);
			service.save(queup);

			String hover = "&eClick me to join &dQueup&e!";
			Chat.broadcastIngame(new JsonBuilder("&3Now playing on &d" + URL + "&3:").hover(hover).url(URL), MuteMenuItem.QUEUP);
			Chat.broadcastIngame(new JsonBuilder("&e " + currentSong).hover(hover).url(URL), MuteMenuItem.QUEUP);
		});
	}

	@Data
	static class ActiveSong {
		private ActiveSongData data;

		@Data
		private static class ActiveSongData {
			private Song song;
			private SongInfo songInfo;

			@Data
			private static class Song {
				@SerializedName("userid")
				private String userId;

				String getUserName() {
					return call(BASE_URL + USER_PATH + userId, User.class).getData().getUsername();
				}
			}

			@Data
			private static class SongInfo {
				private String name;
			}
		}
	}

	@Data
	private static class User {
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
		try (Response response = new OkHttpClient().newCall(request).execute()) {
			if (response.body() == null)
				throw new InvalidInputException("Queup API response is null");

			return new Gson().fromJson(response.body().string(), responseClass);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
