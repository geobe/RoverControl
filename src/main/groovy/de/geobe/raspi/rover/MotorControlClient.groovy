package de.geobe.raspi.rover

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


class MotorControlClient implements IMotorControl {

    String baseUri

    MotorControlClient(String restUri = "http://192.168.101.134:5050") {
        baseUri = restUri
    }

    @Override
    Map setMotion(Side side, Motion motion) {
        def path = "/$side/run/$motion".toString()
        HttpResponse<String> result = doPost(path)
        println result.body()
    }

    private HttpResponse<String> doPost(String path) {
        HttpClient client = HttpClient.newHttpClient()
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUri + path))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build()
        HttpResponse<String> result = client.send(request, HttpResponse.BodyHandlers.ofString())
        result
    }

    private HttpResponse<String> doGet(String path) {
        HttpClient client = HttpClient.newHttpClient()
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUri + path))
                .GET()
                .build()
        HttpResponse<String> result = client.send(request, HttpResponse.BodyHandlers.ofString())
        result
    }

    @Override
    Map getMotion(Side side) {
        return null
    }

    @Override
    Map setSpeed(Side side, float speed) {
        return 0
    }

    @Override
    Map getSpeed(Side side) {
        return 0
    }

    @Override
    Map setFrequency(Side side, int frequency) {
        [frequency: 0]
    }

    @Override
    Map getFrequency(Side side) {
        [frequency: 0]
    }

    @Override
    void shutdown(boolean exit = true) {

    }

    static void main(String[] args) {
        def client = new MotorControlClient()
        client.setMotion(Side.LEFT, Motion.FORWARD)
    }
}
