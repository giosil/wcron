# WCron

A job scheduler configurable through RESTful web services.
The scheduler contains the following entities:

* Activity: identified by name specifies the class that will perform the task and the default execution parameters;
* Job: specifies acitivity schedulation with the optional execution parameters.


The class is identified by Activity.uri as follows:

* mock: no-action job;
* package.ClassName: class in {user.home}/wcron/classes
* java:global/[application]/[module]/EJBName

## Build

- `git clone https://github.com/giosil/wcron.git`
- `mvn clean install`

## API 

### Info

Request:
GET `http://localhost:8080/wcron/scheduler/manager/info`

Response:
HTTP 200
`{"name":"wcron","version":"1.0.5","activities":1,"jobs":1}`

### listActivities

GET `http://localhost:8080/wcron/scheduler/manager/listActivities`

Response:
HTTP 200
`[{"name":"demo","uri":"mock","parameters":{"greeting":"hello"},"createdAt":1584313200000}]`

### addActivity

Request:
POST `http://localhost:8080/wcron/scheduler/manager/addActivity`
`{"name":"test", "uri":"test.JobTest", "parameters":{"greeting":"hello"}}`

Response:
HTTP 200
`true` | `false`

### removeActivity (by activityName)

Request:
GET `http://localhost:8080/wcron/scheduler/manager/removeActivity/{activityName}`

Response:
HTTP 200
`true` | `false`

### listJobs

Request:
GET `http://localhost:8080/wcron/scheduler/manager/listJobs`

Response:
HTTP 200
`[{"id":1,"activity":{"name":"demo","uri":"mock","parameters":{"greeting":"hello"},"createdAt":1584313200000},"expression":"1000 5000","parameters":null,"running":false,"requestInterrupt":false,"lastResult":"1584455410924","lastError":"","createdAt":1584313200000,"elapsed":1}]`

### schedule without execution parameters

Request:
GET `http://localhost:8080/wcron/scheduler/manager/schedule/{activityName}/{expression}`

Response:
HTTP 200
`jobId` (number)

### schedule with execution parameters

Request:
POST `http://localhost:8080/wcron/scheduler/manager/schedule/{activityName}/{expression}`
`{"greeting": "hello"}`

Response:
HTTP 200
`jobId` (number)

### removeJob

Request:
GET `http://localhost:8080/wcron/scheduler/manager/removeJob/{jobId}`

Response:
HTTP 200
`true` | `false`

### getJob

Request:
GET `http://localhost:8080/wcron/scheduler/manager/getJob/{jobId}`

Response:
HTTP 200
`{"id":1,"activity":{"name":"demo","uri":"mock","parameters":{"greeting":"hello"},"createdAt":1584313200000},"expression":"1000 5000","parameters":null,"running":false,"requestInterrupt":false,"lastResult":"1584455850925","lastError":"","createdAt":1584313200000,"elapsed":1}`

## Contributors

* [Giorgio Silvestris](https://github.com/giosil)
