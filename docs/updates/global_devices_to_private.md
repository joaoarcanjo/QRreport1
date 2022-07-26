### Steps to change devices from global to private for each company as well as its anomalies

* **Change data model** to make a relationship between DEVICE and COMPANY (N-1)
* **Change physical model** accordingly, i.e., the DEVICE table should be updated to have a foreign key referencing the company the device corresponds
* The **paths of the devices resources** must be changed to /{companyId}/devices ...
* The **functions and procedures of the devices resources** should be updated to receive the companyId and retrieve the devices accordingly
* The **authorizations and permissions** to access the devices have to be changed, since now all the company managers can access the devices and the anomalies as well as change them.