import sys, getopt, enum, time
import numpy as np
from PIL import Image
from helpful import *
from maze import *
from solver import *
	
def CELL_SIZE():
	return int(8)
	
def create_solved_image(img, path, inputfile, maze, debug):
	out_img = img.load()
	while path:
		cell = path.pop()
		try:
			to_cell = path[len(path) - 1]
		except:
			break
		cell_row = maze.get_row(cell) 
		cell_col = maze.get_col(cell)
		to_cell_row = maze.get_row(to_cell)
		to_cell_col = maze.get_col(to_cell)
		
		if(cell_row == to_cell_row):
			# same row: draw horizontal line 
			leftmost_cell = min(to_cell_col, cell_col)
			rightmost_cell = max(to_cell_col, cell_col)
			pixel_x = leftmost_cell * CELL_SIZE() + 2
			pixel_y = cell_row * CELL_SIZE()
			dist = (rightmost_cell - leftmost_cell) * CELL_SIZE() + 4
			if(debug):
				print("horz", leftmost_cell, rightmost_cell)
			for x in range(pixel_x, pixel_x + dist):
				out_img[x, pixel_y + 2] = (255, 0, 0, 255)
				out_img[x, pixel_y + 3] = (255, 0, 0, 255)
				out_img[x, pixel_y + 4] = (255, 0, 0, 255)
				out_img[x, pixel_y + 5] = (255, 0, 0, 255)
				
		if(cell_col == to_cell_col):
			# same col: draw vertical line 
			topmost_cell = min(to_cell_row, cell_row)
			bottommost_cell = max(to_cell_row, cell_row)
			dist = bottommost_cell - topmost_cell
			pixel_x = cell_col * CELL_SIZE()
			pixel_y = topmost_cell * CELL_SIZE() + 2
			dist = (bottommost_cell - topmost_cell) * CELL_SIZE() + 4
			if(debug):
				print("vert", topmost_cell, bottommost_cell)
			for x in range(pixel_y, pixel_y + dist):
				out_img[pixel_x + 2, x] = (255, 0, 0, 255)
				out_img[pixel_x + 3, x] = (255, 0, 0, 255)
				out_img[pixel_x + 4, x] = (255, 0, 0, 255)
				out_img[pixel_x + 5, x] = (255, 0, 0, 255)
				
	img.save(inputfile[:inputfile.rfind(".")] + "-solved.png")
	
