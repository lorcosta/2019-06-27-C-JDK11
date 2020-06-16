package it.polito.tdp.crimes.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	private EventsDao dao=new EventsDao();
	private Graph<String, DefaultWeightedEdge> grafo;
	private List<String> cammino;
	private Double bestPeso;
	
	public List<String> getCategoryID() {
		return dao.getCategoryID();
	}
	public List<LocalDate> getDays(){
		List<LocalDate> date=dao.getDays();
		Collections.sort(date);
		return date;
	}
	public void creaGrafo(String categoriaReato, LocalDate giorno) {
		this.grafo=new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		List<String> vertici=dao.getVertici(categoriaReato,giorno);
		if(vertici==null) {
			return;
		}
		Graphs.addAllVertices(this.grafo, vertici);
		//aggiungo tutti gli archi fra i vertici
		List<Arco> archi=dao.getArchi(categoriaReato,giorno);
		for(Arco a:archi) {
			if(this.grafo.vertexSet().contains(a.getV1()) && this.grafo.vertexSet().contains(a.getV1())
					&& this.grafo.getEdge(a.getV1(), a.getV2())==null && !a.getV1().equals(a.getV2())) {
				Graphs.addEdgeWithVertices(this.grafo, a.getV1(), a.getV2(), a.getPeso());
			}
		}
		
	}
	
	public Integer getNumVertici() {
		return this.grafo.vertexSet().size();
	}
	public Integer getNumArchi() {
		return this.grafo.edgeSet().size();
	}
	 public List<Arco> getMinoriPesoMediano(){
		 Double pesoMediano;
		 Double min=Double.MAX_VALUE;
		 Double max=Double.MIN_VALUE;
		 List<Arco> archiMinori=new ArrayList<>();
		 for(DefaultWeightedEdge e:this.grafo.edgeSet()) {
			 if(this.grafo.getEdgeWeight(e)<min) {
				 min=this.grafo.getEdgeWeight(e);
			 }
			 if(this.grafo.getEdgeWeight(e)>max) {
				 max=this.grafo.getEdgeWeight(e);
			 }
		 }
		 pesoMediano=(max+min)/2;
		 for(DefaultWeightedEdge e:this.grafo.edgeSet()) {
			 if(pesoMediano>this.grafo.getEdgeWeight(e)) {
				 archiMinori.add(new Arco(this.grafo.getEdgeSource(e),this.grafo.getEdgeTarget(e),this.grafo.getEdgeWeight(e)));
			 }
		 }
		 return archiMinori;
		 
	}
	public List<String> calcolaCammino(Arco arco) {
		//v1 è la partenza, v2 è la destinazione
		String partenza=arco.getV1();
		String arrivo=arco.getV2();
		bestPeso=arco.getPeso();
		List<String> parziale=new ArrayList<>();
		parziale.add(partenza);
		ricorsione(parziale, partenza, arrivo);
		return cammino;
	}
	private void ricorsione(List<String> parziale, String partenza, String arrivo) {
		if(parziale.get(parziale.size()-1).compareTo(arrivo)==0 && bestPeso<calcolaPeso(parziale)) {
			cammino=new ArrayList<>(parziale);
			bestPeso=calcolaPeso(parziale);
		}
		for(String vicino:Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(vicino)) {
				parziale.add(vicino);
				ricorsione(parziale,partenza,arrivo);
				parziale.remove(parziale.size()-1);
			}
		}
		
	}
	private Double calcolaPeso(List<String> parziale) {
		Double peso=0.0;
		for(int i=1;i<parziale.size();i++) {
			peso+=this.grafo.getEdgeWeight(this.grafo.getEdge(parziale.get(i-1),	parziale.get(i)));
		}
		return peso;
	}
	public Double getPesoCamminio() {
		return bestPeso;
	}
}
