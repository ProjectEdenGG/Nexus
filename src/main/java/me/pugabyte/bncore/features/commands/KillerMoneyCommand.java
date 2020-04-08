package me.pugabyte.bncore.features.commands;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

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
    public void onEntityKill(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;
        if (!player.getGameMode().equals(GameMode.SURVIVAL)) return;
        try {
            MobMoney mob = MobMoney.valueOf(event.getEntityType().name());
            if (!mob.getActiveWorlds().contains(WorldGroup.get(player.getWorld()))) return;
            double money = mob.getRandomValue() * BOOST;
            BNCore.getEcon().depositPlayer(player, money);
            DecimalFormat formatter = new DecimalFormat("#.##");
            if (!new SettingService().get(player, "killerMoneyMute").getBoolean())
                player.sendMessage(StringUtils.colorize("&3You killed a " + mob.name().toLowerCase().replace("_", " ") +
                        "&3 and received &e$" + formatter.format(money)));
        } catch (IllegalArgumentException ignore) {
        }
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
            Double random = Utils.randomDouble(min, max);
            return random;
        }

    }


}
