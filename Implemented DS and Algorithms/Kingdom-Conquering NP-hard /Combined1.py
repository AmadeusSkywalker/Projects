# -*- coding: utf-8 -*-
"""
Created on Thu Apr 26 22:38:17 2018

@author: Kevin
"""
import os.path
import numpy as np
import networkx as nx
from tsp_solver.greedy import solve_tsp

def adjacency_matrix_to_graph(adjacency_matrix):
    node_weights = [adjacency_matrix[i][i] for i in range(len(adjacency_matrix))]
    adjacency_matrix_formatted = [[999999999 if entry == 'x' else float(entry) for entry in row] for row in adjacency_matrix]
    
    for i in range(len(adjacency_matrix_formatted)):
        adjacency_matrix_formatted[i][i] = 0
    
    G = nx.convert_matrix.from_numpy_matrix(np.matrix(adjacency_matrix_formatted))
    
    for node, datadict in G.nodes.items():
        assert node_weights[node] != 'x', 'The conquering cost of node number {} was specified to be x. Conquering costs cannot be x.'.format(node)
        datadict['weight'] = float(node_weights[node])
    
    F = G.copy()

    for e in G.edges(data=True):
        if e[2]["weight"] == 999999999:
            F.remove_edge(e[0],e[1])

    return F

def solve(G, n, s):
    flag = [False for i in range(n)]
    dp = [999999999 for i in range(n)]
    pred = [-1 for i in range(n)]
    q = [s]
    dp[s] = 0
    pred[s] = s
    ans = []
    while q:
        v = q.pop(0)
        flag[v] = False
        for i in range(n):
            dist = G[v][i]
            if G[v][i] == 0:
                dist = 999999999
            if dp[v] + dist < dp[i]:
                dp[i] = dp[v] + dist
                if flag[i] == False:
                    q.append(i)
                    flag[i] = True
                pred[i] = v
    
    for i in range(n):
        temp = i
        path = [temp]
        while True:
            path.insert(0,pred[temp])                
            temp = pred[temp]
            if(temp == pred[temp]):
                break
        ans.append([dp[i], path])
            
    return ans

def min_weighted_dominating_set(G, weight=None,post_process=True):
    if not G:
        raise ValueError("Expected non-empty NetworkX graph!")

    dom_set = set([])
    cost_func = dict((n, data.get("weight")) for n, data in G.nodes(data=True))
    vertices = set(G)
    vertex_depth=dict((node,0) for node in G)
    sets = dict((node, set([node]) | set(G[node])) for node in G)

    def _cost(node,subset):
        cost = cost_func[node]
        return cost / float(len(subset - dom_set))

    while vertices:
        dom_node, min_set = min(sets.items(),
                                key=lambda x: ( _cost(x[0],x[1]),x[0])) 
        dom_set.add(dom_node)
        del sets[dom_node]
        vertices = vertices - min_set
        for v in min_set: vertex_depth[v]=vertex_depth[v]+1
        
    if(post_process):
        redundancy = dict()
        for node in dom_set:
            redundancy[node] = -1+ min(vertex_depth[vv] for vv in (set([node]) | set(G[node])))
        (max_redun, redun_node) = max(((redundancy[node],node) for node in redundancy),
                                    key = lambda x: (x[0],cost_func[x[1]]))
        while max_redun > 0:
            dom_set= dom_set-set([redun_node])
            del redundancy[redun_node]
            for v in set([redun_node]) | set(G[redun_node]): vertex_depth[v]=vertex_depth[v]-1
            for node in dom_set:
                redundancy[node] = -1+ min(vertex_depth[vv] for vv in (set([node]) | set(G[node])))
            max_redun, redun_node = max(((redundancy[node],node) for node in redundancy),
                                        key = lambda x: (x[0],cost_func[x[1]])) 
            
    return dom_set

def outputTour(shortestpathsDict, setMatrix, mapping, startingnode):
    newStartingNode=0
    conquerpath = []
    newpath = []
    for element in mapping:  
        if element==startingnode:
            newStartingNode=element
    path=solve_tsp(setMatrix)
    startindex=0
    for i in path:     
        if path[i]==newStartingNode:
            startindex=i
    path=path[startindex:]+path[0:startindex]
    
    for i in range(len(path)): 
        conquerpath.append(mapping[path[i]])
    travelpath=[]
    for i in range(len(path)-1):
        current=mapping[path[i]]
        next=mapping[path[i+1]]
        newpath=shortestpathsDict[current][next][1]
        newpath.pop()
        travelpath.extend(newpath)
    startnode=conquerpath[0]  
    secondlast=conquerpath[len(conquerpath)-1]
    travelpath.extend(shortestpathsDict[secondlast][startnode][1])
    return travelpath,conquerpath

def main(file_name, cur_out_file):  

    "input _____________________________"
    f = open(file_name)
    g = open(cur_out_file, "w")
    n = int(f.readline())
    kdom_names = f.readline().lstrip().rstrip().split()
    kdom_start = f.readline().lstrip().rstrip()
    adj_mtx = []
    for i in range(n):
        line = f.readline().lstrip().rstrip().split()
        adj_mtx.append(line)
    "procesing all pair shortest path___________________________"
    T_G = [[0 for i in range(n)] for j in range(n)]
    for i in range(n):
        for j in range(n):
            if i == j:
                continue
            elif adj_mtx[i][j] == 'x':
                T_G[i][j] = 999999999
            else:
                T_G[i][j] = float(adj_mtx[i][j])
    
    graph_dict = []

    for i in range(n):
        ans = solve(T_G, n, i)
        graph_dict.append(ans)
    
    "Getting min_weight_dom_set__________________________________"
    G = adjacency_matrix_to_graph(adj_mtx)
    
    min_weight_dom_set = min_weighted_dominating_set(G)
    min_weight_dom_set = list(min_weight_dom_set)
    print(min_weight_dom_set)
    
    "Create adj matrix for TSP solver____________________________"
    if kdom_names.index(kdom_start) not in min_weight_dom_set:
        min_weight_dom_set.append(kdom_names.index(kdom_start) )
        
    m = len(min_weight_dom_set)
    adj_tsp = [[-1 for i in range(m)] for j in range(m)]
    mapping = [0 for i in range(m)]
    
    for i in range(m):
        v = min_weight_dom_set[i]
        mapping[i] = v
        for j in range(m):
            u = min_weight_dom_set[j]
            adj_tsp[i][j] = graph_dict[v][u][0]
    
    "print(adj_tsp)"
    starting_node_ind = kdom_names.index(kdom_start)
    mapped_s_n_ind = mapping.index(starting_node_ind)
    t_path, c_path = outputTour(graph_dict, adj_tsp, mapping, mapped_s_n_ind)
        
    t_path_named = []
    c_path_named = []
    for x in t_path:
        t_path_named.append(kdom_names[x])
    for x in c_path:
        c_path_named.append(kdom_names[x])
        
    for i in range(len(t_path_named)):
        if i == len(t_path_named)-1:
            g.write("%s\n" % t_path_named[i])
        else:
            g.write("%s " % t_path_named[i])
            
    for i in range(len(c_path_named)):
        if i == len(c_path_named)-1:
            g.write("%s" % c_path_named[i])
        else:
            g.write("%s " % c_path_named[i])
    g.close()
        
for i in range(7,8):
    cur_file_name = "inputs/" + str(i) + ".in"
    cur_out_file = "outputs1/" + str(i) + ".out"
    print(cur_file_name)
    print(cur_out_file)
    if os.path.exists(cur_file_name): 
        main(cur_file_name,cur_out_file)
