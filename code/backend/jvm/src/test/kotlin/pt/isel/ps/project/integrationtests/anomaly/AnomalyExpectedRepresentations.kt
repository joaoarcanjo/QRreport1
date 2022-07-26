package pt.isel.ps.project.integrationtests.anomaly

object AnomalyExpectedRepresentations {
    const val GET_ANOMALIES = "{\"class\":[\"anomaly\",\"collection\"],\"properties\":{\"pageIndex\":1," +
            "\"pageMaxSize\":10,\"collectionSize\":4},\"entities\":[{\"class\":[\"anomaly\"],\"rel\":[\"item\"]," +
            "\"properties\":{\"id\":1,\"anomaly\":\"The flush doesn't work\"},\"actions\":[{\"name\":\"" +
            "update-anomaly\",\"title\":\"Update anomaly\",\"method\":\"PUT\",\"href\":\"/v1/devices/1/anomalies/1" +
            "\",\"type\":\"application/json\",\"properties\":[{\"name\":\"anomaly\",\"type\":\"string\"}]},{\"name\":" +
            "\"delete-anomaly\",\"title\":\"Delete anomaly\",\"method\":\"DELETE\",\"href\":\"/v1/devices/1/anomalies" +
            "/1\"}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/devices/1/anomalies/1\"}]},{\"class\":[\"anomaly\"]" +
            ",\"rel\":[\"item\"],\"properties\":{\"id\":2,\"anomaly\":\"The water is overflowing\"},\"actions\":[{\"" +
            "name\":\"update-anomaly\",\"title\":\"Update anomaly\",\"method\":\"PUT\",\"href\":\"/v1/devices/1/anoma" +
            "lies/2\",\"type\":\"application/json\",\"properties\":[{\"name\":\"anomaly\",\"type\":\"string\"}]},{\"" +
            "name\":\"delete-anomaly\",\"title\":\"Delete anomaly\",\"method\":\"DELETE\",\"href\":\"/v1/devices/1/" +
            "anomalies/2\"}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/devices/1/anomalies/2\"}]},{\"class\":" +
            "[\"anomaly\"],\"rel\":[\"item\"],\"properties\":{\"id\":3,\"anomaly\":\"The toilet is clogged\"},\"" +
            "actions\":[{\"name\":\"update-anomaly\",\"title\":\"Update anomaly\",\"method\":\"PUT\",\"href\":\"" +
            "/v1/devices/1/anomalies/3\",\"type\":\"application/json\",\"properties\":[{\"name\":\"anomaly\",\"type" +
            "\":\"string\"}]},{\"name\":\"delete-anomaly\",\"title\":\"Delete anomaly\",\"method\":\"DELETE\",\"href" +
            "\":\"/v1/devices/1/anomalies/3\"}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/devices/1/anomalies/3" +
            "\"}]},{\"class\":[\"anomaly\"],\"rel\":[\"item\"],\"properties\":{\"id\":4,\"anomaly\":\"The water is " +
            "always running\"},\"actions\":[{\"name\":\"update-anomaly\",\"title\":\"Update anomaly\",\"method\":\"" +
            "PUT\",\"href\":\"/v1/devices/1/anomalies/4\",\"type\":\"application/json\",\"properties\":[{\"name\":\"" +
            "anomaly\",\"type\":\"string\"}]},{\"name\":\"delete-anomaly\",\"title\":\"Delete anomaly\",\"method\":\"" +
            "DELETE\",\"href\":\"/v1/devices/1/anomalies/4\"}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/devices" +
            "/1/anomalies/4\"}]}],\"actions\":[{\"name\":\"create-anomaly\",\"title\":\"Create anomaly\",\"method\":" +
            "\"POST\",\"href\":\"/v1/devices/1/anomalies\",\"type\":\"application/json\",\"properties\":[{\"name\":" +
            "\"anomaly\",\"type\":\"string\"}]}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/devices/1/anomalies?" +
            "page=1\"},{\"rel\":[\"pagination\"],\"href\":\"/v1/devices/{deviceId}/anomalies{?page}\",\"templated\"" +
            ":true}]}"

    const val CREATED_ANOMALY = "{\"class\":[\"anomaly\"],\"properties\":{\"id\":5,\"anomaly\":\"Anomaly test\"}," +
            "\"links\":[{\"rel\":[\"device-anomalies\"],\"href\":\"/v1/devices/1/anomalies\"}]}"

    const val UPDATED_ANOMALY = "{\"class\":[\"anomaly\"],\"properties\":{\"id\":1,\"anomaly\":\"Anomaly test " +
            "updated\"},\"links\":[{\"rel\":[\"device-anomalies\"],\"href\":\"/v1/devices/1/anomalies\"}]}"

    const val DELETED_ANOMALY = "{\"class\":[\"anomaly\"],\"properties\":{\"id\":1,\"anomaly\":\"The flush doesn't" +
            " work\"},\"links\":[{\"rel\":[\"device-anomalies\"],\"href\":\"/v1/devices/1/anomalies\"}]}"
}