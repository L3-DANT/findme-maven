# Findme (server side)

This is the web service used in the Findme iOS app.<br> It provides url that are meant to interact with the database, and by so, manage the app users.

## Summary
* [Prerequisites](#prerequesits)
    - [Glassfish](#glassfish)
    - [MongoDB](#mongodb)
* [Deploying](#deploying)
	- [Downloading](#downloading)
    - [Configuration](#configuration)
    - [Launching](#launching)
* [Provided URLs](#provided-urls)
    - [User managing](#user-managing)
    - [Friend request managing](#friend-request-managing)
* [Error handling](#error-handling)
    - [HTTP Codes](#http-codes)
    - [Logs](#logs)
* [Tests](#tests)
    - [Testing DAOs](#testing-daos)
    - [Testing services](#tesing-services)
    - [Testing Controllers](#tesing-controllers)

## Prerequisites
#### Glassfish
Findme uses Sun Microsystems' application server [Glassfish](https://glassfish.java.net/).
It requires the **Full Platform version**

Download here : https://glassfish.java.net/download.html

###### Warning :
There's a bug with Glassfish 4.1.1 causing the Classloader being unable to find Jersey's Json parser.<br>
To get rid of it, follow these steps :

* Download [MANIFEST.MF](https://bugs.eclipse.org/bugs/attachment.cgi?id=251917)
* Replace the MANIFEST.MF file in the org.eclipse.persistence.moxy.jar file with the one you downloaded :<br>
`path\to\glassfish4\glassfish\modules\org.eclipse.persistence.moxy.jar`
* Restart Glassfish

#### MongoDB
Findme's database is the NoSQL the document oriented [MongoDB](https://www.mongodb.com/).<br>
[Download](https://www.mongodb.com/download-center?jmp=nav#community) it and install it. <br>
You will have to start the mongo database before deploying the application.

See the documentation : https://docs.mongodb.com/manual/installation/


## Deploying

#### Downloading

Download the latest binary from the repository, or just clone it :
```bash
$ git clone https://github.com/L3-DANT/findme-maven.git
```

#### Configuration
The configuration is handled by the configuration file in `src/main/resources/config.properties`.<br>
Follow the config.properties.dist file to fill it depending on your own configuration.<br>
Don't forget to install dependencies with Maven.<br>
You may need to change the file where the logs are printed. To do so, change the **fileName** param in  `src/main/resources/log4j2.xml` file :
```
<Appenders>
    <File name="File" fileName="c:/logs.log">
        <PatternLayout pattern="[%-5level] %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %c{1} - %msg%n"/>
    </File>
</Appenders>
```

#### Launching
Just launch te mongo daemon, then you can run the `findme.war:exploded` artifact on the glassfish server.
You can start calling URLs from your navigator, or with curl for instance.

## Provided URLs
Note that all the URLs have have to be preceded by "/api/".<br>
For instance :
```
localhost:8080/findme/api/user/v1/Alfred
```

All requests but the GET and DELETE ones have to provide data in JSON format.<br>
Every response should have a 200 or 204 code.

#### User managing

| URL           | Method |Description   |
|:-------------:|:----:|:-----------------:|
| user/v1/:pseudo     | GET | Gets a user |
| user/v1     | PUT | Inserts a user      |
| user/v1 | POST | Updates a user<br> **• Sends a pusher event "friends-removed" when friends are removed**<br>**• Sends a pusher event "position-udpated" when only coordinates are given** |
| user/v1/login | POST | Checks provided credentials and gets the user     |
| user/v1/:pseudo | DELETE |Deletes a user<br>**Sends a pusher event "friends-removed" if the user had friends**|

Exemple of json user to provide :

    {
        "pseudo":"John",
        "password":"qwerty123"
    }

#### Friend Request managing

| URL           | Method |Description   |
|:-------------:|:----:|:-----------------:|
| friendrequest/v1?caller=:pseudo1&receiver=:pseudo2     | GET | Gets a friendrequest<br>or a list of all the pseudos that called<br>or received the friendrequest if only<br>one parameter is given|
| friendrequest/v1     | PUT | Inserts a friendrequest      |
| friendrequest/v1 | POST | Accepts a friendrequest<br>Both users related to it will have<br>the other in its friendlist<br>The friendrequest will then be<br>removed from the database |
| friendrequest/v1?caller=:pseudo1&receiver=:pseudo2 | DELETE |Deletes a friendrequest|

Exemple of json friendrequest to provide (the pseudos have to match real users in database) :

    {
        "caller":"Fred",
        "receiver":"Jamy"
    }

## Error handling

#### Http Codes
The findme server uses HTTP codes to handle errors.<br>
Here's a list of all the codes related to errors :
* **400 - Bad request** : &nbsp;&nbsp;When trying to add friends from the update url (POST user/v1) .To do so, use friendrequest urls.
* **401 - Unauthorized** : When bad credentials are provided when trying to log in, or adding a FriendRequest when users are already friends.
* **404 - Not found** : &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;When the requested resource can not be found in the database.
* **409 - Conflict** : &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;When trying to duplicate resources

Note that this list shows errors supposing the right URL with the right data format was called.

#### Logs
Logs are written in the file you specified in the `log4j2.xml` configuration file. It will write every time an url is called at the end of the file, and will print the Exception's stack trace if one was thrown.

## Tests

Tests are written in `test/` folder. You have nothing else to do than just launching the test Classes.
Note that if you launch every test at once, some of them will fail somehow. It may be due to the embedded mongo database launched both by DAOs and controllers tests.

#### Testing DAOs

DAOs are unit tested with [JUnit](http://junit.org/junit4/).<br>
However, it has real interactions with a database, thanks to [Embedded MongoDB](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo).

#### Testing Services

Services are unit tested aswell, using [JUnit](http://junit.org/junit4/), [Mockito](http://mockito.org/) and [Powermock](https://github.com/jayway/powermock).
The DAOs are mocked.

#### Testing Controllers

Controllers are integration tested. It uses the [Embedded MongoDB](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo) database, and real objects (such as services or DAOs)