# Sorting-Competition-Materials-2022
Materials and results for the UMN Morris CSci 3501 sorting competition Fall 2022

# Table of contents
* [Goal of the competition](#goal)
* [The data](#data)
* [How is the data generated](#generating)
* [How do you need to sort the data](#sortingRules)
* [Setup for sorting](#setup)
* [Submision deadlines](#deadlines)
<!---
* [Scoring](#scoring)
* [System specs](#specs)
* [Results of the first preliminary round](#round1)
* [Results of the final competition](#final)
* [Presentations](#presentation)
--->


## Goal of the competition <a name="goal"></a>

The Sorting Competition is a multi-lab exercise on developing the fastest sorting algorithm for a given type of data. By "fast" we mean the actual running time and not the Big-Theta approximation. The solutions are developed in Java and will be ran on a single processor.

## The data  <a name="data"></a>

You are sorting numbers represented as decimal fractions and rational numbers. The numbers are given as strings. They are in the range [-6, 6] (non-inclusive) and may not fit into standard formats for integers and doubles. 

The comparu=ison rule is as follows:
* If two numbers are of a different value, they are compared by value. For instance, 1/3 > 0.33333. Note that the comparison is always exact: no precision is lost. 
* If they are of exactly the same value, the decimal representation is considered “smaller” than any fraction for positive numbers and "larger" for negatives. For instance, 1/2 > 0.5, but -1/2 < -0.5. 
* If two fractions have the exact same value then the one with the larger numerator is larger for positive numbers and smaller for negative. For instance, 1/3 < 3/9 and -1/3 > -3/9.

The file [Group0.java](src/Group0.java) provides a Comparator that implements this comparison and provides some tests. Please
consult it as needed. However, note that this is a slow implementation, and you should think of a way to make yours much faster. 

Once the data is sorted, it is written out to the output file, also one number per line, in the increasing order (according to the comparison given above). 
The file [example_out.txt](example_out.txt) has the results of sorting [example.txt](example.txt). 

The dataset will have between 10000 and 1000000 elements. Note that the program must sort correctly any data that could've possibly been generated by the data generator, even if the probability of it is very small. 

## Setup for sorting <a name="setup"></a>

The file [Group0.java](src/Group0.java) provides a template for the setup for your solution. Your class will be called `GroupN`, where `N` is the group number that is assigned to your group. The template class runs the sorting method once before the timing for the [JVM warmup](https://www.ibm.com/developerworks/library/j-jtp12214/index.html). It also pauses for 10ms before the actual test to let any leftover I/O or garbage collection to finish. Since the warmup and the actual sorting are done on the same array (for no reason other than simplicity), the array is cloned from the same input data. 

The data reading, the array cloning, the warmup sorting, and writing out the output are all outside of the timed portion of the method, and thus do not affect the total time. 

You may **not** use any **global variables** that **depend on your data**. You may, however, have global constants that are initialized to fixed values (no computation!) before the data is being read and stay the same throughout the run. These constants may be arrays of no more than 100 `long` numbers or equivalent amount of memory. For instance, if you are storing an array of objects that contain two `long` fields, you can only have 50 of them. 
We consider one `long` to be the same as two `int` numbers, so you can store an array of 200 `int` numbers. 
Strings use 16 bits (half of the size of an `int`) for *each* character and 16 bytes (128 bits) for the memory reference and other info for the string *itself*.  
If in doubt about specific cases, please discuss with me. 

The method in the [Group0.java](src/Group0.java) files that you may modify is the `sort` method. It must take the array of Integers. 
The method can sort in place (thus be `void`) or return type of the method can be what it is now or return the result in an array of another type.
If you are returning an array, the following rules have to be followed:
* Your `sort` method return type needs to be changed to whatever  array you are returning. Consequently you would need to change the call in `main` to store the resulting array. 
* Your return type has to be an array (not an array list!) and it has to have one element per element of the original array. That element must contain the integer value of the given element. 
For example, you may create your own class (I will call it `Data` as an example) that has the `int` value of a number plus some other fields. Then you will be returning an array of `Data`. 
You may not, however, create an array of just binary representations of all numbers, return that array, and convert back to decimal upon printing.   
* If you are returning a different type of an array, such as `Data`, you need to supply a method to write out your resulting array into a file. The method will access the `int` value field using a `get` method or directly (if it's accessible) and write it to a file. 
The file has to be exactly the same as in the prototype implementation; they will be compared using `diff` system command. 

If you are not changing the return type, you don't need to modify anything other than `sort` method and any methods/classes called from it. 

Even though you are not modifying anything other than the `sort` method, you still need to submit your entire class: copy the template, rename the Java class to your group number, and change the`sort` method. You may use supplementary classes, just don't forget to submit them. Make sure to add your names in comments when you submit. 

Your program must print **the only value**, which is the **time** (as it currently does). Any other printed output disqualifies your submission. If you use test prints, make sure to turn them off for submission. 

**Important:** if the sorting times may be too small to distinguish groups based on just one run of the sorting, so I may loop over the sorting section multiple times. If this is the case, I will let you know no later than a day after the preliminary competition and will modify `Group0` file accordingly.  

## Scoring <a name="scoring"></a>

The programs are tested on a few (between 1 and 3) data sets. For each data set each group's program is run three times, the median value counts. The groups are ordered by their median score for each data file and assigned places, from 1 to N. 

The final score is given by the sum of places for all data sets. If the sum of places is equal for two groups, the sum of median times for all the runs resolves the tie. So if one group was first for one data set and third for the other one (2 sets total being run), it scored better than a group that was third for the first data set and second for the other. However, if one group was first for the first set and third for the other one, and the second group was second in both, the sum of times determines which one of them won since the sum of places is the same (1 + 3 = 2 + 2). 

If a program has a compilation or a runtime error, doesn't sort correctly, or prints anything other than the total time in milliseconds, it gets a penalty of 1000000ms for that run. 

## System specs <a name="specs"></a>

The language used is Java that's installed in the CSci lab. It's ran on a single CPU core. 

I will post a script for running this program (with a correctness check and all), but for now a couple of things to know: run your program out of `/tmp` directory to avoid overhead of communications with the file server, and pin your program to a single core, i.e. run it like this:
``taskset -c 0 java GroupN``

##  Submision deadlines <a name="deadlines"></a>

The first preliminary competition will be run on Thursday October 13th in the lab. The purpose of it is mostly to check the correctness and to get a sense for your timing. 

The dates for subsequent competitions and the deadlines for other related assignments (code review and presentations) will be announced later. 