def main(argv):
	debug = False
	output = False
	showmaze = True
	inputfile = "" # file path for input image
	outputfile = "" # file path for statistical output
	try:
		# possible arguments are -h (help, optional), -d (debug, optional), i- filename (input filename)
		opts, args = getopt.getopt(argv,"mhdi:o:", ["ifile=", "output="])
	except getopt.GetoptError:
		usage_and_exit()
		
	# the input file is a required command-line argument
	required_options = RequiredOptions([ 'ifile' ])
	
	for opt, arg in opts:
		if opt in ("-m"):
			showmaze = False
		if opt in ("-h"):
			usage_and_exit()
		if opt in ("-d"):
			debug = True
		if opt in ("-o", "--output"):
			outputfile = arg 
			output = True
		elif opt in ("-i", "--ifile"):
			inputfile = arg
			# mark the required option 'ifile' as resolved
			required_options.resolve('ifile')

	if not required_options.optionsResolved():
		# if not all of the required command-line arguments are resolved
		usage_and_exit()
	
	try:
		# try to open the specified file
		img = Image.open(inputfile)
	except FileNotFoundError as err:
		# if it fails, it probably doesnt exist (typo?)
		print(str(err))
		usage_and_exit()
		
	#create memory structure for maze
	rows = img.height / CELL_SIZE()
	cols = img.width / CELL_SIZE()
	maze = Maze(rows, cols)
	
	# create output file for statistics to be save if specified
	if(output):
		outfile = open(outputfile, "a")
		#outfile.write("Time to generate maze object,Time to find maze junctions,Time to calculate connections,Traversible cells,Nodes,Dead ends,Junctions,Time to calc best path,Time to create image,Total time\n")
	
	time_total_start = time.time()
	
	print("Generating maze object...")
	time1 = time.time()
	# loop over each cell in the maze
	for i in range(maze.get_size()):
		row = maze.get_row(i)
		col = maze.get_col(i)
		pixel_x = col * CELL_SIZE()
		pixel_y = row * CELL_SIZE()
		# if the top-left pixel for the cell currently traversed is NOT blue, we have a path
		# figure out in which directions we can traverse from this cell
		if(img.getpixel((pixel_x, pixel_y)) != (0, 0, 255, 255)):
			if(img.getpixel((pixel_x, bound(pixel_y + CELL_SIZE(), 0, img.height - 1))) == (255, 255, 255, 255)):
				# connection above
				n = maze.get_cell(row, col) + CellConnections.UP
				maze.set_cell(i, n)
			if(img.getpixel((pixel_x, bound(pixel_y - CELL_SIZE(), 0, img.height - 1))) == (255, 255, 255, 255)):
				# connection below
				n = maze.get_cell(row, col) + CellConnections.DOWN 
				maze.set_cell(i, n)
			if(img.getpixel((bound(pixel_x - CELL_SIZE(), 0, img.width - 1), pixel_y)) == (255, 255, 255, 255)):
				# connection left 
				n = maze.get_cell(row, col) + CellConnections.LEFT 
				maze.set_cell(i, n)
			if(img.getpixel((bound(pixel_x + CELL_SIZE(), 0, img.width - 1), pixel_y)) == (255, 255, 255, 255)):
				# connection right 
				n = maze.get_cell(row, col) + CellConnections.RIGHT 
				maze.set_cell(i, n)
			if(row == 0 and col == 1):
				# start cell
				n = maze.get_cell(row, col) + CellConnections.START - CellConnections.UP
				maze.set_cell(i, n)
			if(row == rows-1 and col == cols-2):
				# end cell 
				n = maze.get_cell(row, col) + CellConnections.END - CellConnections.DOWN
				maze.set_cell(i, n)
		else:
			# if it is blue, we have a wall
			maze.set_cell(i, CellConnections.WALL)
	time2 = time.time()
	time_diff = time2 - time1 
	print("Done in % f seconds\n" % time_diff)
	if(output):
		outfile.write(str(time_diff) + ",")
		
	print("Finding maze junctions...")
	time1 = time.time()
	maze.calc_nodes()
	time2 = time.time()
	time_diff = time2 - time1
	print("Done in % f seconds\n" % time_diff)
	if(output):
		outfile.write(str(time_diff) + ",")
	
	print("Calculating connections between junctions...")
	time1 = time.time()
	maze.calc_connections()
	time2 = time.time()
	time_diff = time2 - time1 
	print("Done in % f seconds\n" % time_diff)
	if(output):
		outfile.write(str(time_diff) + ",")
	
	print("Traversible cells: % s\n" % str(maze.get_traversible_cells()))
	if(output):
		outfile.write(str(maze.get_traversible_cells()) + ",")
	print("Nodes: % s\n" % str(maze.get_nodes()))
	if(output):
		outfile.write(str(maze.get_nodes()) + ",")
	print("Dead ends: % s\n" % str(maze.get_dead_ends()))
	if(output):
		outfile.write(str(maze.get_dead_ends()) + ",")
	print("Junctions: % s\n" % str(maze.get_junctions()))
	if(output):
		outfile.write(str(maze.get_junctions()) + ",")
	
	if(showmaze):
		maze.print_maze()
	
	print("\nCalculating best path...")
	time1 = time.time()
	dijkstra_path = dijkstra(maze, debug)
	time2 = time.time()
	time_diff = time2 - time1
	print("Done in % f seconds\n" % time_diff)
	if(output):
		outfile.write(str(time_diff) + ",")
	print("Dijkstra path: % s\n" % str(dijkstra_path))
	
	if(debug):
		maze.print_connections()
		
	print("Creating solved image...")
	time1 = time.time()
	create_solved_image(img, dijkstra_path, inputfile, maze, debug)
	time2 = time.time()
	time_diff = time2 - time1
	print("Done in % f seconds\n" % time_diff)
	if(output):
		outfile.write(str(time_diff) + ",")
	
	time_total_end = time.time()
	time_total_diff = time_total_end - time_total_start
	print("Total time: % f seconds\n" % time_total_diff)
	if(output):
		outfile.write(str(time_total_diff) + "\n")
		outfile.close()
	
if __name__ == "__main__":
	main(sys.argv[1:])

	
