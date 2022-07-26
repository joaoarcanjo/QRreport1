package pt.isel.ps.project.integrationtests.person

object PersonExpectedRepresentations {

    const val GET_PERSONS = "{\"class\":[\"person\",\"collection\"],\"properties\":{\"pageIndex\":1,\"pageMaxSize\"" +
            ":10,\"collectionSize\":6},\"entities\":[{\"class\":[\"person\"],\"rel\":[\"item\"],\"properties\":{\"id" +
            "\":\"1f6c1014-b029-4a75-b78c-ba09c8ea474d\",\"name\":\"João Arcanjo\",\"email\":\"joni@isel.com\",\"roles" +
            "\":[\"admin\"],\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons/1f6c1014-" +
            "b029-4a75-b78c-ba09c8ea474d\"}]},{\"class\":[\"person\"],\"rel\":[\"item\"],\"properties\":{\"id\":" +
            "\"5e63ea2f-53cf-4546-af41-f0b3a20eac91\",\"name\":\"António Ricardo\",\"email\":\"antonio@isel.com\"" +
            ",\"roles\":[\"manager\"],\"state\":\"banned\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons/" +
            "5e63ea2f-53cf-4546-af41-f0b3a20eac91\"}]},{\"class\":[\"person\"],\"rel\":[\"item\"],\"properties\":{" +
            "\"id\":\"b555b6fc-b904-4bd9-8c2b-4895738a437c\",\"name\":\"Francisco Ludovico\",\"phone\":\"9653456345" +
            "\",\"email\":\"ludviks@gmail.com\",\"roles\":[\"user\"],\"state\":\"active\"},\"links\":[{\"rel\":[\"" +
            "self\"],\"href\":\"/v1/persons/b555b6fc-b904-4bd9-8c2b-4895738a437c\"}]},{\"class\":[\"person\"],\"rel" +
            "\":[\"item\"],\"properties\":{\"id\":\"b9063a7e-7ba4-42d3-99f4-1b00e00db55d\",\"name\":\"Daniela Gomes" +
            "\",\"email\":\"dani@isel.com\",\"roles\":[\"guest\"],\"state\":\"active\"},\"links\":[{\"rel\":[\"self" +
            "\"],\"href\":\"/v1/persons/b9063a7e-7ba4-42d3-99f4-1b00e00db55d\"}]},{\"class\":[\"person\"],\"rel\":[" +
            "\"item\"],\"properties\":{\"id\":\"c2b393be-d720-4494-874d-43765f5116cb\",\"name\":\"Zé Manuel\",\"phone" +
            "\":\"965555555\",\"email\":\"zeze@fixings.com\",\"roles\":[\"employee\"],\"skills\":[\"water\"," +
            "\"electricity\"],\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons/" +
            "c2b393be-d720-4494-874d-43765f5116cb\"}]},{\"class\":[\"person\"],\"rel\":[\"item\"],\"properties" +
            "\":{\"id\":\"d1ad1c02-9e4f-476e-8840-c56ae8aa7057\",\"name\":\"Pedro Miguens\",\"phone\":\"963333333" +
            "\",\"email\":\"pedro@isel.com\",\"roles\":[\"manager\"],\"state\":\"active\"},\"links\":[{\"rel\":[" +
            "\"self\"],\"href\":\"/v1/persons/d1ad1c02-9e4f-476e-8840-c56ae8aa7057\"}]}],\"actions\":[{\"name\":" +
            "\"create-person\",\"title\":\"Create person\",\"method\":\"POST\",\"href\":\"/v1/persons\",\"type" +
            "\":\"application/json\",\"properties\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"phone\"," +
            "\"type\":\"string\",\"required\":false},{\"name\":\"email\",\"type\":\"string\"},{\"name\":\"password" +
            "\",\"type\":\"string\"},{\"name\":\"role\",\"type\":\"string\",\"possibleValues\":{\"href\":" +
            "\"/v1/persons/roles\"}},{\"name\":\"company\",\"type\":\"number\",\"possibleValues\":{\"href\":" +
            "\"/v1/companies\"}},{\"name\":\"skill\",\"type\":\"number\",\"required\":false,\"possibleValues" +
            "\":{\"href\":\"/v1/categories\"}}]}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons?page=1" +
            "\"},{\"rel\":[\"pagination\"],\"href\":\"/v1/persons{?page}\",\"templated\":true}]}"

