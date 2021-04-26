package me.pugabyte.nexus.features.commands.staff.moderator.justice.misc;

import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.delayedban.DelayedBan;
import me.pugabyte.nexus.models.delayedban.DelayedBanService;
import me.pugabyte.nexus.models.litebans.LiteBansBan;
import me.pugabyte.nexus.models.litebans.LiteBansIPHistory;
import me.pugabyte.nexus.models.litebans.LiteBansKick;
import me.pugabyte.nexus.models.litebans.LiteBansMute;
import me.pugabyte.nexus.models.litebans.LiteBansPunishment;
import me.pugabyte.nexus.models.litebans.LiteBansService;
import me.pugabyte.nexus.models.litebans.LiteBansWarn;
import me.pugabyte.nexus.models.punishments.Punishment;
import me.pugabyte.nexus.models.punishments.PunishmentType;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.models.punishments.PunishmentsService;
import me.pugabyte.nexus.models.watchlist.Watchlisted;
import me.pugabyte.nexus.models.watchlist.WatchlistedService;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils;

import java.util.List;
import java.util.UUID;

import static me.pugabyte.nexus.utils.StringUtils.isUuid;

@Permission("group.admin")
public class JusticeImportCommand extends CustomCommand {

	public JusticeImportCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path
	void run() {
		Nexus.log("==========================================================================");
		litebans();
		ipHistory();
		delayedBans();
		watchlist();

		PunishmentsService service = new PunishmentsService();
		Nexus.log("Saving " + service.getCache().size() + " objects");
		service.saveCacheSync();
		Nexus.log("Complete");
		Nexus.log("==========================================================================");
	}

	private void litebans() {
		litebans(LiteBansBan.class);
		litebans(LiteBansMute.class);
		litebans(LiteBansKick.class);
		litebans(LiteBansWarn.class);
	}

	private void ipHistory() {
		int count = 0;
		List<LiteBansIPHistory> ipHistory = new LiteBansService().getIpHistory();

		Nexus.log("Converting " + ipHistory.size() + " ips");

		for (LiteBansIPHistory entry : ipHistory) {
			if (isNullOrEmpty(entry.getUuid()) || isNullOrEmpty(entry.getIp()) || entry.getIp().startsWith("#"))
				continue;

			Punishments.of(entry.getUuid()).logIp(entry.getIp(), entry.getDate().toLocalDateTime());
			++count;
		}

		Nexus.log(" Converted " + count);
		Nexus.log("   Skipped " + (ipHistory.size() - count));
		Nexus.log("");
	}

	private void delayedBans() {
		int count = 0;
		List<DelayedBan> bans = new DelayedBanService().getAll();

		Nexus.log("Converting " + bans.size() + " delayed bans");

		for (DelayedBan ban : bans) {
			Punishments.of(ban.getUuid()).addImport(Punishment.ofType(PunishmentType.BAN)
					.punisher(ban.getUuid_staff())
					.input(ban.getDuration() + " " + ban.getReason()));
			++count;
		}

		Nexus.log(" Converted " + count);
		Nexus.log("   Skipped " + (bans.size() - count));
		Nexus.log("");
	}

	private void watchlist() {
		int count = 0;
		List<Watchlisted> watched = new WatchlistedService().getAll();

		Nexus.log("Converting " + watched.size() + " watchlists");

		for (Watchlisted watchlisted : watched) {
			Punishment punishment = new Punishment();
			punishment.setType(PunishmentType.WATCHLIST);
			punishment.setId(UUID.randomUUID());
			punishment.setUuid(watchlisted.getUuid());
			punishment.setPunisher(watchlisted.getWatchlister());
			punishment.setTimestamp(watchlisted.getWatchlistedOn());
			punishment.setReason(watchlisted.getReason());
			punishment.setActive(true);

			Punishments.of(watchlisted.getUuid()).getPunishments().add(punishment);
			++count;
		}

		Nexus.log(" Converted " + count);
		Nexus.log("   Skipped " + (watched.size() - count));
		Nexus.log("");
	}

	private void litebans(Class<? extends LiteBansPunishment> clazz) {
		int count = 0;
		List<? extends LiteBansPunishment> punishments = new LiteBansService().getAllPunishments(clazz);

		String type = clazz.getSimpleName().replace("LiteBans", "").toLowerCase();
		Nexus.log("Converting " + punishments.size() + " " + type + "s");

		for (LiteBansPunishment entry : punishments) {
			if (isNullOrEmpty(entry.getUuid()) || entry.getUuid().startsWith("#"))
				continue;

			Punishment punishment = new Punishment();
			punishment.setId(UUID.randomUUID());
			punishment.setUuid(parseUuid(entry.getUuid()));
			punishment.setPunisher(parseUuid(entry.getBanned_by_uuid()));
			punishment.setType(PunishmentType.valueOf(type.toUpperCase()));
			punishment.setReason(entry.getReason());
			punishment.setActive(entry.isActive());
			punishment.setTimestamp(Utils.epochMilli(entry.getTime()));
			punishment.setExpiration(Utils.epochMilli(entry.getUntil()));
			punishment.setSeconds(entry.getSeconds());
			punishment.setRemover(parseUuid(entry.getRemoved_by_uuid()));
			punishment.setRemoved(entry.getRemoved());
			punishment.setReceived(entry.getReceived());

			if (entry.isIpban())
				punishment.setType(PunishmentType.ALT_BAN);

			if (punishment.getRemoved() != null && punishment.getRemover() == null)
				punishment.setRemover(StringUtils.getUUID0());

			Punishments.of(punishment.getUuid()).getPunishments().add(punishment);
			++count;
		}

		Nexus.log(" Converted " + count);
		Nexus.log("   Skipped " + (punishments.size() - count));
		Nexus.log("");
	}

	private UUID parseUuid(String uuid) {
		if (uuid == null)
			return null;

		if (uuid.startsWith("#"))
			return null;

		if (uuid.equals("CONSOLE"))
			return Nexus.getUUID0();

		if (isUuid(uuid))
			return UUID.fromString(uuid);

		return null;
	}

}
