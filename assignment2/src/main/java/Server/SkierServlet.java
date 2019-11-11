package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.google.gson.Gson;

@WebServlet(name = "SkierServlet")
public class SkierServlet extends HttpServlet {

  protected void doPost(HttpServletRequest req,
      HttpServletResponse res)
      throws javax.servlet.ServletException, IOException {
    PrintWriter out = res.getWriter();
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");
    String urlPath = req.getPathInfo();
    Map<String, String[]> body = req.getParameterMap();
//    System.out.println(urlPath);

    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("missing paramterers");
      out.write(jsonString);
      return;
    }

    String[] urlParts = urlPath.split("/");

    if (!isUrlValid(urlParts) && isBodyValid(body)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("invalid url");
      out.write(jsonString);
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      // do any sophisticated processing with urlParts which contains all the url param
      String jsonString = new Gson().toJson("1");
      out.write(jsonString);
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
//      out.print(jsonString);
      return;
    }

    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)

    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("invalid url");
      out.write(jsonString);
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      // do any sophisticated processing with urlParts which contains all the url param
      String jsonString = new Gson().toJson("1");
      out.write(jsonString);
    }
  }

  private boolean isUrlValid(String[] urlPath) {
    // urlPath  = "/1/seasons/2019/day/1/skier/123"
    // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
    if (urlPath.length == 8) {
       return NumericHelper.isInteger(urlPath[1]) && urlPath[2].equals("seasons")
        && NumericHelper.isInteger(urlPath[3]) && urlPath[4].equals("days")
        && NumericHelper.isInteger(urlPath[5]) && urlPath[6].equals("skiers")
        && NumericHelper.isInteger(urlPath[7]);
    }
    else if (urlPath.length == 3) {
      return NumericHelper.isInteger(urlPath[1])
          && urlPath[2].equals("vertical");
    }
    return false;
  }

  private boolean isBodyValid(Map<String, String[]> body) {
    if (body.containsKey("time") && body.containsKey("liftID") && body.size() == 2) {
      return true;
    }
    return false;
  }
}
