package algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import comparators.ComparatorAVGPenalty;
import comparators.ComparatorProfMinusAVGPenOverW;
import comparators.ComparatorProfOverW;

//MODEL ALGORITMO

public class Model {

	private int nOggetti;
	private int capacity;
	private int nForfeits;
	private Map<Integer, Oggetto> itemMap;
	private long start;
	private long end;
	private long bestTime;
	private int tabuSize = 100;
	
	// Siccome applichiamo la filosofia multistart, partiamo da liste ordinate in maniera differente 
	// in modo da costruire tra diverse soluzioni di partenza
	private List<Oggetto> itemSortedByAVGPenalty;
	private List<Oggetto> itemSortedByProfOverW;
	private List<Oggetto> itemSortedByProfMinusAVGPenOverW;
	private List<Oggetto> itemSortedRandomly;

	
	private List<Oggetto> initialCandidates;

	private Solution opt;
	
	private List<Oggetto> tabuList;
	
	public Model() {
		this.itemMap = new HashMap<>();
		
		}
	
	public void execute(String fileName) {
		readInput(fileName);
		//Verifica lettura oggetti
		/*
		for(Integer id : this.itemMap.keySet()) {
			System.out.println("X"+id+" prof:"+itemMap.get(id).getProf()+" weight:"+itemMap.get(id).getPeso());
		}
		*/
		this.sortItemsByAVGPenalty();
		this.sortItemsByProfOverW();
		this.sortItemsByProfMinusAVGPenOverW();
		this.sortItemsRandomly();
		/*Controllo ordinamento
		for(Oggetto item : this.itemSortedByAVGPenalty) {
			System.out.println("X"+item.getId()+" "+item.getForfMedio());
		}
		*/
		
		this.runAlgorithm(this.itemSortedByAVGPenalty, "AVGPen");
		this.runAlgorithm(this.itemSortedByProfOverW, "Prof/Weight");
		this.runAlgorithm(this.itemSortedByProfMinusAVGPenOverW, "(Prof-AVGPen)/Weight");
		this.runAlgorithm(this.itemSortedRandomly, "Random sorting");

 	}

	private void runAlgorithm(List<Oggetto> itemsSortedByCriteria, String type) {
		
		//Generating header in output file
		this.tabuList = new ArrayList<>();
		
		//Controllo superfluo in molti casi. Seleziono sono gli item che possono stare nello zaino!
		this.initialCandidates = new ArrayList<> (cleanCandidates(itemsSortedByCriteria, this.capacity));
		
		Solution current = buildInitialSolution(this.capacity, this.initialCandidates);
		current.removeItemsWithNetProfitLEThanZero();
		this.start = System.currentTimeMillis();
		this.end = start + (30*1000);
		//Init of current solution as starting one
		this.opt = current;
		
		Integer totIt = 0;
		Integer usfIt = 0;
		Double initialObj = current.getObjFunction();
		
		while(System.currentTimeMillis() < end) {
			totIt++;
			boolean removed = false;
			List<Oggetto> candidates = new ArrayList<>(cleanCandidates(this.initialCandidates, current.getResidualCapacity()));
			candidates.removeAll(current.getItemSet());
			//Oggetti ordinati per profitto netto crescente
			List<Oggetto> leavingItems = new ArrayList<>(current.getworstItems());
			for(Oggetto candidateLeaving : leavingItems) {
				//Re inizializzo leavingItems per iterazioni successive alla prima
				leavingItems = new ArrayList<>(current.getworstItems());
				Solution improved = new Solution(current);
				improved.removeItem(candidateLeaving);
				
				if(this.tabuList.size()<this.tabuSize) {
					this.tabuList.add(candidateLeaving);
					//Swap occured
					removed = true;
				}
				else {
					//FIFO approach
					this.tabuList.remove(0);
					this.tabuList.add(candidateLeaving);
					//Swap occured
					removed = true;
				}
				
				//Ora provo a inserire elementi
				//scremo tutti i candidati che ci stanno nella nuova possibile soluzione
				candidates = new ArrayList<>(this.cleanCandidates(candidates, improved.getResidualCapacity()));
				candidates.removeAll(improved.getItemSet());
				for(Oggetto candidateEntering : candidates) {
					//Aggiunta di tutti quelli che ci stanno
					if(!this.tabuList.contains(candidateEntering) && candidateEntering.getPeso() <= improved.getResidualCapacity() && candidateEntering.getNetProfitAddingItToSol(improved) > 0) {
						improved.addItem(candidateEntering);
													
					}
				}
				
				if(improved.getObjFunction() > opt.getObjFunction()) {
					usfIt++;
					this.bestTime = System.currentTimeMillis();
					opt = new Solution(improved);
					current = new Solution(opt);
					System.out.println("*****New solution found!*****");
					System.out.println("Leaving:"+ candidateLeaving);
					System.out.println("New OBJ: = "+ current.getObjFunction());
					System.out.println("#Items = " + current.getItemSet().size());
					System.out.println("Items: " + current.getItemSet());
					System.out.println("Residual capacity = " + current.getResidualCapacity()+"\n\n");
					break;
				}
					
				current = new Solution(improved);
				if(removed) break;
			}
		}
		System.out.println("TIME FINISHED!");
		System.out.println("Obj.= "+opt.getObjFunction());
		System.out.println(opt);
		System.out.println("Res capacity = "+opt.getResidualCapacity());
		Double time = (this.bestTime-this.start)/Double.valueOf(1000);
		System.out.println("Result found in: "+time+"s.");
		
		Double improvement = ( (this.opt.getObjFunction() - initialObj) / Math.abs(initialObj))*100;
		this.writeResultsOnFile(type, Double.toString(initialObj), Double.toString(opt.getObjFunction()), Double.toString(time), totIt, usfIt, improvement);

	}
	

