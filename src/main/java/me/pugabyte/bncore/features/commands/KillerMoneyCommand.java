package me.pugabyte.bncore.features.commands;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

@Aliases("km")
@NoArgsConstructor
public class KillerMoneyCommand extends CustomCommand implements Listener {
	SettingService service = new SettingService();
	final double BOOST = 1.0;

	public KillerMoneyCommand(CommandEvent event) {
		super(event);
	}

	@Path("toggle")
	void mute() {
		Setting setting = service.get(player(), "killerMoneyMute");
		setting.setBoolean(!setting.getBoolean());
		service.save(setting);
		send(PREFIX + "Notifications have been &e" + ((setting.getBoolean()) ? "muted" : "unmuted"));
	}

	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == SpawnReason.SPAWNER || event.getSpawnReason() == SpawnReason.SPAWNER_EGG)
			event.getEntity().setMetadata("killermoney-spawner", new FixedMetadataValue(BNCore.getInstance(), true));
	}

	@EventHandler
	public void onEntityKill(EntityDamageByEntityEvent event) {
		LivingEntity victim;
		if (!(event.getEntity() instanceof LivingEntity)) return;

		victim = (LivingEntity) event.getEntity();
		Player player = null;

		if (event.getDamager() instanceof Player) {
			player = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) event.getDamager();
			if (projectile.getShooter() instanceof Player)
				player = (Player) projectile.getShooter();
		}
		if (player == null) return;

		if (victim.getHealth() - event.getFinalDamage() > 0)
			return;

		if (!player.getGameMode().equals(GameMode.SURVIVAL))
			return;

		if (event.getCause() == DamageCause.SUFFOCATION)
			return;
		for (MetadataValue meta : victim.getMetadata("killermoney-spawner"))
			if (meta.asBoolean())
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

		double money = mob.getRandomValue() * BOOST;
		BNCore.getEcon().depositPlayer(player, money);
		DecimalFormat formatter = new DecimalFormat("#.##");
		if (!new SettingService().get(player, "killerMoneyMute").getBoolean())
			player.sendMessage(StringUtils.colorize("&3You killed a " + mob.name().toLowerCase().replace("_", " ") +
					"&3 and received &e$" + formatter.format(money)));
	}

	@Getter
	public enum MobMoney {
		BAT(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		BLAZE(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		CAVE_SPIDER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		CREEPER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ELDER_GUARDIAN(20.0, 100.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ENDER_DRAGON(50.0, 150.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ENDERMAN(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ENDERMITE(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		EVOKER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		GHAST(3.0, 10.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		GUARDIAN(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		HUSK(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		MAGMA_CUBE(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		PIG_ZOMBIE(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SHULKER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SILVERFISH(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SKELETON(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SLIME(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SPIDER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SQUID(1.0, 3.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		STRAY(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		VEX(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
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
