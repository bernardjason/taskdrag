FOR /F "tokens=* USEBACKQ" %%F IN (`dir /B javafx*.jar`) DO (
SET jar=%%F
)
java -jar %jar%
