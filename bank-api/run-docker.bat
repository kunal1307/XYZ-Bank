@echo off

echo Building Maven package...
call mvn clean package
if errorlevel 1 exit /b 1

echo Building Docker image...
docker build -t bank-api .
if errorlevel 1 exit /b 1

echo Running Docker container...
docker run -p 8080:8080 ^
  --add-host=host.docker.internal:host-gateway ^
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/yourdb ^
  -e DB_USERNAME=your_DB_username^
  -e DB_PASSWORD=your_DB_password ^
  bank-api