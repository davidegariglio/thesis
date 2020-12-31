package GeneratoreInputKPWithForfeits.UI.Model;
public class Forfeit {

	private Oggetto o1;
	private Oggetto o2;
	private Double penalty;
	
	public Forfeit(Oggetto o1, Oggetto o2, Integer penalty) {
		this.o1 = o1;
		this.o2 = o2;
		this.penalty = Double.valueOf(penalty);
	}

	public Forfeit(Oggetto o1, Oggetto o2, Double penalty) {
		this.o1 = o1;
		this.o2 = o2;
		this.penalty = Double.valueOf(penalty);
	}

	public Oggetto getO1() {
		return o1;
	}

	public Oggetto getO2() {
		return o2;
	}

	public Double getPenalty() {
		return penalty;
	}

	@Override
	public String toString() {
		return this.o1.getId()+";"+this.o2.getId()+";"+this.penalty.intValue()+"\n"; 
	}

	@Override
	public boolean equals(Object other) {
		if(this.o1.equals( ((Forfeit) other).getO1()) &&  this.o2.equals( ((Forfeit) other).getO2())
				|| this.o1.equals( ((Forfeit) other).getO2()) &&  this.o2.equals( ((Forfeit) other).getO1())) {
			return true;
		}
		return false;
	}

	
}
