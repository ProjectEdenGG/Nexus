package me.pugabyte.bncore.models.nerds;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "nerds")
public class Nerd {
	@NonNull
	private String uuid;
	@NonNull
	private String name;
	private Date birthday;
	private Timestamp firstJoin;
	private Timestamp lastJoin;
	private Timestamp lastQuit;

}
