**Simple User Management and Authentication System**

This is a simple REST web service for a user object. The service uses an SQLite database for persistence of users.

The following endpoints are exposed:

"/api/v1/users" GET endpoint for listing all users.

"/api/v1/users/{id}" GET endpoint for retrieval of a specific user by ID.

"/api/v1/users" POST endpoint for creating a new user. The fields email and password are required.

"/api/v1/users/{id}" DELETE endpoint for deleting a user by ID.

"/api/v1/users/{id}" PUT endpoint for changing a users name, email and password in a single transaction.

"/api/v1/users/{id}" PATCH endpoint for changing only a portion of a users information.

"/api/v1/users/{id}/login" GET endpoint for validating a users email and password against database records.

"/api/v1/users/login" GET endpoint for validating a users email and password against database records, when an id is not provided.   
 
