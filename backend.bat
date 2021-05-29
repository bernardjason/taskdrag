FOR /F "tokens=* USEBACKQ" %%F IN (`dir /B web*.jar`) DO (
SET jar=%%F
)
java -Dspring.profiles.active=prod -jar %jar%
