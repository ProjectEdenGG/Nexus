package gg.projecteden.nexus.features.nameplates;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public class TameablesNameplateFix implements Listener {

	private static final String TEAM_NAME = "NEXUS_TAMEABLES";
	private static final Team TEAM = initializeTeam(TEAM_NAME);

	static {
		Tasks.repeat(TickTime.SECOND, TickTime.SECOND, () -> {
			for (World world : Bukkit.getWorlds())
				for (Entity entity : world.getEntities())
					assign(entity);
		});
	}

	@EventHandler
	public void on(EntityAddToWorldEvent event) {
		assign(event.getEntity());
	}

	@EventHandler
	public void on(EntityTameEvent event) {
		assign(event.getEntity());
	}

	private static void assign(Entity entity) {
		if (!(entity instanceof Tameable tameable))
			return;

		Team oldTeam = TeamAssigner.scoreboard().getEntityTeam(tameable);
		Team newTeam = TEAM;

		if (tameable.getOwnerUniqueId() == null) {
			if (oldTeam != null)
				oldTeam.removeEntity(tameable);
			return;
		}

		if (oldTeam == null) {
			newTeam.addEntity(tameable);
		} else if (!oldTeam.equals(newTeam)) {
			oldTeam.removeEntity(tameable);
			newTeam.addEntity(tameable);
		}
	}

	private static Team initializeTeam(String name) {
		Scoreboard scoreboard = TeamAssigner.scoreboard();
		Team team = scoreboard.getTeam(name);

		if (team != null)
			team.unregister();

		team = scoreboard.registerNewTeam(name);
		team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);

		return team;
	}

}
