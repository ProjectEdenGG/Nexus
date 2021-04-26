package me.pugabyte.nexus.models.litebans;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.utils.Utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public abstract class LiteBansPunishment {

	abstract public long getId();

	abstract public String getUuid();

	abstract public String getIp();

	abstract public String getReason();

	abstract public String getBanned_by_uuid();

	abstract public String getBanned_by_name();

	abstract public long getTime();

	abstract public long getUntil();

	abstract public boolean isSilent();

	abstract public boolean isIpban();

	abstract public boolean isActive();

	abstract public boolean isWarned();

	abstract public String getRemoved_by_uuid();

	abstract public String getRemoved_by_name();

	abstract public Timestamp getRemoved_by_date();

	abstract public String getServer_scope();

	abstract public String getServer_origin();

	abstract public boolean isIpban_wildcard();

	public LocalDateTime getReceived() {
		return Utils.epochMilli(getTime());
	}

	public int getSeconds() {
		if (getUntil() == -1)
			return 0;

		return Long.valueOf(getUntil() - getTime()).intValue() / 1000;
	}

	public LocalDateTime getRemoved() {
		if (getRemoved_by_date() == null)
			return null;

		return getRemoved_by_date().toLocalDateTime();
	}

}
