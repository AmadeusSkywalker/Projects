from networkx import networkx as nx
import random
import numpy as np


w, h = 200, 200;
Matrix = [['x' for x in range(w)] for y in range(h)]
for x in range(200):
    for y in range(200):
        rannum=random.randint(0,10)
        if( x<=160 and y<=160 and (rannum>=9 or x == y) ):
           len = 1500000+random.randint(0,500000)
           Matrix[x][y]=len
           Matrix[y][x]=len
        elif (x > 160 and y > 160 and x == y):
           Matrix[x][y]=1000000+random.randint(0,500000)

tf = open("outfile.in","w")
tf.write("%s\n" % "200");
for x in range(200):
    tf.write("A%d " % (x+1) )
tf.write("\n")
tf.write("A161\n")

for x in range(40):
    rannum = 1300000+random.randint(0,500000)
    if(x != 39):
        Matrix[160+x][161+x] = rannum
        Matrix[161+x][160+x] = rannum
    else:
        Matrix[199][160] = rannum
        Matrix[160][199] = rannum

for x in range(160):
    nodes = random.randint(2,6)
    temp = [0 for x in range(nodes)]
    for y in range(nodes):
        temp[y] = random.randint(160,199)
    temp.sort()
    for y in range(nodes):
        ind = temp[y]
        if y == 0:
            rannum = 1000000 + random.randint(0, 500000)
            Matrix[ind][x] = rannum
            Matrix[x][ind] = rannum
        else:
            rannum = 2000000 + random.randint(0,450000)
            Matrix[ind][x] = rannum
            Matrix[x][ind] = rannum

for x in range(200):
    for y in range(200):
        tf.write("%s " % Matrix[x][y])
    tf.write("\n")


"""
with open('outfile.txt') as f:
    for line in Matrix:
        np.savetxt(f, line, fmt='%s')

G=nx.Graph()
G=nx.fast_gnp_random_graph(160,0.7,False)
for (u, v) in G.edges():
    G.edge[u][v]['weight'] = random.randint(0,10)
    print(G.edge[u][v]['weight'])
m=nx.attr_matrix(G)
print(m)
"""
