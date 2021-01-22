# VV_bigStringLoadTest
An example with Redis where keys are stored in a Set and thousands of Strings assigned those keys are then retrieved 

This is not a strong use-case for Redis technology.  Redis excels at discreet very low-latency operations that happen on the server-side.
This example fetches a ton of data from Redis to the client - the question is begging to be asked - why?

For reporting - it would be more logical to execute write-behind using RedisGears (if the data is mutated within Redis).
For TimeSeries Data - it makes much more sense to use the TimeSeries Module. which performs powerful aggregations.
The RedisSearch module allows for powerful Full-text search as well as some good aggregations of data...

Avoid moving data in large amounts across networks!!!   
