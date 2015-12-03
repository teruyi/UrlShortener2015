
## UrlShortenerControllerTest

Here we describe the tests made.

### Redirect

Key exists  | Is private | Token supplied | Token correct | Expected result
------------- | -------------| -------------| -------------| -------------
YES|NO|-|-| Redirects successfully
YES|YES|YES|YES| Redirects successfully
YES|YES|YES|NO| Error 401
YES|YES|NO|-| Error 401
NO|-|-|-| Error 404

### Shortener

Is private | URL OK | Web alive | Other | Expected result
------------- | -------------| -------------| ------------- | -------------
NO|YES|YES| -| 200 OK and created
NO|YES|NO|-| 400 Bad Request
NO|NO|-|-|400 Bad Request
YES|YES|YES|-| 200 OK and created
-|YES|YES|Repository fails|400 Bad request
