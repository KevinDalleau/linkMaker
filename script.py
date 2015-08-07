import sys 
import csv 

with open(sys.argv[1], 'r') as csvfile:
    csvreader = csv.reader(csvfile)
    csvwriter = csv.writer(sys.stdout)
    row = next(csvreader)
    fields = 4
    csvwriter.writerow(row)
    for row in csvreader:
        l = len(list(filter(str.strip, row)))
        if l < fields: continue
        csvwriter.writerow(row)

