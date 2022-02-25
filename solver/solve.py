import sys, getopt, enum
import numpy as np
from PIL import Image

class RequiredOptions:
	def __init__(self, options=[]):
		self.required_options = options 
		
	def add(self, option):
		if option not in self.required_options:
			self.required_options.append(option)
			
	def resolve(self, option):
		if option in self.required_options:
			self.required_options.remove(option)
			
	def optionsResolved(self):
		if len(self.required_options):
			return False 
		else:
			return True
	
class CellConnections(enum.IntEnum):
	NONE	= 0 #0000
	UP 		= 1	#0001
	DOWN	= 2	#0010
	LEFT	= 4	#0100
	RIGHT = 8	#1000
	
class Maze:
	def __init__(self, rows, cols):
		self.rows = rows 
		self.cols = cols
		self.cells = [0] * int(rows*cols)
		
	def get_rows(self):
		return self.rows
		
	def get_cols(self):
		return self.cols 
		
	def get_cell(self, row, col):
		return self.cells[int(row*self.cols + col)]
		
	def get_absolute_index(row, col):
		return row*self.cols + col
		
	def get_row(self, index):
		return bound(index / self.cols, 0, self.rows)
		
	def get_col(self, index):
		return bound(index % self.cols, 0, self.cols)
		
	def get_size(self):
		return len(self.cells)
		
	def set_cell(self, cell, value):
		self.cells[cell] = value
		
	def print_maze(self):
		col = 0
		for i in range(len(self.cells)):
			if self.cells[i] == 0:
				print('#', end="")
			else:
				print(' ', end="")
			col = col + 1
			if(col == self.cols):
				print('\r')
				col = 0

def usage_and_exit():
	print("Usage: solve.py -i <inputfile>")
	sys.exit()
	
def CELL_SIZE():
	return int(8)
	
def bound(n, minimum, maximum):
	return int(max(minimum, min(maximum, n)))

def main(argv):
	inputfile = ""
	try:
		opts, args = getopt.getopt(argv,"hi:", ["ifile="])
	except getopt.GetoptError:
		usage_and_exit()
		
	required_options = RequiredOptions([ 'ifile' ])
	
	for opt, arg in opts:
		if opt in ("-h"):
			usage_and_exit()
		elif opt in ("-i", "--ifile"):
			inputfile = arg
			required_options.resolve('ifile')

	if not required_options.optionsResolved():
		usage_and_exit()
	
	try:
		with Image.open(inputfile) as img:
			img.load()
	except FileNotFoundError as err:
		print(str(err))
		usage_and_exit()
		
	#create memory structure for maze
	rows = img.height / CELL_SIZE()
	cols = img.width / CELL_SIZE()
	maze = Maze(rows, cols)
	
	for i in range(maze.get_size()):
		row = maze.get_row(i)
		col = maze.get_col(i)
		pixel_x = col * CELL_SIZE()
		pixel_y = row * CELL_SIZE()
		#print(img.getpixel((pixel_x, pixel_y)))
		if(img.getpixel((pixel_x, pixel_y)) != (0, 0, 255, 255)):
			if(img.getpixel((pixel_x, bound(pixel_y + CELL_SIZE(), 0, img.height - 1))) == (255, 255, 255, 255)):
				#connection above
				n = maze.get_cell(row, col) + CellConnections.UP
				maze.set_cell(i, n)
			if(img.getpixel((pixel_x, bound(pixel_y - CELL_SIZE(), 0, img.height - 1))) == (255, 255, 255, 255)):
				#connection below
				n = maze.get_cell(row, col) + CellConnections.DOWN 
				maze.set_cell(i, n)
			if(img.getpixel((bound(pixel_x - CELL_SIZE(), 0, img.width - 1), pixel_y)) == (255, 255, 255, 255)):
				#connection left 
				n = maze.get_cell(row, col) + CellConnections.LEFT 
				maze.set_cell(i, n)
			if(img.getpixel((bound(pixel_x + CELL_SIZE(), 0, img.width - 1), pixel_y)) == (255, 255, 255, 255)):
				#connection left 
				n = maze.get_cell(row, col) + CellConnections.LEFT 
				maze.set_cell(i, n)
				
	maze.print_maze()
	
if __name__ == "__main__":
	main(sys.argv[1:])

	
