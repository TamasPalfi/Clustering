# Clustering
This project was written in Java in Fall 2018.

The main goal of this project is to implement an agglomerative clustering algorithm that will group Congressmen/Congresswoman based on their voting records and known party affiliation.  The way it works is that it takes in a CSV file of the data and also the desired number of clusters.  The code is run and algorithm is utilized which creates clusters based on the [Jaccard Index](https://en.wikipedia.org/wiki/Jaccard_index), or ( 1 - | A ^ B |/| A U B|), of the data and outputs it to stdout.  

