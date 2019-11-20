package Server;

import DAO.LiftRide;
import DAO.LiftRideDao;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.google.gson.Gson;

@WebServlet(name = "SkierServlet", value = "/skiers")
public class SkierServlet extends HttpServlet {
  private static final LiftRideDao liftRideDao = new LiftRideDao();

  protected void doPost(HttpServletRequest req,
      HttpServletResponse res)
      throws javax.servlet.ServletException, IOException {
    PrintWriter out = res.getWriter();
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");
    String urlPath = req.getPathInfo();

    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("missing paramterers");
      out.write(jsonString);
      return;
    }

    // get body information
    Integer time = null;
    Integer liftID = null;
    String line = req.getReader().readLine();
    StringBuilder sb = new StringBuilder();
    while (line != null) {
      sb.append(line);
      line = req.getReader().readLine();
    }
    io.swagger.client.model.LiftRide body = new Gson().fromJson(sb.toString(),
        io.swagger.client.model.LiftRide.class);
    req.getReader().close();
    time = body.getTime();
    liftID = body.getLiftID();

    String[] urlParts = urlPath.split("/");

    if (!isUrlValidFullInformation(urlParts) && !isBodyValid(time, liftID)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("invalid url");
      out.write(jsonString);
    } else {
      LiftRide liftRide = buildLiftRide(urlParts, time, liftID);
      boolean result = liftRideDao.createLiftRide(liftRide);
      if (result) {
        res.setStatus(HttpServletResponse.SC_OK);
        // do any sophisticated processing with urlParts which contains all the url param
        String jsonString = new Gson().toJson(liftRide.toString());
        out.write(jsonString);
      } else {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        String jsonString = new Gson().toJson("Write in DB Failed");
        out.write(jsonString);
      }
    }
  }

  protected void doGet(HttpServletRequest req,
      HttpServletResponse res)
      throws javax.servlet.ServletException, IOException {
    PrintWriter out = res.getWriter();
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");
    String urlPath = req.getPathInfo();
//    System.out.println(urlPath);

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("missing paramterers");
      out.write(jsonString);
      return;
    }

    String[] urlParts = urlPath.split("/");

    if (isUrlValidFullInformation(urlParts)) {
      int resortId = Integer.valueOf(urlParts[1]);
      int seasonId = Integer.valueOf(urlParts[3]);
      int dayId = Integer.valueOf(urlParts[5]);
      int skierId = Integer.valueOf(urlParts[7]);
      String result = liftRideDao.getVerticalForSpecificDay(skierId, seasonId, resortId, dayId);

      res.setStatus(HttpServletResponse.SC_OK);
      // do any sophisticated processing with urlParts which contains all the url param
      out.write(result);
    } else if (isUrlValidWithVertical(urlParts)) {
      int skierId = Integer.valueOf(urlParts[1]);
      String result = liftRideDao.getVerticalForSpecificResort(skierId);
      if (result == null) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        String jsonString = new Gson().toJson("No Data For Specific Resort");
        out.write(jsonString);
      } else {
        res.setStatus(HttpServletResponse.SC_OK);
        String jsonString = new Gson().toJson("ok");
        out.write(result);
      }
    }
      else {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("invalid url");
      out.write(jsonString);
    }
  }

  private boolean isUrlValidFullInformation(String[] urlPath) {
    // urlPath  = "/1/seasons/2019/day/1/skier/123"
    // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
    if (urlPath.length == 8) {
       return NumericHelper.isInteger(urlPath[1]) && urlPath[2].equals("seasons")
        && NumericHelper.isInteger(urlPath[3]) && urlPath[4].equals("days")
        && NumericHelper.isInteger(urlPath[5]) && urlPath[6].equals("skiers")
        && NumericHelper.isInteger(urlPath[7]);
    }
    return false;
  }

  private boolean isUrlValidWithVertical(String[] urlPath) {
    // urlPath  = "/1/vertical"
    // urlParts = [, 1, vertical]
    if (urlPath.length == 3) {
      return NumericHelper.isInteger(urlPath[1]) && urlPath[2].equals("vertical");
    }
    return false;
  }

  private boolean isBodyValid(Integer time, Integer liftID) {
    if (time != null && liftID != null) {
      return true;
    }
    return false;
  }

  private LiftRide buildLiftRide(String[] urlPath, Integer time, Integer liftID) {
    // urlPath  = "/1/seasons/2019/day/1/skier/123"
    // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
    LiftRide liftRide = new LiftRide(
        Integer.valueOf(urlPath[1]),
        Integer.valueOf(urlPath[3]),
        Integer.valueOf(urlPath[5]),
        Integer.valueOf(urlPath[7]),
        time,
        liftID);
    return liftRide;
  }

  public static String getInfo() {
    return "Version: " + System.getProperty("java.version")
        + " OS: " + System.getProperty("os.name")
        + " User: " + System.getProperty("user.name");
  }
}