    const val CREATED_PERSON = "{\"class\":[\"person\"],\"properties\":{\"id\":\"f8501e08-6445-423b-b33f-" +
            "7d11876ce254\",\"name\":\"Person name test\",\"phone\":\"965536771\",\"email\":\"person@isel.pt" +
            "\",\"roles\":[\"admin\"],\"timestamp\":1658829807671,\"state\":\"active\"},\"links\":[{\"rel\":" +
            "[\"self\"],\"href\":\"/v1/persons/f8501e08-6445-423b-b33f-7d11876ce254\"}]}"

    const val GET_PERSON = "{\"class\":[\"person\"],\"properties\":{\"id\":\"d1ad1c02-9e4f-476e-8840-c56ae8aa7057" +
            "\",\"name\":\"Pedro Miguens\",\"phone\":\"963333333\",\"email\":\"pedro@isel.com\",\"roles\":[\"manager" +
            "\"],\"companies\":[\"ISEL\"],\"timestamp\":1658829969662,\"state\":\"active\"},\"actions\":[{\"name\":" +
            "\"rehire-person\",\"title\":\"Rehire person\",\"method\":\"POST\",\"href\":\"/v1/companies/{companyId}" +
            "/persons/d1ad1c02-9e4f-476e-8840-c56ae8aa7057/rehire\",\"templated\":true,\"type\":\"application/json\"," +
            "\"properties\":[{\"name\":\"company\",\"type\":\"number\",\"possibleValues\":{\"href\":\"/v1/companies?" +
            "page=1&userId=d1ad1c02-9e4f-476e-8840-c56ae8aa7057&state=inactive\"}}]},{\"name\":\"fire-person\"," +
            "\"title\":\"Fire person\",\"method\":\"POST\",\"href\":\"/v1/companies/{companyId}/persons/d1ad1c02-9e4f" +
            "-476e-8840-c56ae8aa7057/fire\",\"templated\":true,\"type\":\"application/json\",\"properties\":[{\"name" +
            "\":\"company\",\"type\":\"number\",\"possibleValues\":{\"href\":\"/v1/companies?page=1&userId=d1ad1c02" +
            "-9e4f-476e-8840-c56ae8aa7057&state=active\"}},{\"name\":\"reason\",\"type\":\"string\"}]},{\"name\":" +
            "\"assign-to-company\",\"title\":\"Assign to company\",\"method\":\"POST\",\"href\":\"/v1/persons/d1ad1" +
            "c02-9e4f-476e-8840-c56ae8aa7057/assign-company\",\"type\":\"application/json\",\"properties\":[{\"name" +
            "\":\"company\",\"type\":\"number\",\"possibleValues\":{\"href\":\"/v1/companies?page=1&userId=d1ad1c02" +
            "-9e4f-476e-8840-c56ae8aa7057&assign=true&state=active\"}}]},{\"name\":\"add-role\",\"title\":\"Add role" +
            "\",\"method\":\"PUT\",\"href\":\"/v1/persons/d1ad1c02-9e4f-476e-8840-c56ae8aa7057/add-role\",\"type\":" +
            "\"application/json\",\"properties\":[{\"name\":\"role\",\"type\":\"string\",\"possibleValues\":{\"href" +
            "\":\"/v1/persons/roles\"}},{\"name\":\"company\",\"type\":\"number\",\"possibleValues\":{\"href\":" +
            "\"/v1/companies?page=1&state=active\"}},{\"name\":\"skill\",\"type\":\"number\",\"required\":false," +
            "\"possibleValues\":{\"href\":\"/v1/categories\"}}]}],\"links\":[{\"rel\":[\"self\"],\"href\":" +
            "\"/v1/persons/d1ad1c02-9e4f-476e-8840-c56ae8aa7057\"}]}"

    const val GET_PROFILE = "{\"class\":[\"person\"],\"properties\":{\"id\":\"4b341de0-65c0-4526-8898-24de463fc315" +
            "\",\"name\":\"Diogo Novo\",\"phone\":\"961111111\",\"email\":\"diogo@qrreport.com\",\"roles\":[\"admin" +
            "\",\"manager\"],\"companies\":[\"ISEL\"],\"timestamp\":1658830167653,\"state\":\"active\"},\"actions\"" +
            ":[{\"name\":\"update-person\",\"title\":\"Update person\",\"method\":\"PUT\",\"href\":\"/v1/persons/4b" +
            "341de0-65c0-4526-8898-24de463fc315\",\"type\":\"application/json\",\"properties\":[{\"name\":\"name\"," +
            "\"type\":\"string\",\"required\":false},{\"name\":\"phone\",\"type\":\"string\",\"required\":false},{" +
            "\"name\":\"email\",\"type\":\"string\",\"required\":false},{\"name\":\"password\",\"type\":\"string\"," +
            "\"required\":false}]},{\"name\":\"switch-role\",\"title\":\"Switch role\",\"method\":\"POST\",\"href\":" +
            "\"/v1/profile/switch-role\",\"type\":\"application/json\",\"properties\":[{\"name\":\"role\",\"type\":" +
            "\"string\"}]}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons/4b341de0-65c0-4526-8898-24de463fc315" +
            "\"}]}"

