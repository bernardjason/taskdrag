curl -X POST localhost:8080/freetext -H 'Content-type:application/json' -d '{ "text":"more '$RANDOM'" , "work":{ "id" : 12 } }' | json_pp
