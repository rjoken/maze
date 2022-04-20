import enum
from helpful import *

class CellConnections(enum.IntEnum):
	# an IntEnum which keeps track of which directions are traversible from a cell 
	# it's bitwise, and that's cool. means we can find a dead end if the hamming weight is just 1
	UP 		= 1	 #0000001
	DOWN	= 2	 #0000010
	LEFT	= 4	 #0000100
	RIGHT = 8	 #0001000
	START = 16 #0010000
	END 	=	32 #0100000
	WALL 	=	64 #1000000
	
class Node:
	def __init__(self, index):
		self.index = index # the node's index in the maze cells array
		self.connections = [None] * 4 # an array of size 4 containing the cell indexes of the up, down, left, right nodes respectively
		self.distances = [None] * 4
		
	def get_index(self):
		return self.index

class Maze:
	def __init__(self, rows, cols):
		self.rows = rows 
		self.cols = cols
		# cool to keep track of
		self.dead_ends = 0 
		self.junctions = 0
		# stores the list of all cells in the maze as a value corresponding to CellConnections
		self.cells = [0] * int(rows*cols) 
		# 'nodes' stores all of the cells which act as the start, end, dead ends, or junctions
		# basically, cells which are significant. junctions are significant because a decision has to be made
		self.nodes = []
		self.junction_nodes = []
		
	def get_node_array_index(self, index):
		# returns the position of a node in the array self.nodes[]
		# the name is confusing, because 'index' has a double meaning
		for i in range(len(self.nodes)):
			if(self.nodes[i].index == index):
				return i
		return -1
		
	def get_node(self, index):
		# returns the node value according the given index in the self.nodes[] array
		for i in range(len(self.nodes)):
			if(self.nodes[i].index == index):
				return self.nodes[i]
		return None
		
	def get_junction_node_array_index(self, index):
		# returns the position of a node in the array self.junction_nodes[]
		for i in range(len(self.junction_nodes)):
			if(self.junction_nodes[i].index == index):
				return i
		return -1	
		
	def get_rows(self):
		# gets the number of rows, but i'm not implementing encapsulation anyway
		return int(self.rows)
		
	def get_cols(self):
		# same again but for cols
		return int(self.cols)
		
	def get_cell(self, row, col):
		# gets the cell based on the row and col
		return self.cells[int(row*self.cols + col)]
		
	def get_absolute_index(self, row, col):
		# gets the cell INDEX in the self.cells array based on the row and col
		return int(row*self.cols + col)
		
	def get_row(self, index):
		# gets the row based on the cell's absolute index
		return bound(index / self.cols, 0, self.rows)
		
	def get_col(self, index):
		# gets the col based on the cell's absolute index
		return bound(index % self.cols, 0, self.cols)
		
	def get_above(self, index):
		# returns the index of the cell above the given index
		return bound(index - self.cols, 0, len(self.cells))
		
	def get_below(self, index):
		# you
		return bound(index + self.cols, 0, len(self.cells))
		
	def get_left(self, index):
		# get
		return bound(index - 1, 0, len(self.cells))
		
	def get_right(self, index):
		# the idea
		return bound(index + 1, 0, len(self.cells))
		
	def get_size(self):
		# the size of the maze
		return len(self.cells)
		
	def set_cell(self, cell, value):
		# set the value of a cell in position [cell] of self.cells[]
		self.cells[cell] = value
		
	def get_dead_ends(self):
		# get the number of dead ends, which we count
		return self.dead_ends
		
	def get_junctions(self):
		# get number of junctions (3+ connections)
		return self.junctions
		
	def get_nodes(self):
		# gets the number of nodes
		return len(self.nodes)
		
	def get_traversible_cells(self):
		# gets the number of non-wall cells 
		count = 0
		for i in range(len(self.cells)):
			if(self.cells[i] & CellConnections.WALL == 0):
				count = count + 1
		return count
		
	def print_maze(self):
		col = 0
		for i in range(len(self.cells)):
			if(self.get_junction_node_array_index(i) != -1):
				print("X", end="")
			elif(self.get_node_array_index(i) != -1):
				print("O", end="")
			elif self.cells[i] == CellConnections.WALL:
				print("#", end="")
			else:
				print(" ", end="")
			col = col + 1
			if(col == self.cols):
				print('\r')
				col = 0
				
	def calc_nodes(self):
		for i in range(len(self.cells)):
			row = self.get_row(i)
			col = self.get_col(i)
			up_cell = self.get_above(i)
			down_cell = self.get_below(i)
			left_cell = self.get_left(i)
			right_cell = self.get_right(i)
			curr_cell = self.cells[i]
			n = Node(i)
			# need to identify cells where a decision must be made
			# i.e. there is both a horizontal and vertical connection 
			
			if n not in self.nodes:
				# if 3 or more connections, considered to be a "junction"
				if ((curr_cell & CellConnections.START == 0) and (curr_cell & CellConnections.END == 0) and (curr_cell & CellConnections.WALL == 0) and (hamming_weight(curr_cell) >= 3)):
					self.junctions = self.junctions + 1
					self.nodes.append(n)
					self.junction_nodes.append(n)

				# IT WOULD BE NICE IF PYTHON ALLOWED ME TO MAKE THIS MORE READABLE BY SPLITTING LINES, BUT NO CIGAR
				# if (in order) start cell, end cell, up & left, up & right, down & left, down & right
				elif ((curr_cell & CellConnections.START != 0) or (curr_cell & CellConnections.END != 0) or (curr_cell & CellConnections.UP != 0 and curr_cell & CellConnections.LEFT != 0) or (curr_cell & CellConnections.UP != 0 and curr_cell & CellConnections.RIGHT != 0) or (curr_cell & CellConnections.DOWN != 0 and curr_cell & CellConnections.LEFT != 0) or (curr_cell & CellConnections.DOWN != 0 and curr_cell & CellConnections.RIGHT != 0)):
					self.nodes.append(n)
				
				# if DEAD END (only one connection)
				elif ((curr_cell & CellConnections.START == 0) and (curr_cell & CellConnections.END == 0) and (curr_cell & CellConnections.WALL == 0) and (hamming_weight(curr_cell) == 1)):
					self.dead_ends = self.dead_ends + 1
					self.nodes.append(n)
				
				
				
	def calc_connections(self):
		for n in self.nodes:
			cell = n.get_index()
			row = self.get_row(cell)
			col = self.get_col(cell)
			
			# scan row until node 
			for i in range(col + 1, self.get_cols()):
				ind = self.get_absolute_index(row, i)
				if(self.cells[ind] & CellConnections.WALL != 0):
					break
				node_index = self.get_node_array_index(ind)
				if(node_index != -1):
					# add right connection from n to i
					n.connections[3] = ind
					n.distances[3] = i - col
					# add left connection from i to n 
					self.nodes[node_index].connections[2] = n.get_index()
					self.nodes[node_index].distances[2] = i - col
					break
			# scan col until node 
			for i in range(row + 1, self.get_rows()):
				ind = self.get_absolute_index(i, col)
				if(self.cells[ind] & CellConnections.WALL != 0):
					break
				node_index = self.get_node_array_index(ind)
				if(node_index != -1):
					# add down connection from n to i 
					n.connections[1] = ind
					n.distances[1] = i - row
					# add up connection from i to n 
					self.nodes[node_index].connections[0] = n.get_index()
					self.nodes[node_index].distances[0] = i - row
					break
					
	def print_connections(self):
		for n in self.nodes:
			print("node " + str(n.get_index()) + " is connected to:\r")
			print("UP:\t" + str(n.connections[0]) + "\tdistance: " + str(n.distances[0]))
			print("DOWN:\t" + str(n.connections[1]) + "\tdistance: " + str(n.distances[1]))
			print("LEFT:\t" + str(n.connections[2]) + "\tdistance: " + str(n.distances[2]))
			print("RIGHT:\t" + str(n.connections[3]) + "\tdistance: " + str(n.distances[3]) + "\n")
	
	
	