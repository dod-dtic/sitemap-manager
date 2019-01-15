The application currently does not give responses which indicate to the requester that their request was invalid for the type of request that the application  receives. If certain fields are malformed or requests try to interact with resources that aren't there, the application may return either a 500 level or 200 level HTTP response code rather than the 400 level HTTP response code which would indicate that their request was invalid.

This also means that some of the tests which expect 400 level HTTP response codes are currently failing.
