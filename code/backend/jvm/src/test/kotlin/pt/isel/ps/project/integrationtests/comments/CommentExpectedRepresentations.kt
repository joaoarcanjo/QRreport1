package pt.isel.ps.project.integrationtests.comments

object CommentExpectedRepresentations{

    const val GET_COMMENTS = "{\"class\":[\"comment\",\"collection\"],\"properties\":{\"pageIndex\":1,\"pageMaxSize" +
            "\":10,\"collectionSize\":2},\"entities\":[{\"class\":[\"comment\"],\"rel\":[\"item\"],\"properties\":{" +
            "\"id\":2,\"comment\":\"Tente fazer o possível para estancar a fuga.\",\"timestamp\":1658850067577}," +
            "\"entities\":[{\"class\":[\"person\"],\"rel\":[\"comment-author\"],\"properties\":{\"id\":\"4b341de0" +
            "-65c0-4526-8898-24de463fc315\",\"name\":\"Diogo Novo\",\"phone\":\"961111111\",\"email\":\"diogo" +
            "@qrreport.com\",\"roles\":[\"admin\",\"manager\"],\"state\":\"active\"},\"links\":[{\"rel\":[" +
            "\"self\"],\"href\":\"/v1/persons/4b341de0-65c0-4526-8898-24de463fc315\"}]}],\"actions\":[{\"name\":" +
            "\"update-comment\",\"title\":\"Update comment\",\"method\":\"PUT\",\"href\":\"/v1/tickets/1/comments/2" +
            "\",\"type\":\"application/json\",\"properties\":[{\"name\":\"comment\",\"type\":\"string\"}]},{\"name" +
            "\":\"delete-comment\",\"title\":\"Delete comment\",\"method\":\"DELETE\",\"href\":\"/v1/tickets/1" +
            "/comments/2\"}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/tickets/1/comments/2\"}]},{\"class\":[" +
            "\"comment\"],\"rel\":[\"item\"],\"properties\":{\"id\":1,\"comment\":\"Esta sanita não tem arranjo, " +
            "vou precisar de uma nova.\",\"timestamp\":1658850067577},\"entities\":[{\"class\":[\"person\"],\"rel" +
            "\":[\"comment-author\"],\"properties\":{\"id\":\"c2b393be-d720-4494-874d-43765f5116cb\",\"name\":" +
            "\"Zé Manuel\",\"phone\":\"965555555\",\"email\":\"zeze@fixings.com\",\"roles\":[\"employee\"]," +
            "\"skills\":[\"water\",\"electricity\"],\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href" +
            "\":\"/v1/persons/c2b393be-d720-4494-874d-43765f5116cb\"}]}],\"actions\":[],\"links\":[{\"rel\":[" +
            "\"self\"],\"href\":\"/v1/tickets/1/comments/1\"}]}],\"actions\":[{\"name\":\"create-comment\",\"title" +
            "\":\"Create comment\",\"method\":\"POST\",\"href\":\"/v1/tickets/1/comments\",\"type\":" +
            "\"application/json\",\"properties\":[{\"name\":\"comment\",\"type\":\"string\"}]}],\"links\":[{\"rel" +
            "\":[\"self\"],\"href\":\"/v1/tickets/1/comments?page=1\"},{\"rel\":[\"pagination\"],\"href\":" +
            "\"/v1/tickets/{ticketId}/comments{?page}\",\"templated\":true}]}"

    const val CREATE_COMMENT = "{\"class\":[\"comment\"],\"properties\":{\"id\":3,\"comment\":\"Comment test\"," +
            "\"timestamp\":1658850437592},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/tickets/1/comments\"}]}"

    const val UPDATE_COMMENT = "{\"class\":[\"comment\"],\"properties\":{\"id\":2,\"comment\":\"Comment update test" +
            "\",\"timestamp\":1658850872861},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/tickets/1/comments\"}]}"

    const val DELETE_COMMENT = "{\"class\":[\"comment\"],\"properties\":{\"id\":2,\"comment\":\"Tente fazer o " +
            "possível para estancar a fuga.\",\"timestamp\":1658851116219},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/tickets/1/comments/2\"},{\"rel\":[\"comments\"],\"href\":\"/v1/tickets/1/comments\"}]}"
}