	public void writeResultsOnFile(String type, String initialObj, String objective, String time, Integer totIt, Integer usfIt, Double percOfIncrement) { 
		try {
		      FileWriter f = new FileWriter("C:\\Users\\garig\\workspace_tesi\\GeneratoreInputKPWithForfeits\\results.txt", true);
				BufferedWriter bw = new BufferedWriter(f);
			    PrintWriter out = new PrintWriter(bw);
		        final Object[][] table = new String[1][7];
		        table[0] = new String [] {type, initialObj, objective, time, Integer.toString(totIt), Integer.toString(usfIt), Double.toString(percOfIncrement)};
		        for(final Object[] row : table) {
			        out.println(String.format("%-20s%-25s%-20s%-20s%-20s%-20s%-20s\n", row));
		        }
	            out.close();
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}

	private void sortItemsByAVGPenalty() {
		this.itemSortedByAVGPenalty = new ArrayList<>(itemMap.values());
		for(Oggetto item : this.itemSortedByAVGPenalty) {
			item.calcolaForfeitMedio();
		}
		Collections.sort(itemSortedByAVGPenalty, new ComparatorAVGPenalty());
	}
	
	private void sortItemsByProfOverW() {
		this.itemSortedByProfOverW = new ArrayList<>(itemMap.values());
		Collections.sort(itemSortedByProfOverW, new ComparatorProfOverW());
	}
	
	private void sortItemsByProfMinusAVGPenOverW() {
		this.itemSortedByProfMinusAVGPenOverW = new ArrayList<>(itemMap.values());
		for(Oggetto item : this.itemSortedByProfMinusAVGPenOverW) {
			item.calcolaProfMinusAVGPenOverW();;
		}
		Collections.sort(itemSortedByProfMinusAVGPenOverW, new ComparatorProfMinusAVGPenOverW());
	}

	private void sortItemsRandomly() {	
		Random r = new Random(System.currentTimeMillis());
		this.itemSortedRandomly = new ArrayList<>(itemMap.values());
		Collections.shuffle(itemSortedRandomly, r);
	}
	
	/**
	 * 
	 * @param initialCandidates
	 * @param capacity2
	 */
	private List<Oggetto> cleanCandidates(List<Oggetto> sortedItems, int capacity) {
		List<Oggetto> result = new ArrayList<>();
		for(Oggetto o : sortedItems) {
			if(o.getPeso() <= capacity)
				result.add(o);
		}
		return result;
	}


	private Solution buildInitialSolution(Integer capacity, List<Oggetto> candidates) {
		Solution initial = new Solution(capacity);
		for(Oggetto item : candidates) {
			if(item.getPeso() <= initial.getResidualCapacity() && item.getNetProfitAddingItToSol(initial) > 0) {
				initial.addItem(item);
			}
		}
		//TODO: Togliere items con prof netto < 0
		return initial;
	}
	private void readInput(String fileName) {
		
		try {
			
			FileReader f = new FileReader ("instances/"+fileName);
			BufferedReader br = new BufferedReader (f);
			
			System.out.println("Reading data from " + fileName);
			
			String row;
			
			
			Integer nRow = 0;
			Integer nObj = 1;

			while((row = br.readLine()) != null) {
				
				String array[] = row.split(";");
				
				//Prima riga = nOggetti
				if(nRow == 0) {
					if(array.length != 1) {
						System.out.println("ATTENZIONE! Input numero oggetti nel formato errato!");
						return;
					}
					this.nOggetti = Integer.parseInt(array[0]);
				}
				
				//Seconda riga = max capacit�
				else if(nRow == 1) {
					if(array.length != 1) {
						System.out.println("ATTENZIONE! Input capcit� nel formato errato!");
						return;
					}
					this.capacity= Integer.parseInt(array[0]);
				}
				
				//Terza riga = nForfeits
				else if(nRow == 2) {
					if(array.length != 1) {
						System.out.println("ATTENZIONE! Input numero forfeits nel formato errato!");
						return;
					}
					this.nForfeits = Integer.parseInt(array[0]);
				}
				
				//Acquisizione profitti e pesi degli oggetti
				else if(nRow > 2 && nRow <= 2 + this.nOggetti) {
					if(array.length != 2) {
						System.out.println("ATTENZIONE! Input profitti e pesi oggetti nel formato errato!");
						return;
					}
					Oggetto nuovo = new Oggetto(nObj++, Integer.parseInt(array[0]), Integer.parseInt(array[1]));
					this.itemMap.put(nuovo.getId(), nuovo);
				}
				
				//Se nessuno dei casi precedenti, sono nel "settore" dei forfeits
				else {
					if(array.length != 3 && array[0].compareTo("")!=0) {
						System.out.println(nRow+" ATTENZIONE! Input forfeits nel formato errato!");
						for(int i = 0; i < array.length; i++) {
							
							System.out.println("Cella numero: "+i+":"+array[i]);
						}
						return;
						}
					if(array[0].compareTo("")!=0) {
						Oggetto primo = this.itemMap.get(Integer.parseInt(array[0]));
						Oggetto secondo = this.itemMap.get(Integer.parseInt(array[1]));
						
						//Controllo che il forfeit no sia gi� stato inserito
						if(!primo.containsForfeit(secondo) && !secondo.containsForfeit(primo)) {
							primo.addConflict(secondo, Integer.parseInt(array[2]));
							secondo.addConflict(primo, Integer.parseInt(array[2]));
						}
						else {
							this.nForfeits--;
						}
					}
				}
				
				nRow++;
			}
			br.close();
			System.out.println("Read completed.");
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
