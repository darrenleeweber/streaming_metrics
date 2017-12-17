# Computation of Report Metrics

## Background:

 We would like to start building a general reporting framework to compute basic metrics, such
 as median or total records processed from an IO source.

 To make the problem more concrete, We are given a CSV file with a list of user data, specifically,
 the user id, age and favorite color. For these records, we would like to compute several metrics
 (defined below) and return a summary. In general, we don't know the file size, or the number of
 streamed records that will be provided (and it might not fit into memory).

**The solution should be either in python or scala**. You should restrict your solution to use on
  the **standard library of the language chosen**.

## Requirements

- Approximate constant memory usage for computation of reports
  (i.e., can't load the data all into memory at one time)
- Iterate exactly ONCE over the file/data
- Compute the following metrics and return a summary datastructure from your API
  - Total number of users processed
  - max age of all users
  - min age of all users
  - Mean age of all users
  - Median age of all users
  - Top 5 favorite colors
  - Total number of users processed with age greater than 21
  - max age of users with an age greater than 21
  - min age of users with an age greater than 21
  - Mean age of all users with age greater than 21
  - Median age of all users with age greater than 21
  - Top 5 favorite colors of user with age greater than 21

Reminder, the data/file can only be iterated over ONCE to compute all metrics and
memory consumption remain approximately constant.

Test data is provided in "users.csv". However, in general, the datasource could be streamed from
another source (database). We will use a range of different input files sizes (1MB to 10GB) to
test the submitted solution.

## Solution

The streaming process is interpreted as a unix pipe; the code reads from a STDIN source
and outputs to STDOUT sink.  The scala script returns valid JSON.
 - for prototype purposes an ease of command line use, all the code is in one script file
   - to solidify the code for maintenance, traits and classes should be broken out
   - unit tests are required for maintenance of the code in a production setting
   - the code is easily navigated as it is in a decent IDE
 - some colors are converted to lower-case and some spelling conversion is done
 - TBD: streaming algorithm for median age, with constant memory footprint

```
$ cat users.csv | scala userMetrics.scala 
{"users": {
    "age": {
      "count": 1000,
      "min": 1,
      "max": 80,
      "mean": 41.0
    },
    "colors": [ "red", "gray", "purple", "grape", "charcoal" ]
},"adults": {
    "age": {
      "count": 760,
      "min": 22,
      "max": 80,
      "mean": 51.0
    },
    "colors": [ "red", "gray", "purple", "grape", "charcoal" ]
}}
```


## Future Iteration and Discussion Points

- Could your API be extended to support searching over any field (e.g., user id, color) or fields?
  - this suggests that CLI options could be useful
  - depends what you mean by "searching"; more information is required to clearly define the requirements
  - if it is anything like the filtering based on age > 21, that's possible for other fields too
    - these filters are similar to the UserStats/AdultUserStats class that overrides the #update method
    - the filters could either create additional data *group-by* data or *select* only the filtered results
- What is your recommended solution to communicate intermediate summaries? For example, after every 10000 records.
  - See code comment in the UserMetrics.main() method
  - it could be enabled by a CLI option to provide the update interval
- How would we generalize this reporting API to other Record types, with different fields, such as a
  Car with VIN, brand, purchased at, etc...
  - current exercise:
    - has IO parsers to create `User` instances
    - has classes that extend the `Accumulator[User]` to manage stats accumulation and reporting
    - the `Accumulator[DATA]` interface includes #report, #toJSON, and #update methods
  - extensions:
    - create IO parsers to create `Car` instances
    - create classes that extend the `Accumulator[Car]` to manage stats accumulation and reporting
    - the Stats class could accept a list of any type conforming to `Accumulator[Car]`
    - create `carMetrics.main()` to work with `Car` data
- How would communicate errors with potentially malformed data from the IO source?
  - raise and handle exceptions (none have been coded in this exercise)
  - log informative (semantic) exception messages

