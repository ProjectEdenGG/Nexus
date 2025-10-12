package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import de.sfuhrm.sudoku.Creator;
import de.sfuhrm.sudoku.GameMatrix;
import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public class GameMatrixConverter extends TypeConverter implements SimpleValueConverter {

	public GameMatrixConverter(Mapper mapper) {
		super(GameMatrix.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (!(value instanceof GameMatrix matrix))
			return null;

		byte[][] array = matrix.getArray();
		List<List<Integer>> numbers = new ArrayList<>();

		for (byte[] row : array) {
			List<Integer> rowList = new ArrayList<>();
			for (byte cell : row) 
				rowList.add((int) cell);
			numbers.add(rowList);
		}

		return numbers;
	}

	@Override
	@SneakyThrows
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null)
			return null;

		List<List<Integer>> numbers = (List<List<Integer>>) value;
		byte[][] array = new byte[numbers.size()][numbers.get(0).size()];

		for (int i = 0; i < numbers.size(); i++) {
			List<Integer> row = numbers.get(i);
			for (int j = 0; j < row.size(); j++)
				array[i][j] = row.get(j).byteValue();
		}

		GameMatrix matrix = Creator.createFull();
		matrix.setAll(array);
		return matrix;
	}

}

