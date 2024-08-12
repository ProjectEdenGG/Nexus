package gg.projecteden.nexus.models.compass;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24QuestItem;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.statusbar.StatusBar;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.UUID;

@Data
@Entity(value = "compass", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Compass implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled;

	public boolean isEnabled() {
		try {
			if (isOnline() && Pugmas24.get().isAtEvent(getLocation()))
				return Pugmas24QuestItem.canUseCompass(getOnlinePlayer());
		} catch (Exception ignore) {}

		return enabled;
	}

	private transient int taskId;
	private transient BossBar bossBar;

	public void start() {
		stop();

		if (!isEnabled())
			return;

		if (!isOnline())
			return;

		bossBar = Bukkit.createBossBar(getCompass(), BarColor.BLUE, BarStyle.SEGMENTED_6);
		bossBar.addPlayer(getOnlinePlayer());
		taskId = Tasks.repeatAsync(0, 2, () -> {
			if (!isOnline())
				stop();
			else if (bossBar != null) {
				bossBar.setColor(StatusBar.getColor(uuid));
				bossBar.setTitle(getCompass());
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

	public String getCompass() {
		return StringUtils.compass(getOnlinePlayer(), 14, 6);
	}
}
