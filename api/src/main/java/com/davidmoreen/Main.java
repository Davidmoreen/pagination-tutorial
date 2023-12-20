package com.davidmoreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.text.html.Option;

import com.davidmoreen.Model.ApiRequest;
import com.davidmoreen.Model.ApiResponse;
import com.davidmoreen.Model.Contact;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Main {

  public static void main(String[] args) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
    server.createContext("/api", new ApiHandler());
    server.setExecutor(null);
    server.start();
  }

  private

  static class ApiHandler implements HttpHandler {
    private static String fileName = "/MOCK_DATA.csv";
    private static int defaultLimit = 25;
    private static String defaultOffset = "0";

    @Override
    public void handle(HttpExchange t) throws IOException {
      ApiRequest request = getApiRequest(t);
      ApiResponse apiResponse = getContactsResponse(defaultLimit, request.after);

      ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
      String jsonResponse = objectWriter.writeValueAsString(apiResponse);

      t.sendResponseHeaders(200, jsonResponse.length());
      OutputStream os = t.getResponseBody();
      os.write(jsonResponse.getBytes());
      os.close();
    }

    private ApiRequest getApiRequest(HttpExchange t) {
      Map<String, Optional<String>> queryParams = getQueryParams(t);
      String after = defaultOffset;
      if (queryParams.containsKey("after")) {
        after = queryParams.get("after").orElse(defaultOffset);
      }
      return new ApiRequest(Integer.parseInt(after));
    }

    private Map<String, Optional<String>> getQueryParams(HttpExchange t) {
      String queryString = t.getRequestURI().getQuery();
      Map<String, Optional<String>> params = new HashMap<>();

      if (queryString == null || queryString.isEmpty()) {
        return params;
      }

      for (String param : queryString.split("&")) {
        String[] fragment = param.split("=");
        if (fragment.length > 1) {
          params.put(fragment[0], Optional.of(fragment[1]));
        } else {
          params.put(fragment[0], Optional.empty());
        }
      }

      return params;
    }

    private ApiResponse getContactsResponse(int limit, int offset) throws IOException {
      List<Contact> contacts = new ArrayList<>();
      Boolean hasMore = false;
      int count = 0;

      InputStreamReader streamReader = new InputStreamReader(
          getClass().getResourceAsStream(fileName)
      );
      try (BufferedReader br = new BufferedReader(streamReader)) {
        for (int i = 0; i < offset; i++) {
          br.readLine();
        }

        String line;
        while (count < limit && (line = br.readLine()) != null) {
          String[] lineData = line.split(",");
          contacts.add(new Contact(
              Integer.parseInt(lineData[0]),
              lineData[1],
              lineData[2],
              lineData[3]
          ));
          count++;
        }

        if (br.readLine() != null) {
          hasMore = true;
        }
      } catch (Exception e) {
        System.out.println("Error: " + e.getLocalizedMessage());
      }

      return new ApiResponse(
          hasMore ? offset+count : null,
          contacts
      );
    }
  }
}