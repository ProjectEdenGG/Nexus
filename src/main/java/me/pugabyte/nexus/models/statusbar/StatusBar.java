package me.pugabyte.nexus.models.statusbar;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.features.scoreboard.ScoreboardLine;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.chat.Channel;
import me.pugabyte.nexus.models.chat.ChatService;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.models.chat.PublicChannel;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.UUID;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

@Data
@Builder
@Entity("status_bar")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class StatusBar extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled;

	private transient int taskId;
	private transient BossBar bossBar;

	public void start() {
		if (!enabled)
			return;

		if (!getOfflinePlayer().isOnline())
			return;

		stop();

		bossBar = Bukkit.createBossBar(getText(), BarColor.BLUE, BarStyle.SEGMENTED_6);
		bossBar.addPlayer(getPlayer());
		taskId = Tasks.repeat(0, Time.SECOND, () -> {
			if (!isOnline())
				stop();
			else if (bossBar != null) {
				bossBar.setColor(getColor(uuid));
				bossBar.setTitle(getText());
			}
		});
	}

	public void stop() {
		Tasks.cancel(taskId);
		if (bossBar != null) {
			bossBar.setVisible(false);
			bossBar.removeAll();
			bossBar = null;
		}
	}

	public String getText() {
		return colorize(
				ScoreboardLine.PING.render(getPlayer()) + "  &8&l|  " +
				ScoreboardLine.TPS.render(getPlayer()) + "  &8&l|  " +
				ScoreboardLine.CHANNEL.render(getPlayer())
		);
	}

	public static BarColor getColor(UUID uuid) {
		Chatter chatter = new ChatService().get(uuid);
		Channel activeChannel = chatter.getActiveChannel();
		if (activeChannel instanceof PublicChannel) {
			PublicChannel channel = (PublicChannel) activeChannel;
			if (StaticChannel.GLOBAL.getChannel().equals(channel))
				return BarColor.GREEN;
			else if (StaticChannel.LOCAL.getChannel().equals(channel))
				return BarColor.YELLOW;
		}

		return BarColor.BLUE;
	}
}
