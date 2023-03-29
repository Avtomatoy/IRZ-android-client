#!/bin/bash

if [[ $swagger = "true" ]]
then
	start https://localhost:7116/swagger/index.html
fi

dotnet run --property:Environment=Development --project ./IrzUccApi/IrzUccApi/IrzUccApi.csproj

