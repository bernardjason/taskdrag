# taskdrag

This is a Kanban style to do list with username/password authentication, spring backend with a web and a javafx GUI.

This started as a simple project, have a small play with spring and javafx. Then morphed a little to this.

Backend server is Spring, with Sqlite db store. This holds user/passwords, work items, free text associated with
work as well as work states, by default in db are created, inprogress, waiting, done then with implicit remove.

GUI tests use testfx for javafx front end and Selenium for web. There was a problem with webdrivers and drag
and drop so this test needs human to do the dragging and dropping. Javafx tests this works as expected.

<img src="/videos/taskdrag.gif" width="640"/>

To try out download the latest release
execute 
```
backend.sh
or 
backend.bat
```
Then for web demo access

http://127.0.0.1:8080

Login as user bernard with a password of jason

Default 4 states created as none exist in Sqlite table task_states
```
"created",      "#ffb3b3"
"inprogress",   "#ccffcc"
"waiting",      "#ff8888"
"done",         "#ffff99"
```

To run the javafx gui (**note** this needs the backend to continue running) execute 
```
gui.sh
or
gui.bat
```

To run in dev
./mvnw spring-boot:run -Dspring-boot.run.profiles=sqlite   -pl common,model,database,web

## note about jdk11 and javafx
With JDK11 start with
-Djdk.gtk.version=2
otherwise drag and drop not reliable


## make a release
```
./mvwn release:prepare
./mvnw release:perform -Darguments="-Dmaven.deploy.skip=true"
```

mp4 to gif
```
ffmpeg  -i taskdrag.mp4 -vf "fps=5,scale=640:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse" -loop 0 taskdrag.gif
```
