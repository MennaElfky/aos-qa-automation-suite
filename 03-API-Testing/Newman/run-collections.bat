@echo off
newman run ..\postman\collections\aos-api.postman_collection.json -e ..\postman\environments\qa.postman_environment.json
