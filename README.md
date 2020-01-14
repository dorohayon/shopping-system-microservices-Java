# shopping-system-microservices-Java
The BookStoreRunner class is tasked with running the simulation. When started, it accept as
argument the name of the json input file to read, and the names of the four output
files- the first file is the output file for the customers HashMap, the second is for the books HashMap
object, the third is for the list of order receipts, and the fourth is for the MoneyRegister object.
The BookStoreRunner reads the input file (using Gson parsing). Next it creates the Inventory
object (adding the initial inventory to it), creates all the required objects, creates and initialize the MicroServices. When the number of passed ticks equal to duration, all Micro-Services unregisters
themselves and terminate gracefully. After all the Micro-Services terminate themselves, the
BookStoreRunner generate all the output files and exit.
