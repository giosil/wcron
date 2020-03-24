# WCron

A job scheduler configurable through RESTful web services.
The scheduler contains the following entities:

* **Activity**: identified by *name* specifies the class that will perform the task and the default execution parameters;
* **Job**: specifies acitivity schedulation with the optional execution parameters.


The class is identified by Activity.uri as follows:

* **mock**: no-action job;
* **package.ClassName**: class in {user.home}/wcron/classes;
* **java:global/[application]/[module]/EJBName**: JNDI lookup name of EJB.

## Build

- `git clone https://github.com/giosil/wcron.git`
- `mvn clean install`
- `docker build -t wcron .` - this will create a Docker image named wcron
- `docker run --rm -it -p 8080:8080 wcron` - To run image named wcron

### Optimize Virtual hard disks on Windows 10

- Shutdown Docker Desktop
- `Optimize-VHD -Path "C:\Users\Public\Documents\Hyper-V\Virtual hard disks\DockerDesktop.vhdx" -Mode Full`
- Start Docker Desktop

## API 

Access to RESTful services is through HTTP basic authentication.<br />
For test use dev:dev.

### Info
Get application info.<br />

Request:<br />
**GET** `http://localhost:8080/wcron/scheduler/manager/info`

Response:<br />
HTTP 200 OK<br />
`object`

Example response:<br />
`{"name":"wcron","version":"1.0.6","activities":1,"jobs":1}`

### listActivities
Get list of activities.<br />

Request:<br />
**GET** `http://localhost:8080/wcron/scheduler/manager/listActivities`

Response:<br />
HTTP 200 OK<br />
`array[object]`

Example response:<br />
`[{"name":"demo","uri":"mock","parameters":{"greeting":"hello"},"createdAt":1584313200000}]`

### getActivityNames
Get list of activity name available.<br />

Request:<br />
**GET** `http://localhost:8080/wcron/scheduler/manager/getActivityNames`

Response:<br />
HTTP 200 OK<br />
`array[string]`

Example response:<br />
`["demo"]`

### addActivity
Define an activity.<br />

Request:<br />
**POST** `http://localhost:8080/wcron/scheduler/manager/addActivity`<br />
`object`

Request example:<br />
`{"name":"test", "uri":"test.JobTest", "parameters":{"greeting":"hello"}}`

Response:<br />
HTTP 200 OK<br />
`boolean`

### removeActivity (by activityName)
Delete an activity.<br />

Request:<br />
**GET** `http://localhost:8080/wcron/scheduler/manager/removeActivity/{activityName}`

Response:<br />
HTTP 200 OK<br />
`boolean`

### listJobs
Get list of scheduled jobs.<br />

Request:<br />
**GET** `http://localhost:8080/wcron/scheduler/manager/listJobs`

Response:<br />
HTTP 200 OK<br />
`array[object]`

Response example:<br />
`[{"id":1,"activity":{"name":"demo","uri":"mock","parameters":{"greeting":"hello"},"createdAt":1584313200000},"expression":"1000 5000","parameters":null,"running":false,"requestInterrupt":false,"lastResult":"1584455410924","lastError":"","createdAt":1584313200000,"elapsed":1}]`

### schedule (without execution parameters)
Schedule a job.<br />

Request:<br />
**GET** `http://localhost:8080/wcron/scheduler/manager/schedule/{activityName}/{expression}`

Response:<br />
HTTP 200 OK<br />
`integer` *(jobId)*

### schedule (with execution parameters)
Schedule a job.<br />

Request:<br />
**POST** `http://localhost:8080/wcron/scheduler/manager/schedule/{activityName}/{expression}`<br />
`object`

Request example:<br />
`{"greeting": "hello"}`

Response:<br />
HTTP 200 OK<br />
`integer` *(jobId)*

### removeJob
Delete a job.<br />

Request:<br />
**GET** `http://localhost:8080/wcron/scheduler/manager/removeJob/{jobId}`

Response:<br />
HTTP 200 OK<br />
`boolean`

### getJob
Get job info.<br />

Request:<br />
**GET** `http://localhost:8080/wcron/scheduler/manager/getJob/{jobId}`

Response:<br />
HTTP 200 OK<br />
`object`

Example response:<br />
`{"id":1,"activity":{"name":"demo","uri":"mock","parameters":{"greeting":"hello"},"createdAt":1584313200000},"expression":"1000 5000","parameters":null,"running":false,"requestInterrupt":false,"lastResult":"1584455850925","lastError":"","createdAt":1584313200000,"elapsed":1}`

Response if not found:<br />
HTTP 404 Not found<br />

### clean
Clean output folder.<br />

Request:<br />
**GET** `http://localhost:8080/wcron/scheduler/manager/clean`<br />

Response:<br />
HTTP 200 OK<br />
`boolean`

## Contributors

* [Giorgio Silvestris](https://github.com/giosil)
