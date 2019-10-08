1. picocli to provide a command line interface
1. all parsing of input files is done via lazy reads of records via streams
1. core java to parse csv, glassfish implementation of javax json parser for json, and stax based parsing for xml
1. small amount of utilities from guava
1. no file needs to be held in memory in order for records to be parsed
1. however, by default, all records must be loaded into memory in order to be sorted
1. if the amount of records exceeds amount of available memory, there is an option to use external sort using an open source library
1. external sort has been tested to as being correct, performance test not included
1. tests are coarse grained, to save time
1. kept to java se for simplicity if truly dealing with large amounts of data, spark might be more natural choice
1. concrete instnaces of AccessRecord in json and cvs servcies would be preferable to the current anonymous classes, but that's an easy change for later
1. equals and hashcode won't work for concrete class used by xml service, but for now, that's ok too
1. java 1.8.0_202 and gradle 5.3.1
1. gradle clean jar && cd build/libs && java -jar ors-event-filter.jar --help