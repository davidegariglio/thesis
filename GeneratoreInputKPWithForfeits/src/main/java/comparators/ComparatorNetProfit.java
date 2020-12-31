package comparators;

import java.util.Comparator;

import algorithm.Oggetto;

public class ComparatorNetProfit implements Comparator<Oggetto> {

	@Override
	public int compare(Oggetto o1, Oggetto o2) {
		return o1.getNetProf().compareTo(o2.getNetProf());
	}

}
