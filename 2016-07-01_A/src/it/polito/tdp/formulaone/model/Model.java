package it.polito.tdp.formulaone.model;

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	
	private FormulaOneDAO fonedao;
	private Graph<Driver, DefaultWeightedEdge> grafo;
	private DriverIdMap driversIdMap;
	
	public Model() {
		fonedao = new FormulaOneDAO();
		driversIdMap = new DriverIdMap();
	}

	public List<Season> getAllSeasons() {
		return fonedao.getAllSeasons();
	}

	public void creaGrafo(Season s) {
		
		grafo = new SimpleDirectedWeightedGraph<Driver, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		List<Driver> drivers = fonedao.getAllDriversBySeason(s,driversIdMap);
		
		Graphs.addAllVertices(grafo, drivers);
		
		//creo gli archi: o itero 2 volte sulla lista di piloti o faccio tutto con DB
		for(DriverSeasonResult dsr : fonedao.getDriverSeasonResults(s,driversIdMap)) {
			Graphs.addEdgeWithVertices(grafo, dsr.getD1(),dsr.getD2(),dsr.getCounter());
		}
		
		System.out.format("Grafo creato: %d archi, %d nodi\n", grafo.edgeSet().size(),grafo.vertexSet().size());
		
	}
	
	public Driver getBestDriver() {
		if(grafo == null) {
			new RuntimeException("Creare il grafo!");
		}
		
		//Inizializzazione
		Driver bestDriver = null;
		int best = Integer.MIN_VALUE; //cerco il max quindi la setto al minimo possibile
		
		for(Driver d : grafo.vertexSet()) {
			int sum = 0 ;
			//Itero su archi uscenti e aggiungo i pesi alla sum
			for(DefaultWeightedEdge e : grafo.outgoingEdgesOf(d)) {
				//outgoingEdgesOf mi da gli archi uscenti
				sum += grafo.getEdgeWeight(e);
			}
			//Itero su archi entranti e sottraggo i pesi alla sum
			for(DefaultWeightedEdge e : grafo.incomingEdgesOf(d)) {
				//incomingEdgesOf mi da gli archi uscenti
				sum -= grafo.getEdgeWeight(e);
			}
			
			if(sum > best || bestDriver == null) {
				best = sum ;
				bestDriver = d ;
			}
		}
		
		if(bestDriver == null)
			new RuntimeException("Best Driver not found.\n");
		
		return bestDriver ;
	}


}
