package pt.isel.ps.project.integrationtests.ticket

object TicketExpectedRepresentations {

    const val GET_TICKETS = "{\"class\":[\"ticket\",\"collection\"],\"properties\":{\"pageIndex\":1,\"pageMaxSize" +
            "\":10,\"collectionSize\":3},\"entities\":[{\"class\":[\"ticket\"],\"rel\":[\"item\"],\"properties\":{" +
            "\"id\":1,\"subject\":\"Fuga de água\",\"description\":\"A sanita está a deixar sair água por baixo" +
            "\",\"company\":\"ISEL\",\"building\":\"A\",\"room\":\"1 - Bathroom\",\"userState\":\"Fixing\"," +
            "\"employeeState\":\"Fixing\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/tickets/1\"}]},{" +
            "\"class\":[\"ticket\"],\"rel\":[\"item\"],\"properties\":{\"id\":2,\"subject\":\"Infiltração na " +
            "parede\",\"description\":\"Os cães começaram a roer a corda e acabaram por fugir todos, foi " +
            "assustador\",\"company\":\"ISEL\",\"building\":\"A\",\"room\":\"1 - Bathroom\",\"userState\":" +
            "\"Waiting analysis\",\"employeeState\":\"To assign\"},\"links\":[{\"rel\":[\"self\"],\"href\":" +
            "\"/v1/tickets/2\"}]},{\"class\":[\"ticket\"],\"rel\":[\"item\"],\"properties\":{\"id\":3,\"subject" +
            "\":\"Archived ticket\",\"description\":\"Archived ticket description\",\"company\":\"ISEL\",\"building" +
            "\":\"A\",\"room\":\"1 - Bathroom\",\"userState\":\"Archived\",\"employeeState\":\"Archived\"}," +
            "\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/tickets/3\"}]}],\"links\":[{\"rel\":[\"self\"],\"href" +
            "\":\"/v1/tickets?page=1&direction=desc&sortBy=date\"},{\"rel\":[\"pagination\"],\"href\":\"/v1/" +
            "persons{?page}&direction=desc&sortBy=date\",\"templated\":true}]}"

