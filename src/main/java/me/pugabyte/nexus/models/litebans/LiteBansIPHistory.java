package me.pugabyte.nexus.models.litebans;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Table(name = "history")
public class LiteBansIPHistory {
	@Id
	@GeneratedValue
	private long id;
	private Timestamp date;
	private String name;
	private String uuid;
	private String ip;

}
