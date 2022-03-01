from fibheap import FibonacciHeap as FibHeap
from collections import deque

def dijkstra(maze, debug=False):
	infinity = float("inf")
	node_count = len(maze.nodes)
	start_node = maze.nodes[0]
	end_node = maze.nodes[node_count-1]
	# all speculative distances are infinity before we start searching
	distances = [infinity] * node_count
	# except for [0], S
	distances[0] = 0 
	# list of visited nodes - store their CELL (not node) index
	prev = [None] * node_count
	# bool corresponding to node index as to whether it has been visited
	visited = [False] * node_count
	# create a fibheap for dijkstra-ing (acts as priority queue)
	unvisited = FibHeap()
	# a copy of the nodes in the PQ so we can do inspections without popping
	nodes_in_queue = [None] * node_count
	# insert S into the PQ with distance as the weight (in this case 0)
	unvisited.insert(distances[0], start_node)
	# keeping a copy of the PQ
	nodes_in_queue[0] = start_node
		
	# while there are nodes in the PQ
	while unvisited.total_nodes > 0:
		# extract the node with the shortest distance in the PQ
		curr_node = unvisited.extract_min()
		curr = curr_node.value 
		curr_dist = curr_node.key 
		curr_index = maze.get_node_array_index(curr.index)
		if(debug):
			print("Exploring node: " + str(curr.index))
		# for each of the node's neighbours
		for i_nbr, nbr in enumerate(curr.connections):
			if(nbr != None):
				nbr_node = maze.get_node(nbr)
				nbr_index = maze.get_node_array_index(nbr)
				if(debug):
					print("Exploring neighbour of " + str(curr.index) + ": " + str(nbr_node.index))
				# if this neighbour has already not yet been visited...
				if(visited[nbr_index] == False):
					if(debug):
						print(str(nbr_node.index) + " is unvisited")
					# add the distance from the currently exploring node to this node by adding this distance 
					# to the distance already travelled to get to curr
					new_distance = int(distances[curr_index] + curr.distances[i_nbr])
					# if the new distance is less than the currently recorded distance to get to this node...
					if(new_distance < distances[nbr_index]):		
						new_node = nodes_in_queue[nbr_index]
						# if this neighbour is not already in the PQ, add it
						if(new_node == None):
							new_node = nbr_node
							unvisited.insert(new_distance, new_node)
							nodes_in_queue[nbr_index] = new_node 
							distances[nbr_index] = new_distance 
							prev[nbr_index] = curr.index
						else:
							# update the 'key' of this node in the PQ to the new distance, thus increasing its priority
							unvisited.decrease_key(new_distance, new_node)
							distances[nbr_index] = new_distance 
							prev[nbr_index] = curr.index
				else:
					if(debug):
						print(str(nbr_node.index) + " is visited")
		# this node has been explored and all of its neighbours have been updated/added to the PQ
		visited[curr_index] = True
		
	# reconstruct the path from E to S
	path = deque()
	current = maze.get_node_array_index(end_node.index)
	while(current != None):
		if(debug):
			print("current: " + str(current))
		if(current == maze.get_node_array_index(end_node.index)):
			current = end_node.index
			path.appendleft(current)
		else:
			path.appendleft(current)
		current = prev[maze.get_node_array_index(current)]
	if(debug):
		print(prev)
	return path