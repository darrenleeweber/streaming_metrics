
[![Build Status](https://travis-ci.org/darrenleeweber/streaming_metrics.svg?branch=master)](https://travis-ci.org/darrenleeweber/streaming_metrics)

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
- Compute the following metrics and return a summary data structure from your API
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

Notes on streaming solutions:
 - the streaming application suggests the solution could benefit from using
   Kafka, possibly combined with Spark, although Kafka now has enough features for
   simple filter and map streams
 - the current solution does not make use of the Akka actor library; for simplicity
   in prototyping this solution, the stream is read and processed as a serial stream;
   use of Akka for concurrent, non-blocking processing could allow scaling and distributing
   the solution.
 - for file IO, see also http://jayconrod.com/posts/27/processing-large-files-in-scala
   - use a 64-bit JVM for larger virtual address space
   - to increase JVM memory for scala: `JAVA_OPTS=-Xmx4g scala ...`
   - use memory-mapped binary file IO (and consider little endian byte order)
   - use stream IO instead of List or Array

Notes on the solution:
 - generic numeric classes are not possible in scala, so the `NumericStats` trait does not
   calculate anything, that must be done in the numeric type-specific class `IntStats`
 - the favorite color counting requires storing a unique set of colors in Map keys, this
   could pose a problem for memory if the number of unique colors is very large; the
   current example seems to be in English and this solution assumes that scope
   - some colors are converted to lower-case and some spelling conversion is done
 - the median calculation is currently a complete median that requires a list of all values
   - TBD: streaming algorithm for median age, with constant memory footprint, e.g. see
   - https://stats.stackexchange.com/questions/346/what-is-a-good-algorithm-for-estimating-the-median-of-a-huge-read-once-data-set
   - https://stackoverflow.com/questions/4662292/scala-median-implementation
   - see notes below and code in `medianOptions.scala`

Notes on the code:
 - for prototype purposes an ease of command line use, all the code is in one script file
   - to solidify the code for maintenance, traits and classes should be broken out
   - unit tests are required for maintenance of the code in a production setting
   - the code is easily navigated in a decent IDE

```bash
# scala userMetrics.scala users.csv # it accepts a file arg
cat users.csv | scala userMetrics.scala 
{"users": {
    "age": {
      "count": 1000,
      "min": 1,
      "max": 80,
      "mean": 41.728
      "median": 42.0
    },
    "colors": [ "red", "gray", "purple", "grape", "charcoal" ]
},"adults": {
    "age": {
      "count": 760,
      "min": 22,
      "max": 80,
      "mean": 51.357894736842105
      "median": 51.0
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
  - this question prompted some refactoring to create generics traits (interfaces) like `Acummulator[T]`,
    `Acummulators[T]`, `StringCounter` and `NumericStats`.  Also the `IntStats` is used for age.
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
  - issues:
    - any unique fields, like VIN, can be a concern for the memory requirements of this problem; it
      depends on how they are accumulated.  If any accumulator keeps VIN values in a Map key or stores
      them all somehow, it could exceed memory limits.  At the expense of speed, some non-volatile
      data persistence solution may be required (e.g. Kafka).
- How would communicate errors with potentially malformed data from the IO source?
  - raise and handle exceptions (none have been coded in this exercise)
  - log informative (semantic) exception messages that identify problem records
  - apply log analysis tools to manage larger volumes of logs

## Median Calculations

Comparing results from a median and a "binned" median calculation (see `medianOptions.scala`).  The
binned-median rounds off values and tracks counts of values in a Map.  It has a `binInterval` parameter
to control the resolution of the bins, and thereby the accuracy of the results.  The aim of the
binned-median was to conserve memory, regardless of performance.

```
$ sbt
[info] Loading project definition from /data/src/streaming_metrics/project
[info] Loading settings from build.sbt ...
[info] Set current project to streaming_metrics (in build file:/data/src/streaming_metrics/)
[info] sbt server started at 127.0.0.1:4227
sbt:streaming_metrics> project median_options
[info] Set current project to median_options (in build file:/data/src/streaming_metrics/)
sbt:median_options> run
[info] Packaging /data/src/streaming_metrics/median_options/target/scala-2.12/median_options_2.12-0.1.0-SNAPSHOT.jar ...
[info] Done packaging.
[info] Running org.example.metrics.MedianOptions 
List(3, 13, 18, 20, 21, 23, 32, 40, 41, 43, 51, 51, 60, 66, 67, 70, 90, 95, 96, 96, 97)
51.0
50.0
[success] Total time: 1 s, completed Dec 23, 2017 8:03:18 PM
sbt:median_options> run
[info] Running org.example.metrics.MedianOptions 
List(1, 1, 1, 2, 7, 7, 29, 36, 42, 44, 48, 49, 60, 65, 66, 66, 67, 71, 81, 88, 89)
48.0
45.0
[success] Total time: 0 s, completed Dec 23, 2017 8:03:22 PM
sbt:median_options> run
[info] Running org.example.metrics.MedianOptions 
List(2, 6, 11, 21, 22, 27, 28, 33, 33, 33, 50, 61, 63, 70, 74, 79, 80, 83, 94, 95, 97)
50.0
50.0
[success] Total time: 1 s, completed Dec 23, 2017 8:04:11 PM
sbt:median_options> run
[info] Running org.example.metrics.MedianOptions 
List(1, 3, 14, 21, 25, 28, 38, 43, 44, 48, 52, 53, 58, 67, 70, 70, 79, 82, 83, 86, 97)
52.0
50.0
[success] Total time: 0 s, completed Dec 23, 2017 8:04:14 PM
sbt:median_options> run
[info] Running org.example.metrics.MedianOptions 
List(1, 6, 6, 10, 16, 20, 21, 24, 30, 32, 33, 47, 53, 53, 54, 58, 78, 81, 83, 94, 97)
33.0
30.0
[success] Total time: 0 s, completed Dec 23, 2017 8:04:16 PM
sbt:median_options> run
[info] Running org.example.metrics.MedianOptions 
List(5, 10, 37, 38, 42, 43, 45, 47, 48, 51, 53, 60, 61, 61, 70, 84, 87, 91, 92, 97, 97)
53.0
50.0
[success] Total time: 0 s, completed Dec 23, 2017 8:04:19 PM
```
