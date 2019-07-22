# API authentication keys
client_id = "1a41385a-48e7-41d8-bf7e-a8048ec300e8"
client_secret = "88de7df1-1585-4b57-9cc6-fb3d8c4d0cb8"
 = "http://localhost:8000/callback"

# import package dependencies
import smartcar

# create smartcar authentication client
smartcar.AuthClient(
    client_id = client_id,
    client_secret = client_secret,
    redirect_uri = redirect_uri,
    scope = ["required:read_vehicle_info read_fuel"],
    mode = test
)
