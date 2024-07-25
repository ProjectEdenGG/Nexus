package gg.projecteden.nexus.features.events.y2024.vulan24.lantern;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.Schedule;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.vulan24.VuLan24;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestItem;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.vulan24.VuLan24LanternService;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.CompletableFuture;

@Data
@EqualsAndHashCode(callSuper = true)
@Schedule("0 */4 15-31 8 *")
public class LanternAnimationManager extends AbstractJob implements Listener {

	public static final VuLan24LanternService service = new VuLan24LanternService();
	public static final WorldGuardUtils WORLDGUARD = VuLan24.get().worldguard();

	public LanternAnimationManager() {
		Nexus.registerListener(this);
	}

	@Override
	protected CompletableFuture<JobStatus> run() {
		if (!VuLan24.get().isEventActive())
			return completed();

		long currentTime = VuLan24.get().getWorld().getTime();
		long waitTime;
		if (currentTime > 14000)
			waitTime = 24000 + (14000 - currentTime);
		else
			waitTime = 14000 - currentTime;
		Tasks.wait(waitTime, () -> new LanternAnimation().start());
		return completed();
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (!WORLDGUARD.isInRegion(event.getPlayer(), "vulan_lanternanimation_place"))
			return;

		if (CustomMaterial.of(event.getPlayer().getInventory().getItemInMainHand()) != CustomMaterial.of(VuLan24QuestItem.PAPER_LANTERN_FLOATING.get()))
			return;

		event.setCancelled(true);
		event.getPlayer().getInventory().getItemInMainHand().subtract(1);
		service.edit0(user -> user.setLanterns(user.getLanterns() + 1));
	}

	public static int getPlayerLanternsAndReset() {
		int lanterns = service.get0().getLanterns();
		service.edit0(user -> user.setLanterns(0));
		return lanterns;
	}

}
