#!/bin/bash

curl --verbose -X POST \
  --header "Content-Type: application/zip" -F filedata=@test/test.zip \
  http://localhost:8000/zip
