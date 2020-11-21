[How to install cURL (Linux)](https://linuxhint.com/install-curl-linux) <br>
[How to install cURL (Windows)](https://stackoverflow.com/questions/9507353/how-do-i-install-and-use-curl-on-windows#answer-9507379)

## Get to know a REST API with cURL

First, you need to create your own profile. 
To do this, you must provide a unique name (2-32 symbols) and password (4-16 symbols).

- ##### Register
```
# POST /users
curl -i -X POST http://localhost:8080/topjava-graduation/users -H 'Content-Type:application/json;charset=UTF-8' -d '
{
    "name":"Mr.Creeper", 
    "password":"88005553535"
}'
```

So, now you can send requests that require [HTTP Basic authentication](https://en.wikipedia.org/wiki/Basic_access_authentication).

## Profile

You received your profile in response to a registration request. Also, you can get this using the following request.

- ##### Get your own profile
```
# GET /profile
curl -i http://localhost:8080/topjava-graduation/profile --user Mr.Creeper:88005553535
```

You also can update and delete your own profile. <br><br>
Let's change the password.

- ##### Update your own profile
```
# PUT /profile
curl -i -X PUT http://localhost:8080/topjava-graduation/profile -H 'Content-Type:application/json;charset=UTF-8' -d '
{
    "name":"Mr.Creeper", 
    "password":"newPa55w0rd"
}' --user Mr.Creeper:88005553535
```
Now let's delete the profile.
- ##### Delete your own profile
```
# DELETE /profile
curl -i -X DELETE http://localhost:8080/topjava-graduation/profile --user Mr.Creeper:newPa55w0rd
```
> [Register](#Register) again to continue.

OK. Now you know how to manage your profile. <br><br>
The last request associated with your profile is
- ##### Get the Restaurant you voted for
```
# GET /profile/vote
curl -i http://localhost:8080/topjava-graduation/profile/vote --user Mr.Creeper:88005553535
```
No content - you haven't voted yet, but we'll fix that later. 

## General

Since this is a demo application, it has three pre-registered users. Let's take a look at them.

- ##### Get all Users
```
# GET /users
curl -i http://localhost:8080/topjava-graduation/users --user Mr.Creeper:88005553535
```

- ##### Get User by ID
```
# GET /users/id{id}
curl -i http://localhost:8080/topjava-graduation/users/id1 --user Mr.Creeper:88005553535
```

- ##### Get User by name
```
# GET /users/{name}
curl -i http://localhost:8080/topjava-graduation/users/1_Admin --user Mr.Creeper:88005553535
```
As you can see, there are two user roles: [ADMIN](#Administrator-role) and [USER](#User-role). Each of the roles has unique privileges.
<br><br>
Similarly, you can get information about pre-registered restaurants.

- ##### Get all Restaurants
```
# GET /restaurants
curl -i http://localhost:8080/topjava-graduation/restaurants --user Mr.Creeper:88005553535
```

- ##### Get a Restaurant by name
```
# GET /restaurants/{name}
curl -i http://localhost:8080/topjava-graduation/restaurants/Godzik --user Mr.Creeper:88005553535
```
<br>
Let's find out how many users have voted for a restaurant called 'Godzik' today.

- ##### Get the number of votes for the Restaurant
```
# GET /restaurants/{name}/vote
curl -i http://localhost:8080/topjava-graduation/restaurants/Godzik/vote --user Mr.Creeper:88005553535
```

We can also do this for all restaurants at once.
- ##### Get today's voting statistics
```
# GET /restaurants/statistics
curl -i http://localhost:8080/topjava-graduation/restaurants/statistics --user Mr.Creeper:88005553535
```
- ##### Get voting statistics from
```
# GET /restaurants/statistics?from={date}
curl -i http://localhost:8080/topjava-graduation/restaurants/statistics?from=2020-10-10 --user Mr.Creeper:88005553535
```
> Note: Returns only restaurants with today's menu (maybe empty). Think of it as a menu list. 

<br>
Let's use the privilege of the User role and change the current statistics.

## User role

- ##### Vote for the Restaurant
```
# POST /restaurants/{name}/vote
curl -i -X POST http://localhost:8080/topjava-graduation/restaurants/Godzik/vote --user Mr.Creeper:88005553535
```
> Note: The voting service is unavailable after 11:00.

## Administrator role

For the next steps you must have an admin role. The administrator provided his password for testing API. Let's use it.

- ##### Update User 
```
# PUT /users/id{id}
curl -i -X PUT http://localhost:8080/topjava-graduation/users/id3 -H 'Content-Type:application/json;charset=UTF-8' -d '
{
    "id":3,
    "name":"Unknown", 
    "password":"newPassword",
    "role":"ADMIN"
}' --user 1_Admin:admin
```

- #####Delete User
```
# DELETE /users/id{id}
curl -i -X DELETE http://localhost:8080/topjava-graduation/users/id3 --user 1_Admin:admin
```

Yes, the administrator can change the name, password and role of any user and delete the user profile. Don't anger the admin! :)
<br><br>
The main task of an administrator is to manage Restaurants, but this is only allowed until 10:00.
- ##### Add a Restaurant    
```
# POST /restaurants
curl -i -X POST http://localhost:8080/topjava-graduation/restaurants -H 'Content-Type:application/json;charset=UTF-8' -d '
{
    "name":"New Restaurant"
}' --user 1_Admin:admin
```
- ##### Add a Restaurant with today's menu
```
# POST /restaurants
curl -i -X POST http://localhost:8080/topjava-graduation/restaurants -H 'Content-Type:application/json;charset=UTF-8' -d '
{
       "name":"The Newest Restaurant",
       "menu":{"dishes": [
                {
             "name": "Chicken",
             "price": 10
          },
                {
             "name": "Fish",
             "price": 8
          },
                {
             "name": "Mice",
             "price": 7
          }
       ]}
}' --user 1_Admin:admin
```

- ##### Update Menu
```
# PUT /restaurants/{name}/menu
curl -i -X PUT http://localhost:8080/topjava-graduation/restaurants/Godzik/menu -H 'Content-Type:application/json;charset=UTF-8' -d '
{
"dishes":[{
	"id" : 221,
	"name": "Cola",
	"price": 2
},
{
	"name": "Draniki",
	"price": 8
},
{
	"name": "Rat",
	"price": 7
}]
}' --user 1_Admin:admin
```
> Note: you may set the dish ID if you want to edit the dish in today's menu (For example, correct a typo).

- ##### Delete Restaurant
```
# DELETE /restaurants/{name}
curl -i -X DELETE http://localhost:8080/topjava-graduation/restaurants/BurgerQueen --user 1_Admin:admin
```

- ##### Update Restaurant info ( rename :) )
```
# PUT /restaurants/{name}
curl -i -X PUT http://localhost:8080/topjava-graduation/restaurants/McDnlds -H 'Content-Type:application/json;charset=UTF-8' -d '
{
    "name":"KCF"
}' --user 1_Admin:admin
```

#
Finally, let's see what happens if you do something wrong. For example, register with a duplicate name:
```
# POST /users
curl -i -X POST http://localhost:8080/topjava-graduation/users -H 'Content-Type:application/json;charset=UTF-8' -d '
{
    "name":"1_Admin", 
    "password":"duplicatename"
}'
```
This is a short description of the error (ErrorInfo object): Request URL that failed, error type, error message and java exception class.
You might use that information to understand what went wrong.