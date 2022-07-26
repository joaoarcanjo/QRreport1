package pt.isel.ps.project.integrationtests.building

object BuildingExpectedRepresentations {
    const val GET_BUILDINGS = "{\"class\":[\"building\",\"collection\"],\"properties\":{\"pageIndex\":1,\"pageMaxSize\"" +
            ":10,\"collectionSize\":3},\"entities\":[{\"class\":[\"building\"],\"rel\":[\"item\"],\"properties\":{\"id" +
            "\":1,\"name\":\"A\",\"floors\":4,\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"" +
            "/v1/companies/1/buildings/1\"}]},{\"class\":[\"building\"],\"rel\":[\"item\"],\"properties\":{" +
            "\"id\":2,\"name\":\"F\",\"floors\":6,\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":" +
            "\"/v1/companies/1/buildings/2\"}]},{\"class\":[\"building\"],\"rel\":[\"item\"],\"properties\":{\"id" +
            "\":3,\"name\":\"Z\",\"floors\":6,\"state\":\"inactive\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"" +
            "/v1/companies/1/buildings/3\"}]}],\"actions\":[{\"name\":\"create-building\",\"title\":\"Create building" +
            "\",\"method\":\"POST\",\"href\":\"/v1/companies/1/buildings\",\"type\":\"application/json\",\"properties" +
            "\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"floors\",\"type\":\"number\"},{\"name\":\"" +
            "managerId\",\"type\":\"string\",\"possibleValues\":{\"href\":\"/v1/persons?page=1&company=1&role=manager" +
            "\"}}]}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/1/buildings?page=1\"},{\"rel\":[\"" +
            "pagination\"],\"href\":\"/v1/companies/{companyId}/buildings{?page}\",\"templated\":true}]}"
    const val CREATE_BUILDING = "{\"class\":[\"building\"],\"properties\":{\"id\":4,\"name\":\"C\",\"floors\":4," +
            "\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/1/buildings/4\"}]}"
    const val GET_BUILDING = "{\"class\":[\"building\"],\"properties\":{\"id\":1,\"name\":\"A\",\"floors\":4,\"state" +
            "\":\"active\"},\"entities\":[{\"class\":[\"room\",\"collection\"],\"rel\":[\"building-rooms\"],\"" +
            "properties\":{\"pageIndex\":1,\"pageMaxSize\":10,\"collectionSize\":2},\"entities\":[{\"class\":[\"room" +
            "\"],\"rel\":[\"item\"],\"properties\":{\"id\":1,\"name\":\"1 - Bathroom\",\"floor\":1,\"state\":\"active" +
            "\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/1/buildings/1/rooms/1\"}]},{\"class\":[\"room" +
            "\"],\"rel\":[\"item\"],\"properties\":{\"id\":2,\"name\":\"2\",\"floor\":1,\"state\":\"active\"},\"links" +
            "\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/1/buildings/1/rooms/2\"}]}],\"actions\":[{\"name\":\"" +
            "create-room\",\"title\":\"Create room\",\"method\":\"POST\",\"href\":\"/v1/companies/1/buildings/1/rooms" +
            "\",\"type\":\"application/json\",\"properties\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"" +
            "floor\",\"type\":\"number\"}]}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/1/buildings/1/" +
            "rooms?page=1\"},{\"rel\":[\"pagination\"],\"href\":\"/v1/companies/{companyId}/buildings/{buildingId}/" +
            "rooms{?page}\",\"templated\":true}]},{\"class\":[\"person\"],\"rel\":[\"building-manager\"],\"" +
            "properties\":{\"id\":\"4b341de0-65c0-4526-8898-24de463fc315\",\"name\":\"Diogo Novo\",\"phone\":" +
            "\"961111111\",\"email\":\"diogo@qrreport.com\",\"roles\":[\"admin\",\"manager\"],\"state\":\"" +
            "active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons/4b341de0-65c0-4526-8898-24de463fc315" +
            "\"}]}],\"actions\":[{\"name\":\"deactivate-building\",\"title\":\"Deactivate building\",\"method\":" +
            "\"POST\",\"href\":\"/v1/companies/1/buildings/1/deactivate\"},{\"name\":\"update-building\"" +
            ",\"title\":\"Update building\",\"method\":\"PUT\",\"href\":\"/v1/companies/1/buildings/1\",\"type\":\"" +
            "application/json\",\"properties\":[{\"name\":\"name\",\"type\":\"string\",\"required\":false},{\"name\"" +
            ":\"floors\",\"type\":\"number\",\"required\":false}]},{\"name\":\"change-building-manager\",\"title\":" +
            "\"Change building manager\",\"method\":\"PUT\",\"href\":\"/v1/companies/1/buildings/1/manager\",\"type\"" +
            ":\"application/json\",\"properties\":[{\"name\":\"managerId\",\"type\":\"string\",\"possibleValues\":{\"" +
            "href\":\"/v1/persons?page=1&company=1&role=manager\"}}]}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/" +
            "companies/1/buildings/1\"}]}"
    const val UPDATE_BUILDING = "{\"class\":[\"building\"],\"properties\":{\"id\":1,\"name\":\"C.v2\",\"floors\":5,\"" +
            "state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/1/buildings/1\"}]}"
    const val DEACTIVATE_BUILDING = "{\"class\":[\"building\"],\"properties\":{\"id\":1,\"name\":\"A\",\"floors\":4,\"" +
            "state\":\"inactive\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/1/buildings/1\"},{\"rel\":[" +
            "\"company\"],\"href\":\"/v1/companies/1\"}]}"
    const val ACTIVATE_BUILDING = "{\"class\":[\"building\"],\"properties\":{\"id\":3,\"name\":\"Z\",\"floors\":6,\"" +
            "state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/1/buildings/3\"},{\"rel\":[" +
            "\"company\"],\"href\":\"/v1/companies/1\"}]}"
    const val CHANGE_BUILDING_MANAGER = "{\"class\":[\"building\"],\"properties\":{\"id\":2,\"name\":\"F\",\"manager\"" +
            ":\"4b341de0-65c0-4526-8898-24de463fc315\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/1/buildings/2\"}]}"
}