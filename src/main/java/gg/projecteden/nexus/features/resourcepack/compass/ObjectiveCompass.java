package gg.projecteden.nexus.features.resourcepack.compass;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.Env;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

@Environments(Env.TEST)
public class ObjectiveCompass extends Feature {
	private BossBar bossBar;

	@Override
	public void onStart() {
		bossBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
		Tasks.repeat(0, 1, () -> {
			final Dev griffin = Dev.GRIFFIN;
			if (!griffin.isOnline())
				return;

			final Player player = griffin.getOnlinePlayer();

			if (!bossBar.getPlayers().contains(player))
				bossBar.addPlayer(player);

			Location objective = new Location(player.getWorld(), 0, 0, 0);
			bossBar.setTitle(CompassState.of(player, objective).getCharacter());
		});
	}

	@Override
	public void onStop() {
		bossBar.setVisible(false);
		bossBar.removeAll();
	}

}
