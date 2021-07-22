package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.boost.BoostConfig;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.models.killermoney.KillerMoney;
import gg.projecteden.nexus.models.killermoney.KillerMoneyService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

@Aliases("km")
@NoArgsConstructor
public class KillerMoneyCommand extends CustomCommand implements Listener {
	private static final NumberFormat formatter = NumberFormat.getCurrencyInstance();
	private final KillerMoneyService service = new KillerMoneyService();
	private KillerMoney km;

	public KillerMoneyCommand(@NotNull CommandEvent event) {
		super(event);
		if (isPlayer())
			km = service.get(player());
	}

	@Description("Toggle KillerMoney's chat notification")
	@Path("[enable]")
	void mute(Boolean enable) {
		if (enable == null)
			enable = !km.isMuted();

		km.setMuted(enable);
		service.save(km);

		send(PREFIX + "Notifications have been &e" + ((km.isMuted()) ? "muted" : "unmuted"));
	}

	@EventHandler
	public void onEntityKill(EntityDeathEvent event) {
		KillerMoneyService kmService = new KillerMoneyService();
		Player player = event.getEntity().getKiller();
		if (player == null) return;

		KillerMoney km = kmService.get(player);

		if (!player.getGameMode().equals(GameMode.SURVIVAL))
			return;

		if (player.getWorld().getName().contains("events"))
			return;

		if (Arrays.asList(SpawnReason.SPAWNER, SpawnReason.SPAWNER_EGG, SpawnReason.NETHER_PORTAL).contains(event.getEntity().getEntitySpawnReason()))
			return;
		if (event.getEntity().getLastDamageCause() != null)
			if (Arrays.asList(DamageCause.CRAMMING, DamageCause.SUFFOCATION).contains(event.getEntity().getLastDamageCause().getCause()))
				return;

		MobMoney mob;
		try {
			mob = MobMoney.valueOf(event.getEntityType().name());
		} catch (IllegalArgumentException ignore) {
			return;
		}

		if (!mob.getActiveWorlds().contains(WorldGroup.of(player.getWorld()))) return;

		// TODO make this enum config driven
		if (event.getEntityType() == EntityType.ENDERMAN && player.getWorld().getName().contains("the_end")) return;

		double boost = BoostConfig.multiplierOf(Boostable.KILLER_MONEY);
		double money = mob.getRandomMoney() * boost;
		new BankerService().deposit(player, money, ShopGroup.of(player), TransactionCause.KILLER_MONEY);
		if (!km.isMuted())
			send(player, "&3You killed a " + mob.name().toLowerCase().replace("_", " ") +
					"&3 and received &e" + formatter.format(money));
	}

	@Getter
	public enum MobMoney {
		BAT(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		BLAZE(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		CAVE_SPIDER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		CREEPER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		DROWNED(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ELDER_GUARDIAN(20.0, 100.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ENDER_DRAGON(50.0, 150.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ENDERMAN(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ENDERMITE(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		EVOKER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		GHAST(3.0, 10.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		GUARDIAN(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		HUSK(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ILLUSIONER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		MAGMA_CUBE(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		PIG_ZOMBIE(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		PILLAGER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		PHANTOM(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		RAVAGER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SHULKER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SILVERFISH(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SKELETON(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SLIME(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SPIDER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SQUID(.25, 1.5, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		STRAY(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		VEX(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		VINDICATOR(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		WITCH(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		WITHER_SKELETON(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ZOMBIE(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ZOMBIE_HORSE(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ZOMBIE_VILLAGER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK);


		double min;
		double max;
		List<WorldGroup> activeWorlds;

		MobMoney(double min, double max, WorldGroup... activeWorlds) {
			this.min = min;
			this.max = max;
			this.activeWorlds = Arrays.asList(activeWorlds);
		}

		double getRandomMoney() {
			return RandomUtils.randomDouble(min, max);
		}

	}


}
