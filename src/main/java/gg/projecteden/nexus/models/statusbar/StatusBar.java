package gg.projecteden.nexus.models.statusbar;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.chat.Chat.StaticChannel;
import gg.projecteden.nexus.features.scoreboard.ScoreboardLine;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.chat.Channel;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.chat.PublicChannel;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.UUID;

@Data
@Entity(value = "status_bar", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class StatusBar implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled;

	private transient int taskId;
	private transient BossBar bossBar;

	public void start() {
		if (!enabled)
			return;

		if (!isOnline())
			return;

		stop();

		bossBar = Bukkit.createBossBar(getText(), BarColor.BLUE, BarStyle.SEGMENTED_6);
		bossBar.addPlayer(getOnlinePlayer());
		taskId = Tasks.repeat(0, TickTime.SECOND, () -> {
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
		return StringUtils.colorize(
				ScoreboardLine.PING.render(getOnlinePlayer()) + "  &8&l|  " +
				ScoreboardLine.TPS.render(getOnlinePlayer()) + "  &8&l|  " +
				ScoreboardLine.CHANNEL.render(getOnlinePlayer())
		);
	}

	public static BarColor getColor(UUID uuid) {
		Chatter chatter = new ChatterService().get(uuid);
		Channel activeChannel = chatter.getActiveChannel();
		if (activeChannel instanceof PublicChannel channel) {
			if (StaticChannel.GLOBAL.getChannel().equals(channel))
				return BarColor.GREEN;
			else if (StaticChannel.LOCAL.getChannel().equals(channel))
				return BarColor.YELLOW;
			else if (StaticChannel.STAFF.getChannel().equals(channel))
				return BarColor.WHITE;
			else if (StaticChannel.PARTY.getChannel().equals(channel))
				return BarColor.PURPLE;
		}

		return BarColor.BLUE;
	}
}
