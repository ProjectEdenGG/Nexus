package me.pugabyte.nexus.models.litebans;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Table(name = "bans")
public class LiteBansBan extends LiteBansPunishment {
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
	private String removed_by_uuid;
	private String removed_by_name;
	private Timestamp removed_by_date;
	private String server_scope;
	private String server_origin;
	private boolean ipban_wildcard;

	@Override
	public boolean isWarned() {
		return true;
	}

}
