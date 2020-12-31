package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import comparators.ComparatorNetProfit;

public class Solution {
	
	private Set<Oggetto> itemSet;
	private Double objFunction;
	private Integer capMax;
	
	public Solution(Integer capMax) {
		this.itemSet = new HashSet<>();
		this.objFunction = 0.0;
		this.capMax = capMax;
	}
	
	public Solution(Solution previous) {
		this.itemSet = previous.getItemSet();
		this.objFunction = previous.getObjFunction();
		this.capMax = previous.getCapMax();
	}
	public Set<Oggetto> getItemSet(){
		return this.itemSet;
	}
	
	public Double getObjFunction() {
		return objFunction;
	}
	public Integer getCapMax() {
		return capMax;
	}
	public Integer getUsedCapacity() {
		Integer result = 0;
		for(Oggetto item : this.itemSet) {
			result += item.getPeso();
		}
		return result;
	}
	public Integer getResidualCapacity() {
		return this.getCapMax()-this.getUsedCapacity();
	}
	/**
	 * Aggiunge oggetto alla soluzione corrente, modificando la f. ob. in funzione del profitto e dei forfeits.
	 * Non verifica che l'oggetto superi la capacit�
	 * @param item da inserire
	 */
	public void addItem(Oggetto item) {
		Double penalty = 0.0;
		for(Oggetto alreadyInserted : this.itemSet) {
			// Acquisisco i conflitti con gli altri item nella soluzione
			Double pen = alreadyInserted.getPenalitaCon(item);
			// Aggiorno prof. netto di ogni elemento in conflitto. Se non in conflitto pen = 0.0
			alreadyInserted.setNetProf(alreadyInserted.getNetProf()-pen);
			penalty += pen;
		}
		this.itemSet.add(item);
		this.objFunction += item.getProf();
		this.objFunction -= penalty;
		item.setNetProf(item.getProf()-penalty);
	}
	
	public void removeItem(Oggetto o) {
		Double gain = 0.0;
		for(Oggetto alreadyInserted : this.itemSet) {
			if(!alreadyInserted.equals(o)) {
				alreadyInserted.setNetProf(alreadyInserted.getNetProf() + alreadyInserted.getPenalitaCon(o));
				gain += alreadyInserted.getPenalitaCon(o);
			}
		}
		this.itemSet.remove(o);
		this.objFunction += gain;
		this.objFunction -= o.getProf();
		
		o.setNetProf(0.0);
	}
	
	@Override
	public String toString() {
		return this.itemSet.toString();
	}

	// Return sorted list of the items ordered by net profit
	public List<Oggetto> getworstItems() {
		List<Oggetto> result = new ArrayList<>(this.itemSet);
		Collections.sort(result , new ComparatorNetProfit());
		return result;
	}

	public void removeItemsWithNetProfitLEThanZero() {
		Set<Oggetto> toRemove = new HashSet<>();
		for(Oggetto item : this.itemSet) {
			if(item.getNetProf() < 0) {
				toRemove.add(item);
			}
		}
		for(Oggetto tr : toRemove) {
			this.removeItem(tr);
		}
	}

}
