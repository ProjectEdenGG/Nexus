package me.pugabyte.nexus.features.commands;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import me.pugabyte.nexus.models.killermoney.KillerMoney;
import me.pugabyte.nexus.models.killermoney.KillerMoneyService;
import me.pugabyte.nexus.models.setting.Setting;
import me.pugabyte.nexus.models.setting.SettingService;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.models.task.TaskService;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
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
import java.util.Map;

@Aliases("km")
@NoArgsConstructor
public class KillerMoneyCommand extends CustomCommand implements Listener {
	private static final NumberFormat formatter = NumberFormat.getCurrencyInstance();
	SettingService settingService = new SettingService();
	KillerMoneyService service = new KillerMoneyService();
	KillerMoney km;
	static double BOOST = 1;

	@Getter
	private static final String expireTaskId = "killermoney-boost-expire";

	static {
		Setting globalBoost = getSetting();
		if (globalBoost.getValue() != null) {
			try {
				if (globalBoost.getDouble() > 1)
					BOOST = globalBoost.getDouble();
			} catch (Exception ex) {
				Nexus.warn("The KillerMoney Boost in the database in invalid");
			}
		}

		Tasks.repeatAsync(Time.SECOND, Time.MINUTE, () -> {
			TaskService taskService = new TaskService();
			taskService.process(expireTaskId).forEach(task -> {
				Map<String, Object> data = task.getJson();
				OfflinePlayer player = PlayerUtils.getPlayer((String) data.get("uuid"));
				if (player.isOnline() && player.getPlayer() != null)
					PlayerUtils.send(player.getPlayer(), StringUtils.getPrefix("KillerMoney") + "Your boost has expired");

				KillerMoneyService kmService = new KillerMoneyService();
				KillerMoney km = kmService.get(player);
				km.setBoost(1);
				kmService.save(km);
				taskService.complete(task);
			});
		});
	}

	public KillerMoneyCommand(@NotNull CommandEvent event) {
		super(event);
		if (isPlayer())
			km = service.get(player());
	}

	private static Setting getSetting() {
		return new SettingService().get("killerMoney", "globalBoost");
	}

	@Permission("group.staff")
	@Path("boost <player> <amount>")
	void boostPlayer(OfflinePlayer player, double amount) {
		if (amount < 1)
			error("The boost amount cannot be less than 1");

		km = service.get(player);
		km.setBoost(amount);
		service.save(km);

		send(PREFIX + "&e" + player.getName() + "'s &3boost is now &e" + amount);
		if (player.isOnline() && !isSelf(player))
			send(player.getPlayer(), PREFIX + "Your boost is now set to &e" + amount);
	}

	@Permission("group.staff")
	@Path("boost global <amount>")
	void boostGlobal(double amount) {
		if (amount < 1)
			error("The boost amount cannot be less than 1");

		Setting setting = getSetting();
		setting.setDouble(amount);
		settingService.save(setting);
		BOOST = amount;

		send(PREFIX + "The global boost is now &e" + amount);
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

		if (!mob.getActiveWorlds().contains(WorldGroup.get(player.getWorld()))) return;

		// TODO make this enum config driven
		if (event.getEntityType() == EntityType.ENDERMAN && player.getWorld().getName().contains("the_end")) return;

		double playerBoost = 1;
		if (km.getBoost() > 1)
			playerBoost = km.getBoost();

		double money = mob.getRandomValue() * BOOST * playerBoost;
		new BankerService().deposit(player, money, ShopGroup.get(player), TransactionCause.KILLER_MONEY);
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
		SQUID(1.0, 3.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
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

		double getRandomValue() {
			return RandomUtils.randomDouble(min, max);
		}

	}


}
