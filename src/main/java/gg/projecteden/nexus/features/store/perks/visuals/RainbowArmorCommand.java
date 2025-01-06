package gg.projecteden.nexus.features.store.perks.visuals;

import com.google.common.util.concurrent.AtomicDouble;
import gg.projecteden.api.common.utils.MathUtils;
import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.rainbowarmor.RainbowArmor;
import gg.projecteden.nexus.models.rainbowarmor.RainbowArmorService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.ArmorSlot;
import gg.projecteden.nexus.utils.StringUtils.Rainbow;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.DecimalFormat;

@NoArgsConstructor
@Permission(RainbowArmorCommand.PERMISSION)
@Aliases({"rainbowarmour", "rba"})
@WikiConfig(rank = "Store", feature = "Visuals")
public class RainbowArmorCommand extends CustomCommand implements Listener {
	public static final String PERMISSION = "rainbowarmor.use";
	private static final DecimalFormat df = new DecimalFormat("0.0");
	private static final double minSpeed = 0.1;
	private static final double maxSpeed = 3.0;
	private final RainbowArmorService service = new RainbowArmorService();
	private RainbowArmor rainbowArmor;

	public RainbowArmorCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			rainbowArmor = service.get(player());
	}

	static {
		for (RainbowArmor rainbowArmor : new RainbowArmorService().getOnline())
			if (rainbowArmor.isEnabled())
				Tasks.wait(RandomUtils.randomLong(0, TickTime.SECOND.x(12)), rainbowArmor::start);
	}

	@Path
	@Description("Toggle rainbow armor")
	void toggle() {
		if (rainbowArmor.isNotAllowed())
			error("You cannot use Rainbow Armor here");

		if (rainbowArmor.isEnabled()) {
			rainbowArmor.stop();
			send(PREFIX + "&cDisabled");
			rainbowArmor.setEnabled(false);
		} else {
			rainbowArmor.start();
			rainbowArmor.setEnabled(true);
			send(PREFIX + Rainbow.apply("Enabled"));
		}

		service.save(rainbowArmor);
	}

	@Path("speed <speed>")
	@Description("Modify the animation speed")
	void speed(@Arg(value = "1.0", min = minSpeed, max = maxSpeed, minMaxBypass = Group.ADMIN) double speed) {
		rainbowArmor.setSpeed(speed);
		service.save(rainbowArmor);

		send(PREFIX + "Set speed to " + speed);
		rainbowArmor.start();
	}

	@Path("menu")
	@Description("Open the rainbow armor configuration menu")
	void menu() {
		new RainbowArmorProvider().open(player());
	}

	// Stop
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		stop(event.getPlayer());
	}

	@EventHandler
	public void onMatchJoin(MatchJoinEvent event) {
		stop(event.getMinigamer().getPlayer());
	}

	// Start
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		start(event.getPlayer());
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		start(event.getPlayer());
	}

	@EventHandler
	public void onMatchQuit(MatchQuitEvent event) {
		start(event.getMinigamer().getPlayer());
	}

	private void start(Player player) {
		RainbowArmor rainbowArmor = new RainbowArmorService().get(player);
		if (rainbowArmor.isEnabled())
			rainbowArmor.start();
	}

	private void stop(Player player) {
		new RainbowArmorService().get(player).stop();
	}

	@Rows(3)
	private static class RainbowArmorProvider extends InventoryProvider {
		private final RainbowArmorService service = new RainbowArmorService();

		@Override
		public String getTitle() {
			return Rainbow.apply("Rainbow Armor");
		}

		@Override
		public void init() {
			final RainbowArmor user = service.get(viewer);

			addCloseItem();

			for (ArmorSlot slot : ArmorSlot.values()) {
				final ItemBuilder other;
				if (user.isSlotEnabled(slot))
					other = new ItemBuilder(user.getShownIcon(slot)).name("&aShown").lore("&cClick to hide");
				else
					other = new ItemBuilder(user.getHiddenIcon(slot)).name("&cHidden").lore("&aClick to show");

				contents.set(new SlotPos(1, slot.ordinal() + 1), ClickableItem.of(other.build(), e -> {
					user.toggleSlot(slot);
					service.save(user);
					open(viewer);
				}));
			}

			AtomicDouble userSpeed = new AtomicDouble(user.getSpeed());
			ItemBuilder speed = new ItemBuilder(Material.RABBIT_FOOT).name("&3Speed: &e" + df.format(userSpeed)).lore("&a+&f/&c-");
			contents.set(new SlotPos(1, 6), ClickableItem.of(speed.build(), e -> {
				if (e.isAnyLeftClick())
					userSpeed.getAndAdd(0.1);
				else if (e.isAnyRightClick())
					userSpeed.getAndAdd(-0.1);

				user.setSpeed(MathUtils.clamp(userSpeed.get(), minSpeed, maxSpeed));
				service.save(user);
				user.start();
				open(viewer);
			}));
		}
	}

}