    const val GET_TICKET = "{\"class\":[\"ticket\"],\"properties\":{\"id\":1,\"subject\":\"Fuga de água\"," +
            "\"description\":\"A sanita está a deixar sair água por baixo\"," +
            "\"employeeState\":\"Fixing\",\"userState\":\"Fixing\",\"possibleTransitions\":[{\"id\":6,\"name\":" +
            "\"Completed\"}]},\"entities\":[{\"class\":[\"comment\",\"collection\"],\"rel\":[\"ticket-comments" +
            "\"],\"properties\":{\"pageIndex\":1,\"pageMaxSize\":10,\"collectionSize\":2},\"entities\":[{\"class" +
            "\":[\"comment\"],\"rel\":[\"item\"],\"properties\":{\"id\":1,\"comment\":\"Esta sanita não tem" +
            " arranjo, vou precisar de uma nova.\"},\"entities\":[{\"class\":[" +
            "\"person\"],\"rel\":[\"comment-author\"],\"properties\":{\"id\":\"c2b393be-d720-4494-874d-43765f5116cb" +
            "\",\"name\":\"Zé Manuel\",\"phone\":\"965555555\",\"email\":\"zeze@fixings.com\",\"roles\":[\"employee" +
            "\"],\"skills\":[\"electricity\",\"water\"],\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href" +
            "\":\"/v1/persons/c2b393be-d720-4494-874d-43765f5116cb\"}]}],\"actions\":[],\"links\":[{\"rel\":[\"self" +
            "\"],\"href\":\"/v1/tickets/1/comments/1\"}]},{\"class\":[\"comment\"],\"rel\":[\"item\"],\"properties" +
            "\":{\"id\":2,\"comment\":\"Tente fazer o possível para estancar a fuga.\"}," +
            "\"entities\":[{\"class\":[\"person\"],\"rel\":[\"comment-author\"],\"properties\":{\"id\":" +
            "\"4b341de0-65c0-4526-8898-24de463fc315\",\"name\":\"Diogo Novo\",\"phone\":\"961111111\",\"email\":" +
            "\"diogo@qrreport.com\",\"roles\":[\"admin\",\"manager\"],\"state\":\"active\"},\"links\":[{\"rel\":[" +
            "\"self\"],\"href\":\"/v1/persons/4b341de0-65c0-4526-8898-24de463fc315\"}]}],\"actions\":[{\"name\":" +
            "\"update-comment\",\"title\":\"Update comment\",\"method\":\"PUT\",\"href\":\"/v1/tickets/1/comments/2" +
            "\",\"type\":\"application/json\",\"properties\":[{\"name\":\"comment\",\"type\":\"string\"}]},{\"name" +
            "\":\"delete-comment\",\"title\":\"Delete comment\",\"method\":\"DELETE\",\"href\":\"/v1/tickets/1/" +
            "comments/2\"}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/tickets/1/comments/2\"}]}],\"actions" +
            "\":[{\"name\":\"create-comment\",\"title\":\"Create comment\",\"method\":\"POST\",\"href\":" +
            "\"/v1/tickets/1/comments\",\"type\":\"application/json\",\"properties\":[{\"name\":\"comment\"," +
            "\"type\":\"string\"}]}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/tickets/1/comments?page=1" +
            "\"},{\"rel\":[\"pagination\"],\"href\":\"/v1/tickets/{ticketId}/comments{?page}\",\"templated" +
            "\":true}]},{\"class\":[\"company\"],\"rel\":[\"ticket-company\"],\"properties\":{\"id\":1,\"name" +
            "\":\"ISEL\",\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href" +
            "\":\"/v1/companies/1\"}]},{\"class\":[\"building\"],\"rel\":[\"ticket-building\"],\"properties\":{" +
            "\"id\":1,\"name\":\"A\",\"floors\":4,\"state\":\"active\"},\"links\":[{" +
            "\"rel\":[\"self\"],\"href\":\"/v1/companies/1/buildings/1\"}]},{\"class\":[\"room\"],\"rel\":[" +
            "\"ticket-room\"],\"properties\":{\"id\":1,\"name\":\"1 - Bathroom\",\"floor\":1,\"state\":\"active" +
            "\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/companies/1/buildings/" +
            "1/rooms/1\"}]},{\"class\":[\"device\"],\"rel\":[\"ticket-device\"],\"properties\":{\"id\":1,\"name\":" +
            "\"Toilet1\",\"category\":\"water\",\"state\":\"active\"},\"links\":[{\"rel" +
            "\":[\"self\"],\"href\":\"/v1/devices/1\"}]},{\"class\":[\"person\"],\"rel\":[\"ticket-author\"]," +
            "\"properties\":{\"id\":\"b555b6fc-b904-4bd9-8c2b-4895738a437c\",\"name\":\"Francisco Ludovico\"," +
            "\"phone\":\"9653456345\",\"email\":\"ludviks@gmail.com\",\"roles\":[\"user\"],\"state\":\"active\"}," +
            "\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons/b555b6fc-b904-4bd9-8c2b-4895738a437c\"}]},{" +
            "\"class\":[\"person\"],\"rel\":[\"ticket-employee\"],\"properties\":{\"id\":\"c2b393be-d720-4494-874d" +
            "-43765f5116cb\",\"name\":\"Zé Manuel\",\"phone\":\"965555555\",\"email\":\"zeze@fixings.com\",\"roles" +
            "\":[\"employee\"],\"skills\":[\"electricity\",\"water\"],\"state\":\"active\"},\"links\":[{\"rel\":[" +
            "\"self\"],\"href\":\"/v1/persons/c2b393be-d720-4494-874d-43765f5116cb\"}]}],\"actions\":[{\"name\":" +
            "\"update-state\",\"title\":\"Update state\",\"method\":\"PUT\",\"href\":\"/v1/tickets/1/state\"," +
            "\"type\":\"application/json\",\"properties\":[{\"name\":\"state\",\"type\":\"number\"}]},{\"name\":" +
            "\"remove-employee\",\"title\":\"Remove employee\",\"method\":\"DELETE\",\"href\":\"/v1/tickets/1/" +
            "employee\"}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/tickets/1\"},{\"rel\":[\"tickets\"]," +
            "\"href\":\"/v1/tickets\"}]}"

    const val CREATED_TICKET = "{\"class\":[\"ticket\"],\"properties\":{\"id\":4,\"subject\":\"Ticket subject test" +
            "\",\"description\":\"Ticket description\",\"company\":\"ISEL\",\"building\":\"A\",\"room\":\"1 - " +
            "Bathroom\",\"userState\":\"Waiting analysis\",\"employeeState\":\"To assign\"},\"links\":[{\"rel" +
            "\":[\"self\"],\"href\":\"/v1/tickets/4\"}]}"

    const val UPDATED_TICKET = "{\"class\":[\"ticket\"],\"properties\":{\"id\":2,\"subject\":\"Ticket subject " +
            "update\",\"description\":\"Ticket description update\",\"company\":\"ISEL\",\"building\":\"A\"," +
            "\"room\":\"1 - Bathroom\",\"userState\":\"Waiting analysis\",\"employeeState\":\"To assign\"}," +
            "\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/tickets/2\"}]}"

    const val CHANGE_TICKET_STATE = "{\"class\":[\"ticket\"],\"properties\":{\"id\":1,\"subject\":\"Fuga de água" +
            "\",\"description\":\"A sanita está a deixar sair água por baixo\",\"company\":\"ISEL\",\"building" +
            "\":\"A\",\"room\":\"1 - Bathroom\",\"userState\":\"Completed\",\"employeeState\":\"Completed\"}," +
            "\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/tickets/1\"}]}"

    const val ADD_TICKET_RATE = "{\"class\":[\"ticket\"],\"properties\":{\"id\":3,\"subject\":\"Archived ticket" +
            "\",\"description\":\"Archived ticket description\",\"userState\":\"Archived\",\"employeeState\":\"7" +
            "\",\"rate\":5},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/tickets/3\"}]}"

