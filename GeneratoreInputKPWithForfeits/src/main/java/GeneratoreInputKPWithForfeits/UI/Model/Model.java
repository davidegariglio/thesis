package GeneratoreInputKPWithForfeits.UI.Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import GeneratoreInputKPWithForfeits.UI.Model.Oggetto;
import ilog.concert.*;
import ilog.cplex.*;
import ilog.cplex.IloCplex;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ilog.cplex.IloCplex;

//	MODEL GENERATORE E SOLVER

public class Model {
	
	private Integer nInstances = 0;
	
	private Integer instancesUpToNow = 0;
	
	private Integer totInstance = 0;
	
	private Integer nOggetti;

	private Integer capacity;
	
	private Integer nForfeits;
	
	private Random r;
	
	private Integer profMin;
	
	private Integer profMax;
	
	private Integer pesoMin;
	
	private Integer pesoMax;
	
	private Integer penalitaMin;
	
	private Integer penalitaMax;
	
	private Integer moltForfeit;
	
	private Integer moltCapacita;
	
	private algorithm.Model algorithm;
	
	private List<Forfeit> forfeits;
	
	List<Oggetto> lista;
		
	Map<Integer, Oggetto> mappaOggetti;

	
	public Model() {
		this.algorithm = new algorithm.Model();
		this.forfeits = new ArrayList<>();
		this.lista = new ArrayList<>();
		
	}
	
	public boolean generaInput(Integer nInstances, Integer n, Integer moltForfeit, Integer lBPesoOgg, Integer uBPesoOgg, Integer lBProfOgg, Integer uBProfOgg, Integer lBPenalitaForfeit, Integer uBPenalitaForfeit) {
		boolean correct = false;
		this.nInstances = nInstances;
		this.totInstance += nInstances;
		this.nOggetti = n;
		this.profMin = lBProfOgg;
		this.profMax = uBProfOgg;
		this.pesoMin = lBPesoOgg;
		this.pesoMax = uBPesoOgg;
		this.penalitaMin = lBPenalitaForfeit;
		this.penalitaMax = uBPenalitaForfeit;
		this.moltForfeit = moltForfeit;
		//this.moltCapacita = moltCapacita;
		
		
		//Dati derivati da input
		
		//this.capacity = n * this.moltCapacita;
		
		this.nForfeits = n * this.moltForfeit;
		
		for(int i = this.instancesUpToNow; i < this.totInstance; i++) {
			
			this.r = new Random(System.currentTimeMillis());

		//Variabili in input
			
			String items = this.generaOggetti(profMin, profMax, pesoMin, pesoMax);

			String result = this.nOggetti+"\n"+this.getCapacityFormItems(items)+"\n"+this.nForfeits+"\n";
			
		    //Genero Profitti e Pesi degli oggetti
			result += items;
			result += this.generaForfeits(penalitaMin, penalitaMax);
			
			//Una volta generato l'input, lo scrivo!
			try {
				//TODO: correggere nome file
				File file = new File("C:\\Users\\garig\\git\\thesis\\GeneratoreInputKPWithForfeits\\instances/instance"+(i+1)+".txt");
			    System.out.println("Absolute path:" + file.getAbsolutePath());
			    if (!file.exists()) {
			        if (file.createNewFile()) {
			            PrintWriter out = new PrintWriter(file);
			            out.println(result);
			            out.close();
			        }
			    }
			}
			catch(Exception e) {
				System.out.println("ERRORE!!!");
				correct = false;
			}
			correct = true;
		}
		this.instancesUpToNow += nInstances; 
		
		return correct;
	}

	private String generaOggetti(Integer profMin2, Integer profMax2, Integer pesoMin2, Integer pesoMax2) {
		String oggetti = "";
		
		for(int i = 0; i < this.nOggetti; i++) {
			Integer prof = r.nextInt((profMax2 - profMin2) + 1) + profMin2;
			Integer peso = r.nextInt((pesoMax2 - pesoMin2) + 1) + pesoMin2;
			oggetti += prof+";"+peso+"\n";
		}
		return oggetti;
	}
	
	private Integer getCapacityFormItems(String items) {
		String rows[] = items.split("\n");
		Integer result = 0;
		for(String r : rows) {
			String data[] = r.split(";");
			if(data[1].compareTo("")!=0)
				result += Integer.parseInt(data[1]);
		}
		return result/2;
	}
	private String generaForfeits(Integer penalitaMin2, Integer penalitaMax2) {
		String result = "";
		this.forfeits = new ArrayList<>();
		while(this.forfeits.size() < this.nForfeits) {
			Integer o1 = r.nextInt(this.nOggetti)+1;
			Integer o2 = r.nextInt(this.nOggetti)+1;
			Integer penalita = r.nextInt((penalitaMax2 - penalitaMin2) + 1) + penalitaMin2;
			if(o1 != o2 && !this.forfeits.contains(new Forfeit(new Oggetto(o1, null, null, null), new Oggetto(o2, null, null, null), penalita))) {
				this.forfeits.add(new Forfeit(new Oggetto(o1, null, null, null), new Oggetto(o2, null, null, null), penalita));
			}
		}
		for(Forfeit f : this.forfeits) {
			result += f.toString();
		}
		
		return result;
	}
	
