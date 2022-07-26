package pt.isel.ps.project.integrationtests.company

object CompanyExpectedRepresentations {
    const val GET_COMPANIES = "{\"class\":[\"company\",\"collection\"],\"properties\":{\"pageIndex\":1,\"page" +
            "MaxSize\":10,\"collectionSize\":3},\"entities\":[{\"class\":[\"company\"],\"rel\":[\"item\"],\"" +
            "properties\":{\"id\":1,\"name\":\"ISEL\",\"state\":\"active\"},\"links" +
            "\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/1\"}]},{\"class\":[\"company\"],\"rel\":[\"item\"]" +
            ",\"properties\":{\"id\":2,\"name\":\"IST\",\"state\":\"active\"},\"link" +
            "s\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/2\"}]},{\"class\":[\"company\"],\"rel\":[\"item\"]," +
            "\"properties\":{\"id\":3,\"name\":\"IPMA\",\"state\":\"inactive\"},\"lin" +
            "ks\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/3\"}]}],\"actions\":[{\"name\":\"create-company\"," +
            "\"title\":\"Create company\",\"method\":\"POST\",\"href\":\"/v1/companies\",\"type\":\"application/json" +
            "\",\"properties\":[{\"name\":\"name\",\"type\":\"string\"}]}],\"links\":[{\"rel\":[\"self\"],\"href\":" +
            "\"/v1/companies?page=1\"},{\"rel\":[\"pagination\"],\"href\":\"/v1/companies{?page}\",\"templated\":true}]}"
    const val GET_COMPANY = "{\"class\":[\"company\"],\"properties\":{\"id\":1,\"name\":\"ISEL\",\"state\":\"active\"" +
            "},\"entities\":[{\"class\":[\"building\",\"collection\"],\"rel\":[\"company-" +
            "buildings\"],\"properties\":{\"pageIndex\":1,\"pageMaxSize\":10,\"collectionSize\":3},\"entities\":[{\"" +
            "class\":[\"building\"],\"rel\":[\"item\"],\"properties\":{\"id\":1,\"name\":\"A\",\"floors\":4,\"state\"" +
            ":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/1/" +
            "buildings/1\"}]},{\"class\":[\"building\"],\"rel\":[\"item\"],\"properties\":{\"id\":2,\"name\":\"F\"," +
            "\"floors\":6,\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":" +
            "\"/v1/companies/1/buildings/2\"}]},{\"class\":[\"building\"],\"rel\":[\"item\"],\"properties\":{\"id\":3," +
            "\"name\":\"Z\",\"floors\":6,\"state\":\"inactive\"},\"links\":[{\"rel\":[\"" +
            "self\"],\"href\":\"/v1/companies/1/buildings/3\"}]}],\"actions\":[{\"name\":\"create-building\",\"title\"" +
            ":\"Create building\",\"method\":\"POST\",\"href\":\"/v1/companies/1/buildings\",\"type\":\"application/json" +
            "\",\"properties\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"floors\",\"type\":\"number\"},{\"" +
            "name\":\"managerId\",\"type\":\"string\",\"possibleValues\":{\"href\":\"/v1/persons?page=1&company=1&" +
            "role=manager\"}}]}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/1/buildings?page=1\"},{\"" +
            "rel\":[\"pagination\"],\"href\":\"/v1/companies/{companyId}/buildings{?page}\",\"templated\":true}]}]," +
            "\"actions\":[{\"name\":\"deactivate-company\",\"title\":\"Deactivate company\",\"method\":\"POST\",\"" +
            "href\":\"/v1/companies/1/deactivate\"},{\"name\":\"update-company\",\"title\":\"Update company\",\"" +
            "method\":\"PUT\",\"href\":\"/v1/companies/1\",\"type\":\"application/json\",\"properties\":[{\"name\"" +
            ":\"name\",\"type\":\"string\"}]}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/1\"},{\"rel" +
            "\":[\"companies\"],\"href\":\"/v1/companies\"}]}"
    const val CREATE_COMPANY = "{\"class\":[\"company\"],\"properties\":{\"id\":4,\"name\":\"Google Portugal\",\"state" +
            "\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/4\"}]}"
    const val UPDATE_COMPANY = "{\"class\":[\"company\"],\"properties\":{\"id\":1,\"name\":\"ISEL University\",\"state" +
            "\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/1\"}]}"
    const val DEACTIVATE_COMPANY = "{\"class\":[\"company\"],\"properties\":{\"id\":1,\"name\":\"ISEL\",\"state\":\"" +
            "inactive\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/1\"}," +
            "{\"rel\":[\"companies\"],\"href\":\"/v1/companies\"}]}"
    const val ACTIVATE_COMPANY = "{\"class\":[\"company\"],\"properties\":{\"id\":3,\"name\":\"IPMA\",\"state\":\"" +
            "active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/3\"},{\"" +
            "rel\":[\"companies\"],\"href\":\"/v1/companies\"}]}"
}