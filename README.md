# KYS
![Build Status](https://img.shields.io/travis/PotatoCurry/KYS.svg)
![Code Quality](https://img.shields.io/codacy/grade/e80c52878b2b4e40a4ac96cfac27d609.svg)
![Issues](https://img.shields.io/github/issues/PotatoCurry/KYS.svg)
![License](https://img.shields.io/github/license/PotatoCurry/KYS.svg)

Kotlin-based YES system for querying volunteer hours

## API
KYS provides a simple JSON API that can be freely accessed by appending `/json` to student queries.
An example is shown below.

HTTP GET Request
```http request
GET https://chskys.herokuapp.com/query/625783/json
Content-Type: application/json
```
JSON Response
```json
{
  "firstName" : "Damian",
  "lastName" : "Lall",
  "gradClass" : 2021,
  "records" : [ {
    "agency" : "School related",
    "startDate" : "10/16/17",
    "endDate" : "4/6/18",
    "hours" : 18.0,
    "extraHours" : 0.0,
    "description" : "Volunteered to help out at the FSMS coding club.  Tought studetnts Java and helped them rite programs.",
    "summer" : false
  }, {
    "agency" : "School related",
    "startDate" : "8/31/18",
    "endDate" : "11/3/18",
    "hours" : 16.0,
    "extraHours" : 0.0,
    "description" : "Taught Java and Scratch to kids at FSMS.",
    "summer" : false
  }, {
    "agency" : "School related",
    "startDate" : "2/5/19",
    "endDate" : "2/16/19",
    "hours" : 12.0,
    "extraHours" : 0.0,
    "description" : "Set p and managed PC2 system for UIL at Travis high school.",
    "summer" : false
  } ],
  "totalExtraHours" : 0.0,
  "totalHours" : 46.0
}
```

A working example of such usage can be found in the [HuskyBot](https://github.com/PotatoCurry/HuskyBot) YES query command.

As KYS is under constant development, no guarantees are made as to the stability of the API, and breaking changes may be introduced without notice.
