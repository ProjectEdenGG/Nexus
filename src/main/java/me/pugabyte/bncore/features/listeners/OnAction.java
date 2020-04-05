package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnAction implements Listener {

    WarpService warpService = new WarpService();
    SettingService settingService = new SettingService();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getUniqueId().toString().equalsIgnoreCase("e2bb9244-d480-4900-b501-7d176cbd919c") ||
                event.getPlayer().getUniqueId().toString().equalsIgnoreCase("4f73321b-2d48-4517-bd54-f3ab43640020")) {
            Setting setting = settingService.get(event.getPlayer(), "endTP");
            if (!setting.getBoolean()) {
                warpService.get("spawn", WarpType.NORMAL).teleport(event.getPlayer());
                event.getPlayer().sendMessage(StringUtils.colorize("You were teleported to spawn because you were in the End, which was reset."));
                setting.setBoolean(true);
                settingService.save(setting);
            }
        }
        if (event.getPlayer().getUniqueId().toString().equalsIgnoreCase("5bff3b47-06f3-4766-9468-edfe19266997")) {
            Setting setting = settingService.get(event.getPlayer(), "s6oobertTP");
            if (!setting.getBoolean()) {
                Utils.runCommand(event.getPlayer(), "home");
                setting.setBoolean(true);
                settingService.save(setting);
            }
        }
    }

}
