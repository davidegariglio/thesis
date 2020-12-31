package comparators;

import java.util.Comparator;

import algorithm.Oggetto;

public class ComparatorProfMinusAVGPenOverW implements Comparator<Oggetto> {


	@Override
	public int compare(Oggetto o1, Oggetto o2) {
		return -o1.getProfMinusAVGPenOverW().compareTo(o2.getProfMinusAVGPenOverW());
	}

}