	public String launchSolver(Integer instance) {
			this.algorithm = new algorithm.Model();
			this.forfeits = new ArrayList<>();
			this.lista = new ArrayList<>();
			this.mappaOggetti = new HashMap<>();
			try {
				
				IloCplex cplex;
				cplex = new IloCplex();
	
				this.readInput("instances/instance"+(instance+1)+".txt");
				IloIntVar[] x = cplex.boolVarArray(this.lista.size());
				IloNumVar[] v = cplex.numVarArray(this.nForfeits, 0, 1);
				
				
				//Inizializzazione variabili:
				//Xi
				for(int i = 0; i < this.nOggetti; i++) {
					x[i] = cplex.boolVar(lista.get(i).getName());
				}
				
				for(int k = 0; k < this.nForfeits; k++) {
					v[k] = cplex.numVar(0, 1, "F"+this.forfeits.get(k).getO1().getName() + this.forfeits.get(k).getO2().getName());
				}
				
				//Per ora solo vincolo di capacità. 
				IloLinearNumExpr vincoloCapacita = cplex.linearNumExpr();
				
				for(int i = 0; i < lista.size(); i++) {
					vincoloCapacita.addTerm(this.lista.get(i).getWeight(), x[i]);
				}
				
				
				IloLinearNumExpr[] vincoloForfeit = new IloLinearNumExpr[this.forfeits.size()];
				
				for(int k = 0; k < this.nForfeits; k++) {
					vincoloForfeit[k] = cplex.linearNumExpr();
					vincoloForfeit[k].addTerm(1, x[this.forfeits.get(k).getO1().getId()-1]);
					vincoloForfeit[k].addTerm(1, x[this.forfeits.get(k).getO2().getId()-1]);
					vincoloForfeit[k].addTerm(-1, v[k]);
				}
	
				IloLinearNumExpr obj = cplex.linearNumExpr();
	
				//f. ob. sommatoria pi * Xi - Vk * penalità
				for(int i = 0; i < lista.size(); i++) {
					obj.addTerm(this.lista.get(i).getProfit(), x[i]);
				}
				for(int k = 0; k < this.forfeits.size(); k++) {
					obj.addTerm(-this.forfeits.get(k).getPenalty(), v[k]);
				}
				
				cplex.addMaximize(obj);
	
				cplex.addLe(vincoloCapacita, capacity);
				for(int k = 0; k < this.nForfeits; k++)
					cplex.addLe(vincoloForfeit[k], 1);
				Integer timeRunning = 0;
				if(this.nOggetti>=1000)
					timeRunning = 60;
				else timeRunning = 30;
				cplex.setParam(IloCplex.DoubleParam.TimeLimit, timeRunning);
				long start = System.currentTimeMillis();
				if(cplex.solve()) {
					long end = System.currentTimeMillis();
					Double time = (end-start)/Double.valueOf(1000);
					//String result = "";
		        	//result += "Obj = "+cplex.getObjValue() + "\n";
		        	
		        	/*
		        	 * SHOWS VALUES OF THE VARIABLES IN THE PROGRAM CONSOLE
		        	 * 
		        	 * for(int i = 0; i < this.lista.size(); i++) {
		        		result += "X"+(i+1)+" = " + cplex.getValue(x[i]) + "\n";
		        	}*/
		        	
		        	/*
		        	 * SHOWS OBJ AND BETS BOUND OF CPLEX 
		        	 * 
		        	 * result += "Current optimum = "+cplex.getObjValue()+"\n";
	        		result += "Best bound = "+cplex.getBestObjValue()+"\n";
		        	*/
		        	
		        	// MODEL EXPORT
		        	//cplex.exportModel("model.lp");
		        	String formattedTime = String.format("%.3f", time);
		        	writeResultsOnFile(instance+1, Double.toString(cplex.getObjValue()), Double.toString(cplex.getBestObjValue()), formattedTime);
		        	algorithm.execute("instance"+(instance+1)+".txt");
		        	//return "Instance "+(j+1)+" solved. Check results.txt!";
		        	cplex.clearModel();
		        	cplex.endModel();
		        	//cplex.clearCallbacks();
		        	//cplex.clearCuts();
		        	//cplex.clearLazyConstraints();
		        	//cplex.clearUserCuts();
		        	cplex.end();
				}
		        else {
		        	//return "Problema non risolto...\n" ;
		        }
				
			}
			catch(IloException e) {
				e.printStackTrace();
			}
			
			//return "Eccezione legata al solver... Riprovare\n";
		return "Instances solved. Check result.txt";
	}

