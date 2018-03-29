package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Get parameter from HTTP request
	    double lat = Double.parseDouble(request.getParameter("lat"));
	    double lon = Double.parseDouble(request.getParameter("lon"));
	    String term = request.getParameter("term"); // term can be null
	    String userId = request.getParameter("user_id");

        // call TicketMasterAPI.search to get event data
	    //TicketMasterAPI tmAPI = new TicketMasterAPI();
	    
	    // Connect to external API(has been moved to implementation class of DBConnectionFactory)
	    //ExternalAPI api = ExternalAPIFactory.getExternalAPI();
	    //List<Item> items = api.search(lat, lon, term);
	    
	    // Invoke searchItems() from default DBConnection(MySQLConnection)
	    DBConnection conn = DBConnectionFactory.getDBConnection();
		List<Item> items = conn.searchItems(userId, lat, lon, term);

	    // Convert Item list back to JSONArray for client
	    List<JSONObject> list = new ArrayList<>();
	    
	    Set<String> favorite = conn.getFavoriteItemIds(userId);
	    
	    try {
	      for (Item item : items) {
	        JSONObject obj = item.toJSONObject();
	        if (favorite != null) {
				obj.put("favorite", favorite.contains(item.getItemId())); //for each result event user got, also send if it has already been favorited by user before.
			}
	        list.add(obj);
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    JSONArray array = new JSONArray(list);
	    RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