    const val UPDATED_PERSON = "{\"class\":[\"person\"],\"properties\":{\"id\":\"d1ad1c02-9e4f-476e-8840-c56ae8aa7057" +
            "\",\"name\":\"Person test updated\",\"phone\":\"963333333\",\"email\":\"test@isel.pt\",\"roles\":" +
            "[\"manager\"],\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons/d1ad1c02" +
            "-9e4f-476e-8840-c56ae8aa7057\"}]}"

    const val DELETED_USER = "{\"class\":[\"person\"],\"properties\":{\"id\":\"b555b6fc-b904-4bd9-8c2b-4895738a437c" +
            "\",\"name\":\"b555b6fc-b904-4bd9-8c2b-4895738a437c\",\"email\":\"b555b6fc-b904-4bd9-8c2b-4895738a437c" +
            "@deleted.com\",\"roles\":[\"user\"],\"timestamp\":1658832807838,\"state\":\"inactive\",\"reason\":\"" +
            "User deleted account\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons/b555b6fc-b904-4bd9-8c2b" +
            "-4895738a437c\"}]}"

    const val FIRED_PERSON = "{\"class\":[\"person\"],\"properties\":{\"id\":\"c2b393be-d720-4494-874d-43765f5116cb" +
            "\",\"name\":\"Zé Manuel\",\"phone\":\"965555555\",\"email\":\"zeze@fixings.com\",\"roles\":[\"employee" +
            "\"],\"skills\":[\"water\",\"electricity\"],\"timestamp\":1658833487334,\"state\":\"inactive\",\"reason" +
            "\":\"Bad behaviour\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons/c2b393be-d720-4494-874d-" +
            "43765f5116cb\"}]}"

    const val REHIRE_PERSON = "{\"class\":[\"person\"],\"properties\":{\"id\":\"d1ad1c02-9e4f-476e-8840-c56ae8aa7057" +
            "\",\"name\":\"Pedro Miguens\",\"phone\":\"963333333\",\"email\":\"pedro@isel.com\",\"roles\":[\"manager" +
            "\"],\"companies\":[\"ISEL\"],\"timestamp\":1658834201183,\"state\":\"active\"},\"links\":[{\"rel\":[" +
            "\"self\"],\"href\":\"/v1/persons/d1ad1c02-9e4f-476e-8840-c56ae8aa7057\"}]}"

    const val BAN_PERSON = "{\"class\":[\"person\"],\"properties\":{\"id\":\"d1ad1c02-9e4f-476e-8840-c56ae8aa7057" +
            "\",\"name\":\"Pedro Miguens\",\"phone\":\"963333333\",\"email\":\"pedro@isel.com\",\"roles\":[\"manager" +
            "\"],\"companies\":[\"ISEL\"],\"timestamp\":1658840575356,\"state\":\"banned\",\"reason\":\"Bad behaviour" +
            "\",\"bannedBy\":{\"id\":\"4b341de0-65c0-4526-8898-24de463fc315\",\"name\":\"Diogo Novo\",\"phone\":" +
            "\"961111111\",\"email\":\"diogo@qrreport.com\",\"roles\":[\"admin\",\"manager\"],\"state\":\"active" +
            "\"}},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons/d1ad1c02-9e4f-476e-8840-c56ae8aa7057\"}]}"

    const val UNBAN_PERSON = "{\"class\":[\"person\"],\"properties\":{\"id\":\"5e63ea2f-53cf-4546-af41-f0b3a20eac91" +
            "\",\"name\":\"António Ricardo\",\"email\":\"antonio@isel.com\",\"roles\":[\"manager\"],\"timestamp" +
            "\":1658840359995,\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons/5e63e" +
            "a2f-53cf-4546-af41-f0b3a20eac91\"}]}"

