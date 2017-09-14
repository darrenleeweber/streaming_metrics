# Computation of Report Metrics

## Background: 

We are given a CSV file with a list of user data, specifically, the user id, age and favorite color. We would like to generate a reporting mechanism to compute different metrics about the users. In general, we don't know the file size that will be provided (and it might not fit into memory).

## Requirements

- Approximate constant memory usage for computation of reports (i.e., can't load the data all into memory at one time)
- Iterate exactly ONCE over the file/data
- Compute the following metrics using scala and print a summary to stdout 
  - Total number of users processed
  - Mean age of all users
  - Median age of all users
  - Top 5 favorite colors
  - Total number of users processed with age greater than 21
  - Mean age of all users with age greater than 21
  - Median age of all users with age greater than 21
  - Top 5 favorite colors of user with age greater than 21
  

Reminder, the data/file can only be iterated over ONCE and memory consumption remain approximately constant.

Test data is provided in "users.csv"
  
