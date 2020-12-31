package GeneratoreInputKPWithForfeits.UI.Model;

public class Oggetto {

	private Integer id;
	private String name;
	private Double profit;
	private Double weight;
	
	public Oggetto(Integer id, String name, Double profit, Double weight) {
		this.id = id;
		this.name = name;
		this.profit = profit;
		this.weight = weight;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Double getProfit() {
		return profit;
	}

	public Double getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return this.name+" profitto:"+this.profit+" peso:"+this.weight;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Oggetto other = (Oggetto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	

}
