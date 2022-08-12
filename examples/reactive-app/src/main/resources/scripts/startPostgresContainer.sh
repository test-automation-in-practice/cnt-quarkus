docker run -it --rm=true --name library -e POSTGRES_USER=book -e POSTGRES_PASSWORD=library -e POSTGRES_DB=library -p 5437:5432 postgres:14.1