	//TODO: Add time limit in params
	public void writeResultsOnFile(Integer instance, String objective, String bestBound, String time) {
		try {
		     /* File f = new File("results.txt");
		      if (f.createNewFile()) {
		        System.out.println("File created: " + f.getName());
		      } else {
		        System.out.println("File already exists.");
		      }*/
			FileWriter f = new FileWriter("results.txt", true);
			BufferedWriter bw = new BufferedWriter(f);
				PrintWriter out = new PrintWriter(bw);
		        out.println("Instance "+instance+")\nCPLEX results:\n");
		        final Object[][] table = new String[2][];
		        table[0] = new String [] {"Obj.", "Best bound", "Time[s]", "TL[s]"};
		        table[1] = new String [] {objective, bestBound, time, Integer.toString(30)};
		        for(final Object[] row : table) {
			        out.println(String.format("%-20s%-20s%-20s%-20s", row));
		        }
		        out.println("\nMetaheuristic results:\n");
		        final Object[][] header = new String[1][7];
		        header[0] = new String [] {"Type of start", "Initial ObjFunct.", "Optimum", "Time[s]", "Total it.", "Useful it.", "Increment %"};
		        for(final Object[] row : header) {
			        out.println(String.format("%-20s%-25s%-20s%-20s%-20s%-20s%-20s\n", row));
		        }
	            out.close();
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }

	}
	private void readInput(String nomeFile) {
				
		//Formato input (diviso in righe)

		//n : numero elementi ---> #Xi
		
		//W : capacità massima KP

		//f: numero di forfeits
				
		//pi;wi ---> Profitto e peso dell'elemento iesimo separato da ;
		//...
		
		//i;j;Vij ---> Elemento i ed elemento j separati da ; e a seguire la penalità, anch'essa separata da ;
		
		try {
			
			FileReader f = new FileReader (nomeFile);
			BufferedReader br = new BufferedReader (f);
			
			System.out.println("Lettura dati da " + nomeFile+" ...");
			
			String row;
			
			
			Integer nRow = 0;
			Integer nObj = 1;


			while((row = br.readLine()) != null) {
				String array[] = row.split(";");
				
				//Prima riga = nOggetti
				if(nRow == 0) {
					if(array.length != 1) {
						System.out.println("ATTENZIONE! Input numero oggetti nel formato errato!");
						throw new InputErratoException();

					}
					this.nOggetti = Integer.parseInt(array[0]);
				}
				
				//Seconda riga = max capacità
				else if(nRow == 1) {
					if(array.length != 1) {
						System.out.println("ATTENZIONE! Input capcità nel formato errato!");
						throw new InputErratoException();
					}
					this.capacity= Integer.parseInt(array[0]);
				}
				
				//Terza riga = nForfeits
				else if(nRow == 2) {
					/*if(array.length != 1) {
						System.out.println("ATTENZIONE! Input numero forfeits nel formato errato!");
						throw new InputErratoException();
					}*/
					this.nForfeits = Integer.parseInt(array[0]);
				}
				
				//Acquisizione profitti e pesi degli oggetti
				else if(nRow > 2 && nRow <= 2 + this.nOggetti) {
					if(array.length != 2) {
						System.out.println("ATTENZIONE! Input profitti e pesi oggetti nel formato errato!");
						throw new InputErratoException();
					}
					Oggetto nuovo = new Oggetto(nObj, "x"+nObj++, Double.parseDouble(array[0]), Double.parseDouble(array[1]));
					this.lista.add(nuovo);
					this.mappaOggetti.put(nuovo.getId(), nuovo);
				}
				
				//Se nessuno dei casi precedenti, sono nel "settore" dei forfeits
				else {
					if(array.length != 3 && array[0].compareTo("") != 0) {
						System.out.println("ATTENZIONE! Input forfeits nel formato errato!");
						throw new InputErratoException();
					}
					if(array[0].compareTo("") != 0) {
						Forfeit forfeit = new Forfeit(this.lista.get(Integer.parseInt(array[0])-1),
								this.lista.get(Integer.parseInt(array[1])-1),
								Double.parseDouble(array[2]));
						if(!this.forfeits.contains(forfeit))
							this.forfeits.add(forfeit);
						/*else {
							System.out.println("Forfeit tra " + forfeit.getO1().getName()+" e " + forfeit.getO2().getName() + "duplicato. Questo verrà ignorato.\n"
									+ "Verrà inoltre ridotto il nunmero di forfeit di 1");
							System.out.println(counterRimozioni++);
							this.nForfeits--;
						}*/
					}
				}
				
				nRow++;
			}
			br.close();
		}
		catch(InputErratoException iee) {
			System.err.println("Ricontrolla i dati in input");	
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public int getTotInstances() {
		return this.totInstance;
	}
}
