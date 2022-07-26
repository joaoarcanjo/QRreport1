package pt.isel.ps.project.integrationtests.categories

object CategoriesExpectedRepresentations {
    const val GET_CATEGORIES = "{\"class\":[\"category\",\"collection\"],\"properties\":{\"pageIndex\":1,\"pageMaxSize" +
            "\":10,\"collectionSize\":4},\"entities\":[{\"class\":[\"category\"],\"rel\":[\"item\"],\"properties\":{" +
            "\"id\":1,\"name\":\"water\",\"state\":\"active\"},\"actions\":[{\"name\":\"update-category\",\"title\":" +
            "\"Update category\",\"method\":\"PUT\",\"href\":\"/v1/categories/1\",\"type\":\"application/json\",\"" +
            "properties\":[{\"name\":\"name\",\"type\":\"string\"}]}],\"links\":[]},{\"class\":[\"category\"],\"rel\"" +
            ":[\"item\"],\"properties\":{\"id\":2,\"name\":\"electricity\",\"state\":\"active\"},\"actions\":[{\"name" +
            "\":\"update-category\",\"title\":\"Update category\",\"method\":\"PUT\",\"href\":\"/v1/categories/2\"," +
            "\"type\":\"application/json\",\"properties\":[{\"name\":\"name\",\"type\":\"string\"}]}],\"links\":[]}," +
            "{\"class\":[\"category\"],\"rel\":[\"item\"],\"properties\":{\"id\":3,\"name\":\"garden\",\"state\":\"" +
            "inactive\"},\"actions\":[{\"name\":\"activate-category\",\"title\":\"Activate category\",\"method\":\"" +
            "POST\",\"href\":\"/v1/categories/3/activate\"}],\"links\":[]},{\"class\":[\"category\"],\"rel\":[\"item" +
            "\"],\"properties\":{\"id\":4,\"name\":\"window\",\"state\":\"active\"},\"actions\":[{\"name\":\"" +
            "update-category\",\"title\":\"Update category\",\"method\":\"PUT\",\"href\":\"/v1/categories/4\",\"type" +
            "\":\"application/json\",\"properties\":[{\"name\":\"name\",\"type\":\"string\"}]},{\"name\":\"deactivate" +
            "-category\",\"title\":\"Deactivate category\",\"method\":\"POST\",\"href\":\"/v1/categories/4/deactivate" +
            "\"}],\"links\":[]}],\"actions\":[{\"name\":\"create-category\",\"title\":\"Create category\",\"method\":" +
            "\"POST\",\"href\":\"/v1/categories\",\"type\":\"application/json\",\"properties\":[{\"name\":\"name\",\"" +
            "type\":\"string\"}]}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/categories?page=1\"},{\"rel\":[\"" +
            "pagination\"],\"href\":\"/v1/categories{?page}\",\"templated\":true}]}"
    const val CREATE_CATEGORY = "{\"class\":[\"category\"],\"properties\":{\"category\":{\"id\":5,\"name\":\"" +
            "farm-machines\",\"state\":\"active\"},\"inUse\":false},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/categories\"}]}"
    const val UPDATE_CATEGORY = "{\"class\":[\"category\"],\"properties\":{\"category\":{\"id\":1,\"name\":\"big-" +
            "machines\",\"state\":\"active\"},\"inUse\":true},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/categories\"}]}"
    const val DEACTIVATE_CATEGORY = "{\"class\":[\"category\"],\"properties\":{\"category\":{\"id\":4,\"name\":\"" +
            "window\",\"state\":\"inactive\"},\"inUse\":false},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/" +
            "categories/4\"},{\"rel\":[\"categories\"],\"href\":\"/v1/categories\"}]}"
    const val ACTIVATE_CATEGORY = "{\"class\":[\"category\"],\"properties\":{\"category\":{\"id\":3,\"name\":\"garden" +
            "\",\"state\":\"active\"},\"inUse\":false},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/categories\"}]}"
}