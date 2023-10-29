package devs.mrp.springturkey.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Duple<T1,T2> {

	private T1 value1;
	private T2 value2;

}
