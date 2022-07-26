package pt.isel.ps.project.integrationtests.devices

object DeviceExpectedRepresentations {

    const val GET_DEVICES = "{\"class\":[\"device\",\"collection\"],\"properties\":{\"pageIndex\":1,\"pageMaxSize\"" +
            ":10,\"collectionSize\":3},\"entities\":[{\"class\":[\"device\"],\"rel\":[\"item\"],\"properties\":{\"id\"" +
            ":1,\"name\":\"Toilet1\",\"category\":\"water\",\"state\":\"active\",\"timestamp\":1658827256321},\"links" +
            "\":[{\"rel\":[\"self\"],\"href\":\"/v1/devices/1\"}]},{\"class\":[\"device\"],\"rel\":[\"item\"]," +
            "\"properties\":{\"id\":3,\"name\":\"Faucet\",\"category\":\"water\",\"state\":\"inactive\",\"timestamp\"" +
            ":1658827256321},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/devices/3\"}]},{\"class\":[\"device\"]," +
            "\"rel\":[\"item\"],\"properties\":{\"id\":2,\"name\":\"Lights\",\"category\":\"electricity\",\"state\":" +
            "\"active\",\"timestamp\":1658827256321},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/devices/2\"}]}]," +
            "\"actions\":[{\"name\":\"create-device\",\"title\":\"Create device\",\"method\":\"POST\",\"href\":\"/v1/" +
            "devices\",\"type\":\"application/json\",\"properties\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\"" +
            ":\"category\",\"type\":\"number\",\"possibleValues\":{\"href\":\"/v1/categories\"}}]}],\"links\":[{\"rel" +
            "\":[\"self\"],\"href\":\"/v1/devices?page=1\"},{\"rel\":[\"pagination\"],\"href\":\"/v1/devices{?page}\"" +
            ",\"templated\":true}]}"

    const val CREATED_DEVICE = "{\"class\":[\"device\"],\"properties\":{\"id\":4,\"name\":\"New device name\"," +
            "\"category\":\"water\",\"state\":\"active\",\"timestamp\":1658827630455},\"links\":[{\"rel\":[\"self" +
            "\"],\"href\":\"/v1/devices/4\"}]}"

    const val GET_DEVICE = "{\"class\":[\"ticket\"],\"properties\":{\"id\":1,\"name\":\"Toilet1\",\"category\":\"" +
            "water\",\"state\":\"active\",\"timestamp\":1658827754453},\"entities\":[{\"class\":[\"anomaly\"," +
            "\"collection\"],\"rel\":[\"device-anomalies\"],\"properties\":{\"pageIndex\":1,\"pageMaxSize\":10," +
            "\"collectionSize\":4},\"entities\":[{\"class\":[\"anomaly\"],\"rel\":[\"item\"],\"properties\":{\"id\":1" +
            ",\"anomaly\":\"The flush doesn't work\"},\"actions\":[{\"name\":\"update-anomaly\",\"title\":" +
            "\"Update anomaly\",\"method\":\"PUT\",\"href\":\"/v1/devices/1/anomalies/1\",\"type\":\"application/json\"" +
            ",\"properties\":[{\"name\":\"anomaly\",\"type\":\"string\"}]},{\"name\":\"delete-anomaly\"," +
            "\"title\":\"Delete anomaly\",\"method\":\"DELETE\",\"href\":\"/v1/devices/1/anomalies/1\"}]," +
            "\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/devices/1/anomalies/1\"}]},{\"class\":[\"anomaly\"]," +
            "\"rel\":[\"item\"],\"properties\":{\"id\":2,\"anomaly\":\"The water is overflowing\"},\"actions\":" +
            "[{\"name\":\"update-anomaly\",\"title\":\"Update anomaly\",\"method\":\"PUT\",\"href\":" +
            "\"/v1/devices/1/anomalies/2\",\"type\":\"application/json\",\"properties\":[{\"name\":\"anomaly\"," +
            "\"type\":\"string\"}]},{\"name\":\"delete-anomaly\",\"title\":\"Delete anomaly\",\"method\":\"DELETE\"," +
            "\"href\":\"/v1/devices/1/anomalies/2\"}],\"links\":[{\"rel\":[\"self\"],\"href\":" +
            "\"/v1/devices/1/anomalies/2\"}]},{\"class\":[\"anomaly\"],\"rel\":[\"item\"],\"properties\":{\"id\":3," +
            "\"anomaly\":\"The toilet is clogged\"},\"actions\":[{\"name\":\"update-anomaly\",\"title\":" +
            "\"Update anomaly\",\"method\":\"PUT\",\"href\":\"/v1/devices/1/anomalies/3\",\"type\":\"application/json\"," +
            "\"properties\":[{\"name\":\"anomaly\",\"type\":\"string\"}]},{\"name\":\"delete-anomaly\",\"title\":" +
            "\"Delete anomaly\",\"method\":\"DELETE\",\"href\":\"/v1/devices/1/anomalies/3\"}],\"links\":[{\"rel\":" +
            "[\"self\"],\"href\":\"/v1/devices/1/anomalies/3\"}]},{\"class\":[\"anomaly\"],\"rel\":[\"item\"]," +
            "\"properties\":{\"id\":4,\"anomaly\":\"The water is always running\"},\"actions\":[{\"name\":" +
            "\"update-anomaly\",\"title\":\"Update anomaly\",\"method\":\"PUT\",\"href\":\"/v1/devices/1/anomalies/4\"," +
            "\"type\":\"application/json\",\"properties\":[{\"name\":\"anomaly\",\"type\":\"string\"}]},{\"name\":" +
            "\"delete-anomaly\",\"title\":\"Delete anomaly\",\"method\":\"DELETE\",\"href\":" +
            "\"/v1/devices/1/anomalies/4\"}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/devices/1/anomalies/4\"}]}]," +
            "\"actions\":[{\"name\":\"create-anomaly\",\"title\":\"Create anomaly\",\"method\":\"POST\",\"href\":" +
            "\"/v1/devices/1/anomalies\",\"type\":\"application/json\",\"properties\":[{\"name\":\"anomaly\",\"type\":" +
            "\"string\"}]}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/devices/1/anomalies?page=1\"},{\"rel\":" +
            "[\"pagination\"],\"href\":\"/v1/devices/{deviceId}/anomalies{?page}\",\"templated\":true}]}],\"actions\":" +
            "[{\"name\":\"deactivate-device\",\"title\":\"Deactivate device\",\"method\":\"POST\",\"href\":" +
            "\"/v1/devices/1/deactivate\"},{\"name\":\"update-device\",\"title\":\"Update device\",\"method\":\"PUT\"," +
            "\"href\":\"/v1/devices/1\",\"type\":\"application/json\",\"properties\":[{\"name\":\"name\",\"type\":" +
            "\"string\"}]},{\"name\":\"change-device-category\",\"title\":\"Change device category\",\"method\":\"PUT\"," +
            "\"href\":\"/v1/devices/1/category\",\"type\":\"application/json\",\"properties\":[{\"name\":\"category\"," +
            "\"type\":\"number\",\"possibleValues\":{\"href\":\"/v1/categories\"}}]}],\"links\":[{\"rel\":[\"self\"]," +
            "\"href\":\"/v1/devices/1\"},{\"rel\":[\"devices\"],\"href\":\"/v1/devices\"}]}"

