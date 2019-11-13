package Server;

import DAO.LiftRideDao;
import DAO.Statistics;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "StatisticsServlet")
public class StatisticsServlet extends HttpServlet {
  private static final LiftRideDao liftRideDao = new LiftRideDao();

  protected void doGet(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();
    String urlPath = request.getPathInfo();
    if (urlPath == null || urlPath.isEmpty()) {
      List<Statistics> statistics = new ArrayList<Statistics>();
      try {
        statistics = liftRideDao.getStatistics();
      } catch (Exception e) {
        e.printStackTrace();
      }

      String jsonString = new Gson().toJson(statistics);
      response.setStatus(HttpServletResponse.SC_OK);
      out.print(jsonString);
      out.flush();
    }
  }
}
