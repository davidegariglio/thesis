package GeneratoreInputKPWithForfeits.UI;

import java.net.URL;
import java.util.ResourceBundle;

import GeneratoreInputKPWithForfeits.UI.Model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

	 @FXML
	 private ResourceBundle resources;

	 @FXML
	 private URL location;

	 @FXML
	    private TextField txtNumberInstancs;

	 @FXML
	 private TextField inputN;

	 @FXML
	 private TextField moltiplicatoreForfeit;

	 @FXML
	 private TextField moltiplicatoreCapacita;

	 @FXML
	 private TextField LBPesoOggetti;

	 @FXML
	 private TextField UBPesoOggetti;

	 @FXML
	 private TextField LBProfittiOggetti;

	 @FXML
	 private TextField UBProfittiOggetti;
	 
	 @FXML
	 private TextField LBPenalitaForfeit;

	 @FXML
	 private TextField UBPenalitaForfeit;

	 @FXML
	 private TextArea txtResul;

	 @FXML
	 private Button btnGenera;

	 @FXML
	 private Button btnLaunchSolver;

	 //private Boolean generato = false;
    @FXML
    void doGeneraInput(ActionEvent event) {
    	Integer nInstances;
    	Integer n;
    	Integer moltForfeit;
    	Integer moltCapacita;
    	Integer LBPesoOgg, UBPesoOgg;
    	Integer LBProfOgg, UBProfOgg;
    	Integer LBPenalitaForfeit, UBPenalitaForfeit;
    	try {
    		nInstances = Integer.parseInt(this.txtNumberInstancs.getText());
    		n = Integer.parseInt(this.inputN.getText());
    		moltForfeit = Integer.parseInt(this.moltiplicatoreForfeit.getText());
    		//Cap = somma pesi / 2
    		moltCapacita = Integer.parseInt(this.moltiplicatoreCapacita.getText());
    		LBPesoOgg= Integer.parseInt(this.LBPesoOggetti.getText());
    		UBPesoOgg= Integer.parseInt(this.UBPesoOggetti.getText());
        	LBProfOgg = Integer.parseInt(this.LBProfittiOggetti.getText());
        	UBProfOgg = Integer.parseInt(this.UBProfittiOggetti.getText());
        	LBPenalitaForfeit = Integer.parseInt(this.LBPenalitaForfeit.getText());
        	UBPenalitaForfeit = Integer.parseInt(this.UBPenalitaForfeit.getText());
    	}
    	catch(NumberFormatException e) {
    		this.txtResul.appendText("ATTENZIONE! Sembra che uno o più numero inseriti non siano nel formato corretto!\n"
    				+ "Si prega di inserire solo numeri interi maggiori di zero.");
    		return;
    	}
    	if(this.model.generaInput(nInstances, n,moltCapacita, moltForfeit, LBPesoOgg, UBPesoOgg,
    			LBProfOgg, UBProfOgg, LBPenalitaForfeit, UBPenalitaForfeit)) {
    		//this.generato = true;
        	txtResul.appendText("Il file .txt è stato creato con successo. Verificare nella cartella.");
    	}
    }
    

    @FXML
    void doLaunchSolver(ActionEvent event) {
    	//if(generato) {
    	Integer nInstances;
    	try {
    		nInstances = Integer.parseInt(this.txtNumberInstancs.getText());
    	}catch(NumberFormatException nfe) {
    		this.txtResul.appendText("Inserire numero di istanze da eseguire");
    		return;
    	}
    	for(int i = 0; i < nInstances; i++) {
    		this.txtResul.clear();	
    		this.txtResul.appendText(this.model.launchSolver(i));
    	}
    		/*}
    	else {
    		this.txtResul.appendText("Occorre prima compilare tutti i campi e generare un file txt di input.");
    	}*/
    }

    @FXML
    void initialize() {
        assert txtNumberInstancs != null : "fx:id=\"txtNumberInstancs\" was not injected: check your FXML file 'Scene.fxml'.";
    	assert inputN != null : "fx:id=\"inputN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert moltiplicatoreForfeit != null : "fx:id=\"moltiplicatoreForfeit\" was not injected: check your FXML file 'Scene.fxml'.";
        assert moltiplicatoreCapacita != null : "fx:id=\"moltiplicatoreCapacita\" was not injected: check your FXML file 'Scene.fxml'.";
        assert LBPesoOggetti != null : "fx:id=\"LBPesoOggetti\" was not injected: check your FXML file 'Scene.fxml'.";
        assert UBPesoOggetti != null : "fx:id=\"UBPesoOggetti\" was not injected: check your FXML file 'Scene.fxml'.";
        assert LBProfittiOggetti != null : "fx:id=\"LBProfittiOggetti\" was not injected: check your FXML file 'Scene.fxml'.";
        assert UBProfittiOggetti != null : "fx:id=\"UBProfittiOggetti\" was not injected: check your FXML file 'Scene.fxml'.";
        assert LBPenalitaForfeit != null : "fx:id=\"LBPenalitaForfeit\" was not injected: check your FXML file 'Scene.fxml'.";
        assert UBPenalitaForfeit != null : "fx:id=\"UBPenalitaForfeit\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResul != null : "fx:id=\"txtResul\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnGenera != null : "fx:id=\"btnGenera\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnLaunchSolver != null : "fx:id=\"btnLaunchSolver\" was not injected: check your FXML file 'Scene.fxml'.";

    }
		
	public void setModel(Model model) {
		this.model = model;
	}
}
