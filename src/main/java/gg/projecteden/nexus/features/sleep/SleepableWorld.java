package gg.projecteden.nexus.features.sleep;

import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang.math.NumberRange;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

@Data
public class SleepableWorld {
	@NonNull String worldName;
	State state = null;
	int percent = 50;
	Integer percentOverride;

	SleepableWorld(World world) {
		this.worldName = world.getName();
	}

	SleepableWorld(@NotNull String worldName) {
		this.worldName = worldName;
	}

	public World getWorld() {
		return Bukkit.getWorld(worldName);
	}

	public int getPercent() {
		if (percentOverride != null && percentOverride >= 0)
			return percentOverride;

		return percent;
	}

	public boolean isDaylightCycleEnabled() {
		Boolean gameRuleValue = getWorld().getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE);
		return gameRuleValue != null && gameRuleValue;
	}

	public void setTime(long time) {
		getWorld().setTime(time);
	}

	public long getTime() {
		return getWorld().getTime();
	}

	public boolean isDayTime() {
		World world = getWorld();
		return !(world.getTime() >= 12541 && world.getTime() <= 23458);
	}

	public void setStorm(boolean bool) {
		getWorld().setStorm(bool);
	}

	public boolean isStorming() {
		return getWorld().hasStorm();
	}

	public void setThundering(boolean bool) {
		getWorld().setThundering(bool);
	}

	public boolean isThundering() {
		return getWorld().isThundering();
	}

	public void skipNight() {
		new SkipNightEvent(getWorld()).callEvent();
		setState(State.SKIPPING);

		setStorm(false);
		setThundering(false);

		int taskId = Tasks.repeat(0, 1, () ->
			sendActionBar("The night was skipped because " + getPercent() + "% of players slept"));

		int wait = 0;
		while (true) {
			long newTime = getTime() + (++wait * Sleep.getSPEED());
			if (!new NumberRange(12541L, (23999L - Sleep.getSPEED())).containsNumber(newTime))
				break;

			Tasks.wait(wait, () -> setTime(newTime));
		}

		Tasks.wait(wait, () -> {
			setTime(23999);
			if (isStorming())
				setStorm(false);
			if (isThundering())
				setThundering(false);
			Tasks.cancel(taskId);
			setState(null);
		});
	}

	public void sendActionBar(String message) {
		ActionBarUtils.sendActionBar(OnlinePlayers.where().world(getWorld()).get(), message);
	}

	public enum State {SLEEPING, SKIPPING, LOCKED}
}