    const val GET_SPECIFIC_EMPLOYEES = "{\"class\":[\"person\",\"collection\"],\"properties\":{\"pageIndex\":1," +
            "\"pageMaxSize\":10,\"collectionSize\":1},\"entities\":[{\"class\":[\"person\"],\"rel\":[\"item\"]," +
            "\"properties\":{\"id\":\"c2b393be-d720-4494-874d-43765f5116cb\",\"name\":\"Zé Manuel\",\"phone\":" +
            "\"965555555\",\"email\":\"zeze@fixings.com\",\"roles\":[\"employee\"],\"skills\":[\"electricity\"," +
            "\"water\"],\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons/c2b393be" +
            "-d720-4494-874d-43765f5116cb\"}]}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/tickets/2/employee?" +
            "page=1\"},{\"rel\":[\"pagination\"],\"href\":\"/v1/tickets/2/employee{?page}\",\"templated\":true}]}"

    const val SET_EMPLOYEE = "{\"class\":[\"ticket\"],\"properties\":{\"id\":2,\"subject\":\"Infiltração na parede" +
            "\",\"description\":\"Os cães começaram a roer a corda e acabaram por fugir todos, foi assustador\"," +
            "\"company\":\"ISEL\",\"building\":\"A\",\"room\":\"1 - Bathroom\",\"userState\":\"Not started\"," +
            "\"employeeState\":\"Not started\"},\"entities\":[{\"class\":[\"person\"],\"rel\":[\"ticket-employee" +
            "\"],\"properties\":{\"id\":\"c2b393be-d720-4494-874d-43765f5116cb\",\"name\":\"Zé Manuel\",\"phone\":" +
            "\"965555555\",\"email\":\"zeze@fixings.com\",\"roles\":[\"employee\"],\"skills\":[\"electricity\"," +
            "\"water\"],\"state\":\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons/c2b393be" +
            "-d720-4494-874d-43765f5116cb\"}]}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/tickets/2\"}]}"

    const val REMOVE_EMPLOYEE = "{\"class\":[\"ticket\"],\"properties\":{\"id\":1,\"subject\":\"Fuga de água\"," +
            "\"description\":\"A sanita está a deixar sair água por baixo\",\"company\":\"ISEL\",\"building\":\"A" +
            "\",\"room\":\"1 - Bathroom\",\"userState\":\"Waiting analysis\",\"employeeState\":\"To assign\"}," +
            "\"entities\":[{\"class\":[\"person\"],\"rel\":[\"ticket-employee\"],\"properties\":{\"id\":" +
            "\"c2b393be-d720-4494-874d-43765f5116cb\",\"name\":\"Zé Manuel\",\"phone\":\"965555555\",\"email" +
            "\":\"zeze@fixings.com\",\"roles\":[\"employee\"],\"skills\":[\"electricity\",\"water\"],\"state\":" +
            "\"active\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/persons/c2b393be-d720-4494-874d-43765f5116cb" +
            "\"}]}],\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/tickets/1\"}]}"

    const val GROUP_TICKET = "{\"class\":[\"ticket\"],\"properties\":{\"id\":2,\"subject\":\"Infiltração na parede" +
            "\",\"description\":\"Os cães começaram a roer a corda e acabaram por fugir todos, foi assustador\"," +
            "\"company\":\"ISEL\",\"building\":\"A\",\"room\":\"1 - Bathroom\",\"userState\":\"Fixing\"," +
            "\"employeeState\":\"Fixing\"},\"links\":[{\"rel\":[\"self\"],\"href\":\"/v1/tickets/2\"}]}"

    const val EMPLOYEE_STATES = "{\"class\":[\"state\",\"collection\"],\"rel\":[\"tickets-states\"]," +
            "\"properties\":{\"pageIndex\":1,\"pageMaxSize\":5,\"collectionSize\":7},\"entities\":[{\"class" +
            "\":[\"state\"],\"rel\":[\"item\"],\"properties\":{\"id\":1,\"name\":\"To assign\"}},{\"class\":[" +
            "\"state\"],\"rel\":[\"item\"],\"properties\":{\"id\":2,\"name\":\"Refused\"}},{\"class\":[\"state" +
            "\"],\"rel\":[\"item\"],\"properties\":{\"id\":3,\"name\":\"Not started\"}},{\"class\":[\"state" +
            "\"],\"rel\":[\"item\"],\"properties\":{\"id\":4,\"name\":\"Fixing\"}},{\"class\":[\"state\"],\"rel" +
            "\":[\"item\"],\"properties\":{\"id\":5,\"name\":\"Waiting for material\"}}],\"links\":[{\"rel" +
            "\":[\"self\"],\"href\":\"/v1/tickets/states?page=1\"},{\"rel\":[\"pagination\"],\"href\":" +
            "\"/v1/tickets/states{?page}\",\"templated\":true}]}"
}