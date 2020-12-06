package me.pugabyte.nexus.models.radio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RadioSong {
	private String name;
	private File file;

}