    const val UPDATED_DEVICE = "{\"class\":[\"device\"],\"properties\":{\"id\":1,\"name\":\"New device name updated\"," +
            "\"category\":\"water\",\"state\":\"active\",\"timestamp\":1658827989789},\"links\":[{\"rel\":[\"self\"]," +
            "\"href\":\"/v1/devices/1\"}]}"

    const val DEACTIVATE_DEVICE = "{\"class\":[\"device\"],\"properties\":{\"id\":1,\"name\":\"Toilet1\",\"category\"" +
            ":\"water\",\"state\":\"inactive\",\"timestamp\":1658828219323},\"links\":[{\"rel\":[\"self\"],\"href\":" +
            "\"/v1/devices/1\"},{\"rel\":[\"devices\"],\"href\":\"/v1/devices\"}]}"

    const val CHANGE_CATEGORY_DEVICE = "{\"class\":[\"device\"],\"properties\":{\"id\":1,\"name\":\"Toilet1\"," +
            "\"category\":\"electricity\",\"state\":\"active\",\"timestamp\":1658828441559},\"links\":[{\"rel\":" +
            "[\"self\"],\"href\":\"/v1/devices/1\"}]}"

    const val ROOM_DEVICES = "{\"class\":[\"device\",\"collection\"],\"properties\":{\"pageIndex\":1," +
            "\"pageMaxSize\":10,\"collectionSize\":1},\"entities\":[{\"class\":[\"device\"],\"rel\":" +
            "[\"item\"],\"properties\":{\"id\":1,\"name\":\"Toilet1\",\"category\":\"water\",\"state\":" +
            "\"active\",\"timestamp\":1658828778749},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/devices/1\"}]}]," +
            "\"actions\":[{\"name\":\"add-room-device\",\"title\":\"Add device\",\"method\":\"POST\",\"href\":\"" +
            "/v1/companies/1/buildings/1/rooms/1/devices\",\"type\":\"application/json\",\"properties\":[{\"name\"" +
            ":\"device\",\"type\":\"number\",\"possibleValues\":{\"href\":\"/v1/devices\"}}]}],\"links\":[{\"rel\"" +
            ":[\"self\"],\"href\":\"/v1/devices\"},{\"rel\":[\"pagination\"],\"href\":\"/v1/companies/{companyId}/" +
            "buildings/{buildingId}/rooms/{roomId}/devices{?page}\",\"templated\":true}]}"

    const val ROOM_DEVICE = "{\"class\":[\"device\"],\"properties\":{\"id\":1,\"name\":\"Toilet1\",\"category" +
            "\":\"water\",\"state\":\"active\",\"timestamp\":1658828941054},\"entities\":[{\"class\":[\"qrcode\"]" +
            ",\"rel\":[\"room-device-qrcode\"],\"properties\":{\"qrcode\":\"/v1/companies/1/buildings/1/rooms/1/" +
            "devices/1/qrcode\"},\"actions\":[{\"name\":\"generate-new-qrcode\",\"title\":\"Generate new QR Code\"" +
            ",\"method\":\"POST\",\"href\":\"/v1/companies/1/buildings/1/rooms/1/devices/1/qrcode\"}],\"links\":[]}]" +
            ",\"actions\":[{\"name\":\"remove-room-device\",\"title\":\"Remove device\",\"method\":\"DELETE\",\"href" +
            "\":\"/v1/companies/1/buildings/1/rooms/1/devices/1\"}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/" +
            "companies/1/buildings/1/rooms/1/devices/1\"},{\"rel\":[\"room\"],\"href\":\"/v1/companies/1/buildings/" +
            "1/rooms/1\"}]}"
}