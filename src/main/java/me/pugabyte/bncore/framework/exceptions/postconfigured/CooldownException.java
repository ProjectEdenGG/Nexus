package me.pugabyte.bncore.framework.exceptions.postconfigured;

import me.pugabyte.bncore.models.cooldown.Cooldown;
import me.pugabyte.bncore.utils.StringUtils;

import java.time.LocalDateTime;

public class CooldownException extends PostConfiguredException {

	public CooldownException(Cooldown cooldown) {
		super("You can run this command again in &e" + StringUtils.timespanDiff(LocalDateTime.now(), cooldown.getExpiration()));
	}

}
