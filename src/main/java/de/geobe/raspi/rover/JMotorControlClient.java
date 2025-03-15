package de.geobe.raspi.rover;

import com.cedarsoftware.io.JsonIo;
import com.cedarsoftware.io.ReadOptions;
import com.cedarsoftware.io.ReadOptionsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;

public class JMotorControlClient implements IMotorControl {

    String baseUri;
    ReadOptions readOptions;

    public JMotorControlClient(String restUri) {
        baseUri = restUri;
        readOptions = new ReadOptionsBuilder().failOnUnknownType(false).build();
        if (doGet("/") == null) {
//            System.out.println("Kein Server bei " + restUri);
            throw new RuntimeException("Kein Server bei " + restUri);
        }
    }

    public JMotorControlClient() {
        this("http://192.168.101.134:5050");
    }

    @Override
    public Map setMotion(Side side, Motion motion) {
        String path = "/" + side.toString() + "/run/" + motion.toString();
        Map result = doPost(path);
        return result;
    }

    @Override
    public Map getMotion(Side side) {
        String path = "/" + side.toString() + "/run";
        Map result = doGet(path);
        return result;
    }

    @Override
    public Map setSpeed(Side side, float speed) {
        String path = "/" + side.toString() + "/speed/" + Float.valueOf(speed).toString();
        Map result = doPost(path);
        return result;
    }

    @Override
    public Map getSpeed(Side side) {
        String path = "/" + side.toString() + "/speed";
        Map result = doGet(path);
        return result;
    }

    @Override
    public Map setFrequency(Side side, int frequency) {
        String path = "/" + side.toString() + "/frequency/" + Integer.valueOf(frequency).toString();
        Map result = doPost(path);
        return result;
    }

    @Override
    public Map getFrequency(Side side) {
        String path = "/" + side.toString() + "/frequency";
        Map result = doGet(path);
        return result;
    }

    @Override
    public void shutdown(boolean exit) {
        String path = "/exit";
        if (exit) {
            path += "/true";
        }
        Map result = doPost(path);
    }

    private Map doPost(String path) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUri + path))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Map result = JsonIo.toJava(response.body(), readOptions).asClass(LinkedHashMap.class);
                return result;
            } else {
                return Map.of("error", "Status code " + response.statusCode());
            }
        } catch (Exception e) {
            if (path.contains("exit") || path.contains("shut")) {
                return null;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private Map doGet(String path) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUri + path))
                .GET()
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Map result = JsonIo.toJava(response.body(), readOptions).asClass(LinkedHashMap.class);
                return result;
            } else {
                return Map.of("error", "Status code " + response.statusCode());
            }
        } catch (Exception e) {
            if (path.equals("/") || path.contains("exit")) {
                return null;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        JMotorControlClient client = new JMotorControlClient();
        client.setMotion(Side.LEFT, Motion.FORWARD);
        client.setSpeed(Side.LEFT, 75.0f);
        client.setMotion(Side.RIGHT, Motion.BACKWARD);
        client.setSpeed(Side.RIGHT, 35.0f);
        client.getFrequency(Side.LEFT);
        client.getFrequency(Side.RIGHT);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        client.shutdown(true);
    }
}
