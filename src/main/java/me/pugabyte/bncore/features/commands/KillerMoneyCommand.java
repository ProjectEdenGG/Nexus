package me.pugabyte.bncore.features.commands;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Description;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.killermoney.KillerMoney;
import me.pugabyte.bncore.models.killermoney.KillerMoneyService;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.models.task.TaskService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

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
				BNCore.warn("The KillerMoney Boost in the database in invalid");
			}
		}

		Tasks.repeatAsync(Time.SECOND, Time.MINUTE, () -> {
			TaskService taskService = new TaskService();
			taskService.process(expireTaskId).forEach(task -> {
				Map<String, Object> data = task.getJson();
				OfflinePlayer player = Utils.getPlayer((String) data.get("uuid"));
				if (player.isOnline() && player.getPlayer() != null)
					player.getPlayer().sendMessage(colorize(StringUtils.getPrefix("KillerMoney") + "Your boost has expired"));

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
	public void onEntitySpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == SpawnReason.SPAWNER || event.getSpawnReason() == SpawnReason.SPAWNER_EGG)
			event.getEntity().setMetadata("killermoney-spawner", new FixedMetadataValue(BNCore.getInstance(), true));
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)) return;
		event.getEntity().setMetadata("killermoney-lastDamageCause", new FixedMetadataValue(BNCore.getInstance(), event.getCause().name()));
	}

	@EventHandler
	public void onEntityKill(EntityDeathEvent event) {
		KillerMoneyService kmService = new KillerMoneyService();
		Player player = event.getEntity().getKiller();
		if (player == null) return;

		KillerMoney km = kmService.get(player);

		if (!player.getGameMode().equals(GameMode.SURVIVAL))
			return;

		for (MetadataValue meta : event.getEntity().getMetadata("killermoney-spawner"))
			if (meta.asBoolean())
				return;
		for (MetadataValue meta : event.getEntity().getMetadata("killermoney-lastDamageCause"))
			if (Arrays.asList(DamageCause.CRAMMING.name(), DamageCause.SUFFOCATION.name()).contains(meta.asString()))
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
		BNCore.getEcon().depositPlayer(player, money);
		if (!km.isMuted())
			player.sendMessage(colorize("&3You killed a " + mob.name().toLowerCase().replace("_", " ") +
					"&3 and received &e" + formatter.format(money)));
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
			return Utils.randomDouble(min, max);
		}

	}


}