    const val ADD_ROLE_PERSON = "{\"class\":[\"person\"],\"properties\":{\"id\":\"c2b393be-d720-4494-874d-43765f5116cb" +
            "\",\"name\":\"Zé Manuel\",\"phone\":\"965555555\",\"email\":\"zeze@fixings.com\",\"roles\":[\"employee" +
            "\",\"admin\"],\"skills\":[\"water\",\"electricity\"],\"companies\":[\"ISEL\"],\"timestamp" +
            "\":1658841083638,\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons/c2b39" +
            "3be-d720-4494-874d-43765f5116cb\"}]}"

    const val REMOVE_ROLE_PERSON = "{\"class\":[\"person\"],\"properties\":{\"id\":\"4b341de0-65c0-4526-8898-24de" +
            "463fc315\",\"name\":\"Diogo Novo\",\"phone\":\"961111111\",\"email\":\"diogo@qrreport.com\",\"roles\"" +
            ":[\"admin\"],\"timestamp\":1658841306973,\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\"" +
            ":\"/v1/persons/4b341de0-65c0-4526-8898-24de463fc315\"}]}"

    const val ADD_SKILL_EMPLOYEE = "{\"class\":[\"person\"],\"properties\":{\"id\":\"c2b393be-d720-4494-874d-43765" +
            "f5116cb\",\"name\":\"Zé Manuel\",\"phone\":\"965555555\",\"email\":\"zeze@fixings.com\",\"roles\":[" +
            "\"employee\"],\"skills\":[\"water\",\"electricity\",\"window\"],\"state\":\"active\"},\"links\":[{" +
            "\"rel\":[\"self\"],\"href\":\"/v1/persons/c2b393be-d720-4494-874d-43765f5116cb\"}]}"

    const val REMOVE_SKILL_EMPLOYEE = "{\"class\":[\"person\"],\"properties\":{\"id\":\"c2b393be-d720-4494-874d" +
            "-43765f5116cb\",\"name\":\"Zé Manuel\",\"phone\":\"965555555\",\"email\":\"zeze@fixings.com\",\"roles" +
            "\":[\"employee\"],\"skills\":[\"water\"],\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href" +
            "\":\"/v1/persons/c2b393be-d720-4494-874d-43765f5116cb\"}]}"

    const val ASSIGN_PERSON_COMPANY = "{\"class\":[\"person\"],\"properties\":{\"id\":\"c2b393be-d720-4494-" +
            "874d-43765f5116cb\",\"name\":\"Zé Manuel\",\"phone\":\"965555555\",\"email\":\"zeze@fixings.com" +
            "\",\"roles\":[\"employee\"],\"skills\":[\"water\",\"electricity\"],\"companies\":[\"ISEL\",\"IST" +
            "\"],\"timestamp\":1658842131306,\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":" +
            "\"/v1/persons/c2b393be-d720-4494-874d-43765f5116cb\"}]}"

    const val SWITCH_ROLE_PERSON = "{\"class\":[\"person\"],\"properties\":{\"id\":\"4b341de0-65c0-4526-8898-" +
            "24de463fc315\",\"name\":\"Diogo Novo\",\"phone\":\"961111111\",\"email\":\"diogo@qrreport.com\"," +
            "\"roles\":[\"admin\",\"manager\"],\"companies\":[\"ISEL\"],\"timestamp\":1658842635005,\"state\":" +
            "\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons/4b341de0-65c0-4526-8898-24de463fc315" +
            "\"},{\"rel\":[\"companies\"],\"href\":\"/v1/companies\"},{\"rel\":[\"persons\"],\"href\":\"/v1/persons" +
            "\"},{\"rel\":[\"tickets\"],\"href\":\"/v1/tickets\"}]}"

    const val GET_ROLES = "{\"class\":[\"collection\",\"role\"],\"properties\":{\"pageIndex\":1,\"pageMaxSize" +
            "\":5,\"collectionSize\":5},\"entities\":[{\"class\":[\"role\"],\"rel\":[\"item\"],\"properties" +
            "\":{\"id\":2,\"name\":\"user\"}},{\"class\":[\"role\"],\"rel\":[\"item\"],\"properties\":{\"id\":3," +
            "\"name\":\"employee\"}},{\"class\":[\"role\"],\"rel\":[\"item\"],\"properties\":{\"id\":4,\"name\":" +
            "\"manager\"}},{\"class\":[\"role\"],\"rel\":[\"item\"],\"properties\":{\"id\":5,\"name\":\"admin\"}}]}"
}