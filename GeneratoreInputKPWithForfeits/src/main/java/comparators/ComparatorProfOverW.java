package comparators;

import java.util.Comparator;
import algorithm.Oggetto;

public class ComparatorProfOverW implements Comparator<Oggetto> {

	@Override
	public int compare(Oggetto o1, Oggetto o2) {
		return -o1.getpOverW().compareTo(o2.getpOverW());
	}

}
