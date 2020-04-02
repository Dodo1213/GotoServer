# GotoServer
A service, which makes links shorter.

# Basic Information
The service requires you to have a password in order to create shorter links. This is made in order to prevent spam not 
only from bots, but from other people you don't want to use your personalized goto server. The password set into the 
config.json is a bcrypt hash, you can get one for your password from [here](https://www.browserling.com/tools/bcrypt).
The default one set is `admin`.

The service is using Spring to provide fast speeds for responses. We do support the `/favicon.ico` route, however I wasn't
able to make it working on the pages. Installing one is also easy: the config.json has a path for it, you can put it
whatever you want. The default location is where the application is ran.

# Usage 
The first thing you need to do is to retrieve a jar. You have to have maven installed on your computer! Simply run
`mvn package` and get jar from `/target`. 

The second thing you need to do is to host it somewhere. The port the program is running at is 8118 . You can change it
in the `/src/main/resources/application.properties` before retrieving a jar. The hosting machine should have at least
java 8 in order to run!

There is a UI at the default page which generates random shortened links. However, if you want custom links, you may want
to take a look at the `/customCreate` route which is also a UI. But don't get upset! There is also an API if you want to
use the service with OS installable application. The routes are `/api/create` for random shortened url creation, and `/api/createCustom`
for custom shortened url. 

# There it is 
Very simple to use & install, made in java, program which makes links shorter!
