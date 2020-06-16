/**
 * Sample Skeleton for 'Crimes.fxml' Controller Class
 */

package it.polito.tdp.crimes;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.crimes.model.Arco;
import it.polito.tdp.crimes.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class CrimesController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxCategoria"
    private ComboBox<String> boxCategoria; // Value injected by FXMLLoader

    @FXML // fx:id="boxGiorno"
    private ComboBox<LocalDate> boxGiorno; // Value injected by FXMLLoader

    @FXML // fx:id="btnAnalisi"
    private Button btnAnalisi; // Value injected by FXMLLoader

    @FXML // fx:id="boxArco"
    private ComboBox<Arco> boxArco; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	txtResult.appendText("Crea grafo...\n");
    	String categoriaReato=this.boxCategoria.getValue();
    	if(categoriaReato==null) {
    		this.txtResult.appendText("Attenzione! Nessuna categoria reato selezionata!\n");
    		return;
    	}
    	LocalDate giorno=this.boxGiorno.getValue();
    	if(giorno==null) {
    		this.txtResult.appendText("Attenzione, nessun giorno selezionato!\n");
    		return;
    	}
    	model.creaGrafo(categoriaReato,giorno);
    	Integer archi=model.getNumArchi();
    	Integer vertici=model.getNumVertici();
    	if(archi!=0 && vertici!=0) {
    		this.txtResult.appendText("GRAFO CREATO CON SUCCESSO!\n "+archi+" ARCHI e "+vertici+" VERTICI.\n");
    	}
    	this.txtResult.appendText("Gli archi con peso inferiore al peso mediano sono");
    	List<Arco> minori=model.getMinoriPesoMediano();
    	if(minori.size()==0) {
    		this.txtResult.appendText(" zero.");
    	}else {
    		this.txtResult.appendText(":\n");
    		for(Arco a:model.getMinoriPesoMediano()) {
        		this.txtResult.appendText(""+a.toString()+"\n");
        	}
    	}
    	this.boxArco.getItems().addAll(model.getMinoriPesoMediano());
    }

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	txtResult.clear();
    	txtResult.appendText("Calcola percorso...\n");
    	Arco arco=this.boxArco.getValue();
    	if(arco==null) {
    		this.txtResult.appendText("ATTENZIONE! Nessun arco selezionato");
    		return;
    	}
    	List<String> cammino=model.calcolaCammino(arco);
    	if(cammino==null) {
    		this.txtResult.appendText("Nessun cammino trovato");
    	}else {
    		this.txtResult.appendText("Trovato un cammino di lunghezza "+cammino.size()+" che passa per i vertici:\n");
    		for(String vertice:cammino) {
    			this.txtResult.appendText(vertice+"\n");
    		}
    		this.txtResult.appendText("Il peso totoale del cammino e' :"+model.getPesoCamminio());
    	}
    	
    }

    void loadData() {
    	this.boxCategoria.getItems().addAll(model.getCategoryID());
    	this.boxGiorno.getItems().addAll(model.getDays());
    }
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxCategoria != null : "fx:id=\"boxCategoria\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert boxGiorno != null : "fx:id=\"boxGiorno\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert btnAnalisi != null : "fx:id=\"btnAnalisi\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert boxArco != null : "fx:id=\"boxArco\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Crimes.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	loadData();
    }
}
