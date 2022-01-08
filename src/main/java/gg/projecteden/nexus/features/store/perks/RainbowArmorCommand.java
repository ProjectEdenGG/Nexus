package gg.projecteden.nexus.features.store.perks;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MinigamerQuitEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.rainbowarmor.RainbowArmor;
import gg.projecteden.nexus.models.rainbowarmor.RainbowArmorService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.ArmorSlot;
import gg.projecteden.nexus.utils.StringUtils.Rainbow;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static gg.projecteden.nexus.features.store.perks.RainbowArmorCommand.PERMISSION;
import static gg.projecteden.nexus.utils.RandomUtils.randomInt;

@NoArgsConstructor
@Permission(PERMISSION)
@Aliases({"rainbowarmour", "rba"})
public class RainbowArmorCommand extends CustomCommand implements Listener {
	public static final String PERMISSION = "rainbowarmor.use";
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
				Tasks.wait(randomInt(0, TickTime.SECOND.x(12)), rainbowArmor::start);
	}

	@Path
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

	@Path("menu")
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
	public void onMatchQuit(MinigamerQuitEvent event) {
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

	private static class RainbowArmorProvider extends MenuUtils implements InventoryProvider {
		private final RainbowArmorService service = new RainbowArmorService();

		@Override
		public void open(Player player, int page) {
			SmartInventory.builder()
				.provider(this)
				.size(6, 9)
				.title(Rainbow.apply("Rainbow Armor"))
				.build()
				.open(player, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			final RainbowArmor user = service.get(player);

			addCloseItem(contents);

			for (ArmorSlot slot : ArmorSlot.values()) {
				final ItemBuilder other;
				if (user.isSlotEnabled(slot))
					other = new ItemBuilder(user.getShownIcon(slot)).name("&aShown").lore("&cClick to hide");
				else
					other = new ItemBuilder(user.getHiddenIcon(slot)).name("&cHidden").lore("&aClick to show");

				contents.set(slot.ordinal() + 1, 4, ClickableItem.from(other.build(), e -> {
					user.toggleSlot(slot);
					service.save(user);
					open(player);
				}));
			}
		}
	}

}
