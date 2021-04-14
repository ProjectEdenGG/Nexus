package me.pugabyte.nexus.features.socialmedia.commands;

import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.features.socialmedia.SocialMedia.BNSocialMediaSite;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.queup.QueUp;
import me.pugabyte.nexus.models.queup.QueUpService;
import me.pugabyte.nexus.utils.Env;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;

@Aliases("dubtrack")
@SuppressWarnings("SameParameterValue")
public class QueUpCommand extends CustomCommand {
	private static final String URL = BNSocialMediaSite.QUEUP.getUrl();

	private static boolean enabled = true;
	private static final QueUpService service = new QueUpService();
	private static final QueUp queup = service.get();

	public QueUpCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(json("&e" + URL));

		if (!isNullOrEmpty(queup.getLastSong()))
			send("&3Currently playing: " + queup.getLastSong());
	}

	@Path("updates [enable]")
	@Permission("group.staff")
	void updates(Boolean enable) {
		if (enable == null)
			enable = !enabled;

		enabled = enable;
		send(PREFIX + "Song updates " + (enable ? "&aenabled" : "&cdisabled"));
	}

	static {
		if (Nexus.getEnv() == Env.PROD)
			Tasks.repeatAsync(Time.SECOND, Time.SECOND.x(15), () -> {
				if (!enabled)
					return;

				try {
					String currentSong = queup.getCurrentSong();

					if (currentSong != null && currentSong.equals(queup.getLastSong()))
						return;

					queup.setLastSong(currentSong);
					service.save(queup);

					if (currentSong == null)
						return;

					String hover = "&eClick me to join &dQueUp&e!";
					Chat.broadcastIngame(new JsonBuilder("&3Now playing on &d" + URL + "&3:").hover(hover).url(URL), MuteMenuItem.QUEUP);
					Chat.broadcastIngame(new JsonBuilder(" " + currentSong).hover(hover).url(URL), MuteMenuItem.QUEUP);
				} catch (Exception ignored) {
				}
			});
	}

}
