package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.crimes.model.Arco;
import it.polito.tdp.crimes.model.Event;


public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {	
			e.printStackTrace();
			return null ;
		}
	}

	public List<String> getCategoryID(){
		String sql="SELECT DISTINCT offense_category_id " + 
				"FROM events";
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<String> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(res.getString("offense_category_id"));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {	
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<LocalDate> getDays(){
		String sql="SELECT DISTINCT DATE_FORMAT(reported_date,'%Y-%d-%c') AS data " + 
				"FROM events " + 
				"ORDER BY data";
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<LocalDate> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(res.getDate("data").toLocalDate());
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {	
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<String> getVertici(String categoriaReato, LocalDate giorno){
		String sql="SELECT DISTINCT offense_type_id " + 
				"FROM events " + 
				"WHERE DATE(reported_date)=? AND offense_category_id=?";
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setDate(1, Date.valueOf(giorno));
			st.setString(2, categoriaReato);
			List<String> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(res.getString("offense_type_id"));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {	
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Arco> getArchi(String offense_category_id,LocalDate giorno){
		String sql="SELECT e1.offense_type_id v1, e2.offense_type_id v2, COUNT(DISTINCT e1.precinct_id) as tot " + 
				"FROM events e1, events e2 " + 
				"WHERE e1.incident_id<>e2.incident_id AND e1.offense_category_id=e2.offense_category_id " + 
				"AND DATE(e1.reported_date)=DATE(e2.reported_date) AND e1.offense_type_id>e2.offense_type_id " + 
				"AND e1.precinct_id=e2.precinct_id AND e1.offense_category_id=? AND DATE(e1.reported_date)=? " + 
				"GROUP BY e1.offense_type_id,e2.offense_type_id";
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setString(1, offense_category_id);
			st.setDate(2, Date.valueOf(giorno));
			List<Arco> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Arco(res.getString("v1"),res.getString("v2"),res.getDouble("tot")));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {	
			e.printStackTrace();
			return null ;
		}
	}
}