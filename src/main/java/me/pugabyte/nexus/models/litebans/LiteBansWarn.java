package me.pugabyte.nexus.models.litebans;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Table(name = "warnings")
public class LiteBansWarn extends LiteBansPunishment {
	@Id
	@GeneratedValue
	private long id;
	private String uuid;
	private String ip;
	private String reason;
	private String banned_by_uuid;
	private String banned_by_name;
	private long time;
	private long until;
	private boolean silent;
	private boolean ipban;
	private boolean active;
	private boolean warned;
	private String removed_by_uuid;
	private String removed_by_name;
	private Timestamp removed_by_date;
	private String server_scope;
	private String server_origin;
	private boolean ipban_wildcard;

	@Override
	public LocalDateTime getReceived() {
		return isWarned() ? super.getReceived() : null;
	}

}
